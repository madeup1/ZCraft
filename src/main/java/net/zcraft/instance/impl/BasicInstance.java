package net.zcraft.instance.impl;

import net.zcraft.instance.Instance;
import net.zcraft.util.Difficulty;
import net.zcraft.util.Dimension;
import net.zcraft.util.Gamemode;
import net.zcraft.util.LevelType;


public class BasicInstance implements Instance
{
    private final Gamemode gamemode;
    private final Difficulty difficulty;
    private final Dimension dimension;
    private final LevelType levelType;

    public BasicInstance(Gamemode gamemode, Difficulty difficulty, Dimension dimension, LevelType levelType)
    {
        this.gamemode = gamemode;
        this.difficulty = difficulty;
        this.dimension = dimension;
        this.levelType = levelType;
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
}
