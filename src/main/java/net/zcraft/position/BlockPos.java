package net.zcraft.position;

public record BlockPos(int x, int y, int z)
{
    public Vec3 asVec()
    {
        return new Vec3(x, y, z);
    }
}
