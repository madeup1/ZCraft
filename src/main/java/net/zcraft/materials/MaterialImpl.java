package net.zcraft.materials;

import lombok.Getter;

import java.io.File;

public class MaterialImpl
{
    private static final MaterialRegistry REGISTRY = new MaterialRegistry(new File("materials.json"), (c) -> {
        Material material = new Material(c);

        return material;
    });

    public static MaterialRegistry getRegistry()
    {
        return REGISTRY;
    }
}
