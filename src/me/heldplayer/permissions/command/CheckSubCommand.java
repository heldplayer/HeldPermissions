
package me.heldplayer.permissions.command;

import java.util.List;

import me.heldplayer.permissions.Permissions;
import net.specialattack.core.command.AbstractMultiCommand;
import net.specialattack.core.command.AbstractSubCommand;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CheckSubCommand extends AbstractSubCommand {

    private final String permission;

    public CheckSubCommand(AbstractMultiCommand command, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);

        this.permission = permissions;
    }

    @Override
    public void runCommand(CommandSender sender, String alias, String... args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Not enough parameters, expected at least 1");
            return;
        }

        String permission = args[0];

        if (args.length == 1) {
            sender.sendMessage(Permissions.format("%s has permission %s set to %s", ChatColor.GREEN, sender.getName(), permission, sender.hasPermission(permission)));
        }
        else {
            for (int i = 1; i < args.length; i++) {
                Player player = Bukkit.getPlayer(args[i]);

                if (player == null) {
                    sender.sendMessage(ChatColor.RED + "Player " + ChatColor.WHITE + args[i] + ChatColor.RED + " is not online right now.");
                }
                else {
                    sender.sendMessage(Permissions.format("%s has permission %s set to %s", ChatColor.GREEN, player.getName(), permission, player.hasPermission(permission)));
                }
            }
        }
    }

    @Override
    public boolean canUseCommand(CommandSender sender) {
        return true;
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        if (sender.hasPermission("permissions.command.*")) {
            return true;
        }

        return sender.hasPermission(permission);
    }

    @Override
    public List<String> getTabCompleteResults(CommandSender sender, String alias, String... args) {
        if (args.length == 1) {
            return emptyTabResult;
        }

        return null;
    }

}
