package net.zcraft.entities;

import lombok.Getter;
import lombok.Setter;
import net.zcraft.ZCraftServer;
import net.zcraft.instance.Instance;
import net.zcraft.network.ZCraftConnection;
import net.zcraft.util.Gamemode;

import java.util.UUID;

@Getter
public class EntityPlayer extends Entity
{
    @Setter private String name;
    @Setter private ZCraftConnection connection;
    @Setter private UUID uuid;
    @Setter private Gamemode gamemode;
    @Setter private int viewDistance;


    public EntityPlayer(String name, ZCraftConnection connection)
    {
        super();
        this.name = name;
        this.connection = connection;

        this.gamemode = this.getInstance().defaultGamemode();
    }

    /*
        FIX THIS
     */
    public boolean isAuthenticated()
    {
        if (connection.has("authed"))
            return connection.get("authed");

        return ZCraftServer.getAuthManager().isAuthenticated(connection);
    }
}
