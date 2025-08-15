package net.zcraft.util;

public class NanoTimer
{
    private long start;

    public NanoTimer()
    {
        this.start = System.nanoTime();
    }

    public boolean hasTimePassed(long nano)
    {
        return System.nanoTime() - start >= nano;
    }

    public long elapsedNano()
    {
        return System.nanoTime() - start;
    }

    public float elapsedMs()
    {
        return elapsedNano() / 1_000_000F;
    }

    public void reset()
    {
        this.start = System.nanoTime();
    }
}
