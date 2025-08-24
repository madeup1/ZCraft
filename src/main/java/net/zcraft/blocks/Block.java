package net.zcraft.blocks;

import lombok.Getter;
import net.zcraft.materials.Material;
import net.zcraft.materials.MaterialImpl;
import net.zcraft.materials.Materials;

@Getter
public class Block
{
    private final Material material;
    private final int id;
    private final int metadata;

    public Block(Material material)
    {
        this.material = material;

        this.id = material.getId();
        this.metadata = material.getMetadata();
    }

    public Block(int id)
    {
        this(id, 0);
    }

    public Block(int id, int metadata)
    {
        material = MaterialImpl.getRegistry().get(id, metadata);

        this.id = id;
        this.metadata = metadata;
    }
}
