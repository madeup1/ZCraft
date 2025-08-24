package net.zcraft.instance;

public interface IWorldLoader
{
    Chunk getChunk(int x, int y);
    void setChunk(int x, int y, Chunk chunk);
    Chunk loadChunk(int x, int y);
    void unloadChunk(int x, int y);
    void load();
    boolean isChunkLoaded(int x, int y);
}
