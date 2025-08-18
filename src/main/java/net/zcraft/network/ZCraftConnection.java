package net.zcraft.network;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.zcraft.ZCraftServer;
import net.zcraft.entities.EntityPlayer;
import net.zcraft.init.ZCraftSettings;
import net.zcraft.network.buffers.ReadBuffer;
import net.zcraft.network.buffers.WriteBuffer;
import net.zcraft.protocol.IClientPacket;
import net.zcraft.protocol.IServerPacket;
import net.zcraft.protocol.PacketMode;
import net.zcraft.protocol.server.status.ServerStatusResponse;
import net.zcraft.util.CipherPair;
import net.zcraft.util.ConnectionUtils;
import net.zcraft.util.Flags;
import net.zcraft.util.ZlibUtils;
import org.tinylog.Logger;

import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

import static net.zcraft.network.buffers.Types.*;

@Getter
public class ZCraftConnection
{
    private final Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    @Setter private volatile int compressionThreshold = -1;
    @Setter private volatile PacketMode packetMode;

    @Getter @Setter private volatile EntityPlayer player;

    private static final WriteBuffer REUSE_BUFFER = new WriteBuffer();

    @Getter @Setter private volatile CipherPair cipher;

    // flag storage mainly?
    private static final Map<String, Object> flags = new HashMap<>();

    @SneakyThrows
    public ZCraftConnection(Socket socket)
    {
        this.socket = socket;
        this.packetMode = PacketMode.Handshake;

        System.out.println("PacketMode: " + packetMode);

        try
        {
            this.inputStream = socket.getInputStream();
            this.outputStream = socket.getOutputStream();

            this.socket.setTcpNoDelay(true);
            this.socket.setReceiveBufferSize(4096);
            this.socket.setSendBufferSize(4096);
            this.socket.setSoTimeout(0);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

        ZCraftServer.getExecutor().runVirtual(() -> {
            try
            {
                this.listen();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            Logger.info("Disconnecting socket");

            ZCraftServer.getConnections().remove(this);
            try
            {
                socket.close();
            }
            catch (IOException ex)
            {
                throw new RuntimeException(ex);
            }
        });
    }

    private void sendPacket(IServerPacket packet, WriteBuffer buffer)
    {
        buffer.write(VARINT, packet.getPacketId(packetMode));
        packet.write(buffer);

        byte[] data = buffer.getBytes();
        buffer.clear();

        boolean compressed = compressionThreshold != -1 && data.length >= compressionThreshold;
        int preCompressLength = data.length;

        if (compressed)
            data = ZlibUtils.compress(data);

        if (compressed) {
            buffer.write(VARINT, data.length + ConnectionUtils.getVarIntLength(preCompressLength));
            buffer.write(VARINT, preCompressLength);
        } else {
            if (compressionThreshold != -1) {
                buffer.write(VARINT, data.length + ConnectionUtils.getVarIntLength(0));
                buffer.write(VARINT, 0);
            } else {
                buffer.write(VARINT, data.length);
            }
        }

        buffer.UNSAFE_write(data);

        data = buffer.getBytes();

        try {
            this.outputStream.write(data);
            this.outputStream.flush();
        } catch (Exception e) {
            System.out.println("Failed to send packet");
        }
    }

    public void sendPackets(IServerPacket... packets)
    {
        for (IServerPacket packet : packets)
        {
            this.sendPacket(packet, REUSE_BUFFER);
            REUSE_BUFFER.clear();
        }
    }

    public void setCipher(CipherPair pair)
    {
        this.cipher = pair;
        this.outputStream = new CipherOutputStream(outputStream, cipher.getCipherOut());
        this.inputStream = new CipherInputStream(inputStream, cipher.getCipherIn());
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key)
    {
        return (T) flags.get(key);
    }

    public <T> void set(String key, T value)
    {
        flags.put(key, value);
    }

    public void remove(String key)
    {
        flags.remove(key);
    }

    public void sendPacket(IServerPacket packet)
    {
        this.sendPacket(packet, REUSE_BUFFER);

        REUSE_BUFFER.clear();
    }

    public void sendPacketVirtual(IServerPacket packet)
    {
        ZCraftServer.getExecutor().runVirtual(() -> {
            this.sendPacket(packet, new WriteBuffer());
        });
    }

    private void listen() throws Exception
    {
        while (!socket.isClosed())
        {
            //if (socket.isClosed() || !socket.isConnected())
            //    break;

            int packetLen = ConnectionUtils.readVarInt(inputStream);

            if (packetLen == -1)
            {
                Logger.debug("No more data from client");

                return;
            }

            int len = packetLen;
            boolean compressed = false;
            if (compressionThreshold != -1)
            {
                int dataLength = ConnectionUtils.readVarInt(inputStream);

                if (dataLength == 0)
                {
                    // Packet is uncompressed
                    len = packetLen - ConnectionUtils.getVarIntLength(dataLength);
                }
                else
                {
                    // Packet is compressed
                    len = dataLength;
                    compressed = true;
                }
            }

            byte[] data = inputStream.readNBytes(len);

            if (compressed)
                data = ZlibUtils.decompress(data);

            ReadBuffer buffer = new ReadBuffer(data);
            int packId = buffer.read(VARINT);

            Logger.debug("Found Packet ID: " + packId);
            Logger.debug("Packet Mode: " + packetMode);
            // match packet and read data
            IClientPacket packet = ZCraftServer
                    .getPacketManager()
                    .find(packetMode, packId);

            if (packet == null)
            {
                throw new IllegalArgumentException("Invalid packet. (" + packId + ")");
            }

            packet.read(buffer);

            if (packet.processInstantly())
            {
                packet.process(this);
            }
            else
            {
                ZCraftServer.getPacketManager().queue(new UnprocessedPacket(this, packet));
            }
        }
    }
}
