package net.zcraft.materials;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.zcraft.blocks.Block;
import net.zcraft.items.ItemStack;
import org.jetbrains.annotations.ApiStatus;

@Getter
public class Material
{
    private int id;
    private int metadata;
    private String namespace;
    private String displayName;
    private MaterialType type;
    private int stackSize;

    // block fields
    @Setter private float hardness;
    @Setter private boolean diggable;
    @Setter private boolean transparent;

    private Block reference;

    public Material(int id, int metadata, String namespace, String displayName, MaterialType type, int stackSize)
    {
        this.id = id;
        this.metadata = metadata;
        this.namespace = namespace;
        this.displayName = displayName;
        this.type = type;
        this.stackSize = stackSize;

        if (this.isBlock())
            this.reference = new Block(this);
    }

    @ApiStatus.Internal
    public Material()
    {

    }

    public Material(JsonObject object)
    {
        this.type = MaterialType.valueOf(object.get("type").getAsString().toUpperCase());

        this.id = object.get("id").getAsInt();

        this.metadata = object.has("metadata") ? object.get("metadata").getAsInt() : 0;

        this.namespace = object.get("name").getAsString();
        this.displayName = object.get("displayName").getAsString();
        this.stackSize = object.get("stackSize").getAsInt();

        if (this.type == MaterialType.BLOCK)
        {
            // this.hardness = object.get("hardness").getAsFloat();
            this.diggable = object.get("diggable").getAsBoolean();
            this.transparent = object.get("transparent").getAsBoolean();
        }
    }

    public boolean isBlock()
    {
        return type == MaterialType.BLOCK;
    }

    public Block asBlock()
    {
        if (!this.isBlock())
            return null;
        return reference;
    }

    public @NonNull ItemStack asItemStack()
    {
        return asItemStack(1);
    }

    public @NonNull ItemStack asItemStack(int count)
    {
        return new ItemStack(this, count);
    }
}
