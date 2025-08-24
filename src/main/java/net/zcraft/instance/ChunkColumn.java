package net.zcraft.instance;

import net.zcraft.blocks.Block;
import net.zcraft.position.BlockPos;

import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Function;

public class ChunkColumn
{
    private final Chunk[] chunks;

    public ChunkColumn()
    {
        chunks = new Chunk[16];

        Arrays.fill(chunks, null);
    }

    public Block getBlock(BlockPos pos)
    {
        return this.getBlock(pos.x(), pos.y(), pos.z());
    }

    public Block getBlock(int x, int y, int z)
    {
        int yIndex = y >> 4;

        if (!this.isChunkLoaded(yIndex))
            return null;

        int localY = y & 15;
        Chunk chunk = this.getChunk(yIndex);

        return chunk.getBlock(x, localY, z);
    }

    public ChunkColumn computeIfAbsent(int index, Function<ChunkColumn, Chunk> mapper)
    {
        if (this.isChunkLoaded(index))
            return this;

        chunks[index] = mapper.apply(this);

        return this;
    }

    public void ifPresent(int index, Consumer<Chunk> consumer)
    {
        if (this.isChunkLoaded(index))
            consumer.accept(chunks[index]);
    }

    public void setChunk(int index, Chunk chunk)
    {
        chunks[index] = chunk;
    }

    public Chunk getChunk(int index)
    {
        if (chunks[index] == null)
            chunks[index] = new ChunkImpl();

        return chunks[index];
    }

    public boolean isChunkLoaded(int index)
    {
        return (chunks[index] != null);
    }

    public boolean isFullLoaded()
    {
        for (int i = 0; i < 16; i++)
            if (chunks[i] == null) return false;

        return true;
    }
}
