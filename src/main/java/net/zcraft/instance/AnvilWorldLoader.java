package net.zcraft.instance;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.zcraft.position.Vec2;

public class AnvilWorldLoader implements IWorldLoader
{
    private Object2ObjectMap<Vec2, Chunk> chunks = new Object2ObjectOpenHashMap<>();
    private Instance instance;

    private AnvilWorldLoader(Instance instance)
    {
        this.instance = instance;
    }

    @Override
    public Chunk getChunk(int x, int y)
    {
        if (this.isChunkLoaded(x, y))
            return chunks.get(new Vec2(x, y));

        this.chunks.put(new Vec2(x, y), this.loadChunk(x, y));

        return this.chunks.get(new Vec2(x, y));
    }

    @Override
    public void setChunk(int x, int y, Chunk chunk)
    {
        this.chunks.put(new Vec2(x, y), chunk);
    }

    @Override
    public Chunk loadChunk(int x, int y)
    {
        return null;
    }

    @Override
    public void unloadChunk(int x, int y)
    {

    }

    @Override
    public boolean isChunkLoaded(int x, int y)
    {
        return chunks.containsKey(new Vec2(x, y));
    }

    @Override
    public void load()
    {

    }
}
