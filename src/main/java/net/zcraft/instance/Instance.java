package net.zcraft.instance;

import net.zcraft.util.Difficulty;
import net.zcraft.util.Dimension;
import net.zcraft.util.Gamemode;
import net.zcraft.util.LevelType;

public interface Instance
{
    Gamemode defaultGamemode();
    Difficulty difficulty();
    Dimension dimension();
    LevelType levelType();
}
