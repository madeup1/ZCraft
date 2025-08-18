package net.zcraft.entities;

import lombok.Getter;
import lombok.Setter;
import net.zcraft.ZCraftServer;
import net.zcraft.network.ZCraftConnection;
import net.zcraft.util.Gamemode;

import java.util.UUID;

@Getter
public class EntityPlayer
{
    @Setter private String name;
    @Setter private ZCraftConnection connection;
    @Setter private UUID uuid;
    private final int entityId;
    @Setter private Gamemode gamemode;


    public EntityPlayer(String name, ZCraftConnection connection)
    {
        this.name = name;
        this.connection = connection;

        this.entityId = ZCraftServer.getInstanceManager().getEntityId();
    }

    /*
        FIX THIS
     */
    public boolean isAuthenticated()
    {
        if (connection.get("authed"))
            return true;



        return true;
    }
}
