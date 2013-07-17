
package me.heldplayer.permissions.command;

import java.util.List;
import java.util.logging.Level;

import me.heldplayer.permissions.Permissions;
import net.specialattack.core.command.AbstractMultiCommand;
import net.specialattack.core.command.AbstractSubCommand;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ReloadSubCommand extends AbstractSubCommand {

    private final String permission;

    public ReloadSubCommand(AbstractMultiCommand command, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);

        this.permission = permissions;
    }

    @Override
    public void runCommand(CommandSender sender, String alias, String... args) {
        try {
            Permissions.instance.reloadConfig();
            sender.sendMessage(ChatColor.GREEN + "Permissions reloaded!");
        }
        catch (Exception e) {
            Permissions.log.log(Level.SEVERE, "Error loading config", e);
            sender.sendMessage(ChatColor.RED + "There was a problem reloading the permissions. Please check the console for more information.");
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
        return emptyTabResult;
    }

}
