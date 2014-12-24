package me.heldplayer.permissions.command.group;

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

public class GroupSetPermCommand extends AbstractSubCommand {

    public GroupSetPermCommand(ISubCommandHolder command, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);
    }

    @Override
    public void runCommand(CommandSender sender, String alias, String... args) {
        if (args.length != 3) {
            sender.sendMessage(Permissions.format("Expected %s parameters, no more, no less.", ChatColor.RED, 3));
            return;
        }

        String group = args[0];

        WorldlyPermission permission = new WorldlyPermission(args[1]);

        boolean bool = Boolean.valueOf(args[2]);

        BasePermissions permissions;

        if (permission.world != null) {
            permissions = Permissions.instance.getPermissionsManager().getGroup(group).getWorldPermissions(permission.world);
        } else {
            permissions = Permissions.instance.getPermissionsManager().getGroup(group);
        }

        if (bool) {
            permissions.allow.add(permission.permission);

            if (permissions.deny.contains(permission.permission)) {
                permissions.deny.remove(permission.permission);
            }

            sender.sendMessage(Permissions.format("Set %s for %s to %s", ChatColor.GREEN, permission, group, "true"));

            try {
                Permissions.instance.savePermissions();
            } catch (IOException e) {
                sender.sendMessage(ChatColor.DARK_RED + "Applied the changes, but the changes didn't get saved!");
            }

            Permissions.instance.recalculatePermissions();
        } else {
            permissions.deny.add(permission.permission);

            if (permissions.allow.contains(permission.permission)) {
                permissions.allow.remove(permission.permission);
            }

            sender.sendMessage(Permissions.format("Set %s for %s to %s", ChatColor.GREEN, permission, group, "false"));

            try {
                Permissions.instance.savePermissions();
            } catch (IOException e) {
                sender.sendMessage(ChatColor.DARK_RED + "Applied the changes, but the changes didn't get saved!");
            }

            Permissions.instance.recalculatePermissions();
        }
    }

    @Override
    public List<String> getTabCompleteResults(CommandSender sender, String alias, String... args) {
        if (args.length == 1) {
            return TabHelper.tabAnyGroup();
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
        return new String[] { this.name + " <group> [world:]<permission> <true/false>" };
    }

}
