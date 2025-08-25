package net.zcraft.commands.impl;

import net.zcraft.commands.ICommand;
import net.zcraft.entities.EntityPlayer;
import net.zcraft.materials.Material;
import net.zcraft.materials.MaterialImpl;
import net.zcraft.materials.MaterialRegistry;
import net.zcraft.protocol.server.play.ServerSetSlot;

public class GiveCommand implements ICommand
{
    @Override
    public String[] getAliases()
    {
        return new String[]{"/give"};
    }

    @Override
    public void execute(String[] args, EntityPlayer player)
    {
        if (args.length == 1)
        {
            player.sendMessage("/give <item> <count>");

            return;
        }

        String item = args[1].toLowerCase();
        // int metadata = 0;
        int count = 1;

        if (args.length == 3)
        {
            count = Integer.parseInt(args[2]);
        }

        Material material = MaterialImpl.getRegistry().get(item);

        if (material == null)
        {
            player.sendMessage("Invalid item!");

            return;
        }

        player.sendPacket(new ServerSetSlot(material.asItemStack(), 0, 36));
    }
}
