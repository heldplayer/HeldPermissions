
package me.heldplayer.permissions.command;

import java.util.List;

import me.heldplayer.permissions.Permissions;
import net.specialattack.bukkit.core.command.AbstractMultiCommand;
import net.specialattack.bukkit.core.command.AbstractSubCommand;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

public class InfoSubCommand extends AbstractSubCommand {

    private final String permission;

    public InfoSubCommand(AbstractMultiCommand command, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);

        this.permission = permissions;
    }

    @Override
    public void runCommand(CommandSender sender, String alias, String... args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Expected 1 parameter, no more, no less.");
            return;
        }

        Permission perm = Bukkit.getPluginManager().getPermission(args[0]);

        if (perm == null) {
            sender.sendMessage(Permissions.format("Unknown permission %s", ChatColor.RED, args[0]));
        }
        else {
            sender.sendMessage(Permissions.format("Info on permission %s:", ChatColor.GREEN, perm.getName()));
            sender.sendMessage(Permissions.format("Default: %s", ChatColor.GREEN, perm.getDefault()));
            if ((perm.getDescription() != null) && (perm.getDescription().length() > 0)) {
                sender.sendMessage(Permissions.format("Description: %s", ChatColor.GREEN, perm.getDescription()));
            }
            if ((perm.getChildren() != null) && (perm.getChildren().size() > 0)) {
                sender.sendMessage(Permissions.format("Children: %s", ChatColor.GREEN, perm.getChildren().size()));
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

        return sender.hasPermission(this.permission);
    }

    @Override
    public List<String> getTabCompleteResults(CommandSender sender, String alias, String... args) {
        return emptyTabResult;
    }

    @Override
    public String[] getHelpMessage() {
        return new String[] { this.name + " <permission>" };
    }

}
