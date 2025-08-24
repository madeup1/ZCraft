package net.zcraft.instance;

import net.zcraft.blocks.Block;

public interface Chunk
{
    Block getBlock(int x, int y, int z);
    void setBlock(int x, int y, int z, Block block);
    void setBlock(int x, int y, int z, int id);
    void setBlock(int x, int y, int z, int id, int meta);
    Block[] getBlocks();
    short[] getRawBlocks();
    int size();
}
