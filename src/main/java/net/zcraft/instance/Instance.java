package net.zcraft.instance;

import net.zcraft.entities.Entity;
import net.zcraft.entities.EntityPlayer;
import net.zcraft.position.BlockPos;
import net.zcraft.protocol.IServerPacket;
import net.zcraft.util.Difficulty;
import net.zcraft.util.Dimension;
import net.zcraft.util.Gamemode;
import net.zcraft.util.LevelType;

import java.util.List;

public interface Instance
{
    Gamemode defaultGamemode();
    Difficulty difficulty();
    Dimension dimension();
    LevelType levelType();
    BlockPos spawnPosition();
    void addEntity(Entity entity);
    void addPlayer(EntityPlayer player);
    List<Entity> getEntities();
    List<EntityPlayer> getPlayers();

    default void broadcast(IServerPacket packet)
    {
        this.getPlayers()
                .forEach(c -> {
                    c.getConnection().sendPacketVirtual(packet);
                });
    }
}
