package net.zcraft.instance.impl;

import net.zcraft.entities.Entity;
import net.zcraft.entities.EntityPlayer;
import net.zcraft.instance.Instance;
import net.zcraft.position.BlockPos;
import net.zcraft.protocol.IServerPacket;
import net.zcraft.util.Difficulty;
import net.zcraft.util.Dimension;
import net.zcraft.util.Gamemode;
import net.zcraft.util.LevelType;

import java.util.ArrayList;
import java.util.List;


public class BasicInstance implements Instance
{
    private final Gamemode gamemode;
    private final Difficulty difficulty;
    private final Dimension dimension;
    private final LevelType levelType;
    private final ArrayList<Entity> entityList;
    private final ArrayList<EntityPlayer> playerList;

    public BasicInstance(Gamemode gamemode, Difficulty difficulty, Dimension dimension, LevelType levelType)
    {
        this.gamemode = gamemode;
        this.difficulty = difficulty;
        this.dimension = dimension;
        this.levelType = levelType;

        this.entityList = new ArrayList<>();
        this.playerList = new ArrayList<>();
    }

    @Override
    public Gamemode defaultGamemode()
    {
        return gamemode;
    }

    @Override
    public Difficulty difficulty()
    {
        return difficulty;
    }

    @Override
    public Dimension dimension()
    {
        return dimension;
    }

    @Override
    public LevelType levelType()
    {
        return levelType;
    }

    @Override
    public BlockPos spawnPosition()
    {
        return new BlockPos(64, 70, 64);
    }

    @Override
    public void addEntity(Entity entity)
    {
        this.entityList.add(entity);
    }

    @Override
    public void addPlayer(EntityPlayer player)
    {
        this.playerList.add(player);
        this.addEntity(player);
    }

    @Override
    public List<Entity> getEntities()
    {
        return entityList;
    }

    @Override
    public List<EntityPlayer> getPlayers()
    {
        return playerList;
    }
}
