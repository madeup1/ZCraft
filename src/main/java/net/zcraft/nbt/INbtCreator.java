package net.zcraft.nbt;

public interface INbtCreator<T>
{
    NbtTag<T> create();
}
