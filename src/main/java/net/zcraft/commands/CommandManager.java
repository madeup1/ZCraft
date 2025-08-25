package net.zcraft.commands;

import net.zcraft.chat.ChatColor;
import net.zcraft.chat.Component;
import net.zcraft.commands.impl.GiveCommand;
import net.zcraft.entities.EntityPlayer;

import java.util.ArrayList;
import java.util.Arrays;

public class CommandManager
{
    private final ArrayList<ICommand> commands = new ArrayList<>();

    public CommandManager()
    {
        register(new GiveCommand());
    }

    public void register(ICommand... commands)
    {
        this.commands.addAll(Arrays.asList(commands));
    }

    public void execute(String command, EntityPlayer player)
    {
        String[] args = command.split(" ");

        this.commands
                .stream()
                .filter(c -> Arrays.stream(c.getAliases()).anyMatch(b -> b.equals(args[0])))
                .findFirst()
                .ifPresentOrElse(c -> {
                    c.execute(args, player);
                }, () -> {
                    // this is if none is found

                    player.sendMessage(Component.text("Unknown command. Try /help for a list of commands '" + args[0] + "'").color(ChatColor.red));
                });
    }
}
