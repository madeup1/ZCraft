package net.zcraft.inventory;

import lombok.Getter;

public enum InventoryType
{
    Chest("minecraft:chest"),
    CraftingTable("minecraft:crafting_table"),
    Furnace("minecraft:furnace"),
    Dispenser("minecraft:dispenser"),
    EnchantingTable("minecraft:enchanting_table"),
    BrewingStand("minecraft:brewing_stand"),
    Villager("minecraft:villager"),
    Beacon("minecraft:beacon"),
    Anvil("minecraft:anvil"),
    Hopper("minecraft:hopper"),
    Dropper("minecraft:dropper"),
    EntityHorse("EntityHorse");

    @Getter private final String windowType;

    InventoryType(String windowType)
    {
        this.windowType = windowType;
    }
}
