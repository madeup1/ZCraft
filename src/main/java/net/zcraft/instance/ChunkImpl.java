package net.zcraft.instance;

import net.zcraft.blocks.Block;

public class ChunkImpl implements Chunk
{
    private static final int CHUNK_SIZE = 16*16*16;
    private final short[] blocks;

    public ChunkImpl()
    {
        blocks = new short[CHUNK_SIZE];
    }

    private int getIndex(int x, int y, int z)
    {
        return x + (z * 16) + (y * 16 * 16);
    }

    @Override
    public Block getBlock(int x, int y, int z)
    {
        if (x >= 16 || x < 0 || y >= 16 || y < 0 || z >= 16 || z < 0)
            return null;

        int index = this.getIndex(x, y, z);

        short data = this.blocks[index];
        int id = data >> 4;
        int meta = data & 0xF;

        return new Block(id, meta);
    }

    @Override
    public void setBlock(int x, int y, int z, Block block)
    {
        this.setBlock(x, y, z, block.getId(), block.getMetadata());
    }

    @Override
    public void setBlock(int x, int y, int z, int id)
    {
        this.setBlock(x, y, z, id, 0);
    }

    @Override
    public void setBlock(int x, int y, int z, int id, int meta)
    {
        blocks[getIndex(x, y, z)] = (short) ((id << 4) | (meta & 0xF));
    }

    @Override
    public Block[] getBlocks()
    {
        return new Block[0];
    }

    @Override
    public short[] getRawBlocks()
    {
        return blocks;
    }

    @Override
    public int size()
    {
        return CHUNK_SIZE;
    }
}
