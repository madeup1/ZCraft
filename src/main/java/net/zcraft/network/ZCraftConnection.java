package net.zcraft.network;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.SneakyThrows;
import net.zcraft.ZCraftServer;
import net.zcraft.chat.Component;
import net.zcraft.entities.EntityPlayer;
import net.zcraft.init.ZCraftSettings;
import net.zcraft.network.buffers.ReadBuffer;
import net.zcraft.network.buffers.WriteBuffer;
import net.zcraft.protocol.IClientPacket;
import net.zcraft.protocol.IServerPacket;
import net.zcraft.protocol.PacketMode;
import net.zcraft.protocol.server.play.ServerDisconnect;
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

        boolean compressed = compressionThreshold != -1 && (data.length >= compressionThreshold);

        int bonusLength = (compressionThreshold == -1 ? 0 : 1);
        int preCompressLength = data.length;

        if (compressed)
        {
            Logger.debug("Compressing packet of size " + data.length);

            data = ZlibUtils.compress(data);
        }

        if (compressed)
        {
            int packetLen = data.length + ConnectionUtils.getVarIntLength(preCompressLength);

            buffer.write(VARINT, packetLen);
            buffer.write(VARINT, preCompressLength);

        }
        else
        {
            buffer.write(VARINT, data.length + bonusLength);
            if (compressionThreshold != -1)
                buffer.write(VARINT, 0);

            // Logger.debug("Compression Threshold: " + compressionThreshold);
            // Logger.debug("Header: ({}, {})", data.length + bonusLength, 0);
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
    public <T> T get(@NonNull String key)
    {
        return (T) flags.get(key);
    }

    public boolean has(@NonNull String key)
    {
        return flags.containsKey(key);
    }

    public <T> void set(@NonNull String key, @NonNull T value)
    {
        flags.put(key, value);
    }

    public void remove(@NonNull String key)
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

            // match packet and read data
            IClientPacket packet = ZCraftServer
                    .getPacketManager()
                    .find(packetMode, packId);

            if (packet == null)
            {
                Logger.debug("Found Packet ID: " + packId);
                Logger.debug("Packet Mode: " + packetMode);

                // this.sendPacket(new ServerDisconnect(Component.text("Invalid packet (C" + Integer.toString(packId, 16) + ")\n\nPacket not supported yet.")));

                // throw new IllegalArgumentException("Invalid packet. (C" + Integer.toString(packId, 16) + ")");

                continue;
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
