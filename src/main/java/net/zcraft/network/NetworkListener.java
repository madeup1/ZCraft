package net.zcraft.network;

import lombok.Getter;
import net.zcraft.ZCraftServer;
import org.tinylog.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class NetworkListener
{
    @Getter
    private int port;
    @Getter
    private ServerSocket socket;
    public NetworkListener(int port)
    {
        this.port = port;
    }

    public void start()
    {
        try
        {
            this.socket = new ServerSocket(this.port);
            while (true)
            {
                Socket socket = this.socket.accept();

                Logger.debug("Connection found! addr: ({})", socket.getRemoteSocketAddress());

                ZCraftServer
                        .getConnections()
                        .add(new ZCraftConnection(socket));
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
