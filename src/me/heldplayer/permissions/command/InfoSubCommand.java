package me.heldplayer.permissions.command;

import java.util.List;
import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.util.TabHelper;
import net.specialattack.bukkit.core.command.AbstractMultiCommand;
import net.specialattack.bukkit.core.command.AbstractSubCommand;
import net.specialattack.bukkit.core.command.ISubCommandHolder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

public class InfoSubCommand extends AbstractSubCommand {

    public InfoSubCommand(ISubCommandHolder command, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);
    }

    @Override
    public void runCommand(CommandSender sender, String alias, String... args) {
        if (args.length != 1) {
            sender.sendMessage(Permissions.format("Expected %s parameter, no more, no less.", ChatColor.RED, 1));
            return;
        }

        Permission perm = Bukkit.getPluginManager().getPermission(args[0]);

        if (perm == null) {
            sender.sendMessage(Permissions.format("Unknown permission %s", ChatColor.RED, args[0]));
        } else {
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
    public List<String> getTabCompleteResults(CommandSender sender, String alias, String... args) {
        if (args.length == 1) {
            return TabHelper.tabAnyPermission(args[0]);
        }

        return emptyTabResult;
    }

    @Override
    public String[] getHelpMessage(CommandSender sender) {
        return new String[] { this.name + " <permission>" };
    }

}
