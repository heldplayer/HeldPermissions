package me.heldplayer.permissions.command.player;

import java.io.IOException;
import java.util.List;
import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.core.BasePermissions;
import me.heldplayer.permissions.util.TabHelper;
import me.heldplayer.permissions.util.WorldlyPermission;
import net.specialattack.bukkit.core.command.AbstractSubCommand;
import net.specialattack.bukkit.core.command.ISubCommandHolder;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class PlayerSetPermCommand extends AbstractSubCommand {

    public PlayerSetPermCommand(ISubCommandHolder command, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);
    }

    @Override
    public void runCommand(CommandSender sender, String alias, String... args) {
        if (args.length != 3) {
            sender.sendMessage(Permissions.format("Expected %s parameters, no more, no less.", ChatColor.RED, 3));
            return;
        }

        String username = args[0];

        WorldlyPermission permission = new WorldlyPermission(args[1]);

        boolean bool = Boolean.valueOf(args[2]);

        BasePermissions permissions;

        if (permission.world != null) {
            permissions = Permissions.instance.getManager().getPlayer(username).getWorldPermissions(permission.world);
        } else {
            permissions = Permissions.instance.getManager().getPlayer(username);
        }

        if (permissions == null) {
            sender.sendMessage(Permissions.format("Player %s does not exist", ChatColor.RED, username));

            return;
        }

        if (bool) {
            permissions.allow.add(permission.permission);

            if (permissions.deny.contains(permission.permission)) {
                permissions.deny.remove(permission.permission);
            }

            sender.sendMessage(Permissions.format("Set %s for %s to %s", ChatColor.GREEN, permission, username, "true"));
        } else {
            permissions.deny.add(permission.permission);

            if (permissions.allow.contains(permission.permission)) {
                permissions.allow.remove(permission.permission);
            }

            sender.sendMessage(Permissions.format("Set %s for %s to %s", ChatColor.GREEN, permission, username, "false"));
        }

        try {
            Permissions.instance.savePermissions();
        } catch (IOException e) {
            sender.sendMessage(ChatColor.DARK_RED + "Applied the changes, but the changes didn't get saved!");
        }

        Permissions.instance.recalculatePermissions(username);
    }

    @Override
    public List<String> getTabCompleteResults(CommandSender sender, String alias, String... args) {
        if (args.length == 1) {
            return null;
        }

        if (args.length == 2) {
            return TabHelper.tabAnyPermissionWorldly(args[1]);
        }

        if (args.length == 3) {
            return trueFalseResult;
        }

        return emptyTabResult;
    }

    @Override
    public String[] getHelpMessage(CommandSender sender) {
        return new String[] { this.name + " <player> [world:]<permission> <true/false>" };
    }

}
