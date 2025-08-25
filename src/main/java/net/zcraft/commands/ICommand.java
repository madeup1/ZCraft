package net.zcraft.commands;

import net.zcraft.entities.EntityPlayer;

public interface ICommand
{
    String[] getAliases();
    void execute(String[] args, EntityPlayer player);
}
