package net.zcraft.nbt;

import lombok.Getter;
import lombok.Setter;
import net.zcraft.nbt.impl.NbtByte;
import net.zcraft.nbt.impl.NbtCompound;
import net.zcraft.network.buffers.ReadBuffer;
import net.zcraft.network.buffers.WriteBuffer;

import java.util.function.Consumer;

@Setter
@Getter
public abstract class NbtTag<T>
{
    private T value;

    public NbtTag(T value)
    {
        this.value = value;
    }

    @SuppressWarnings("unchecked")
    public <B extends NbtTag<?>> B toType(Class<B> type)
    {
        if (type.isInstance(this))
            return (B) this;

        return null;
    }

    public <B extends NbtTag<?>> void ifIsType(Class<B> type, Consumer<B> consumer)
    {
        if (type.isInstance(this))
            consumer.accept(this.toType(type));
    }

    public NbtTag<T> toTag()
    {
        return this;
    }

    public abstract void read(ReadBuffer buf);
    public abstract void write(WriteBuffer buf);
    public abstract NbtTagType type();
}
