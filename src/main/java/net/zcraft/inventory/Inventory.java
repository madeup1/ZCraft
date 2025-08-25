package net.zcraft.inventory;

import lombok.Getter;
import net.zcraft.chat.Component;
import net.zcraft.entities.EntityPlayer;
import net.zcraft.util.WindowUtils;

import java.util.List;

@Getter
public abstract class Inventory
{
    private final byte windowId;
    private final InventoryType inventoryType;
    private Component title;
    private final int offset;

    public Inventory(InventoryType type, Component title)
    {
        this.windowId = WindowUtils.nextWindowId();
        this.inventoryType = type;
        this.title = title;
        this.offset = this.getSize();
    }

    public void setTitle(Component component, EntityPlayer player)
    {
        this.title = component;

        this.open(player);
    }

    public void open(EntityPlayer player)
    {

    }

    public boolean leftClick(EntityPlayer player, int slot)
    {

    }

    public boolean rightClick(EntityPlayer player, int slot)
    {

    }

    public boolean shiftClick(EntityPlayer player, int slot, int button)
    {

    }

    public boolean drop(EntityPlayer player, boolean all, int slot)
    {

    }

    public boolean dragging(EntityPlayer player, List<Integer> slots, int button)
    {
        
    }

    public abstract int getSize();
}
