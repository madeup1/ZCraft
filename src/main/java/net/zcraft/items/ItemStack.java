package net.zcraft.items;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.zcraft.materials.Material;
import org.jetbrains.annotations.NotNull;

public class ItemStack
{
    @Getter @NonNull private final Material material;
    @Getter @Setter private int count;

    public ItemStack(@NotNull Material material, int count)
    {
        this.material = material;
        this.count = count;
    }

    public int getItemId()
    {
        return material.getId();
    }

    public int getMetadata()
    {
        return material.getMetadata();
    }
}
