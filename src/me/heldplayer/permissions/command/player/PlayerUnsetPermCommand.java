package me.heldplayer.permissions.command.player;

import java.io.IOException;
import java.util.List;
import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.core.BasePermissions;
import me.heldplayer.permissions.core.PlayerPermissions;
import me.heldplayer.permissions.util.TabHelper;
import me.heldplayer.permissions.util.WorldlyPermission;
import net.specialattack.bukkit.core.command.AbstractSubCommand;
import net.specialattack.bukkit.core.command.ISubCommandHolder;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class PlayerUnsetPermCommand extends AbstractSubCommand {

    public PlayerUnsetPermCommand(ISubCommandHolder command, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);
    }

    @Override
    public void runCommand(CommandSender sender, String alias, String... args) {
        if (args.length != 2) {
            sender.sendMessage(Permissions.format("Expected %s parameters, no more, no less.", ChatColor.RED, 2));
            return;
        }

        String username = args[0];

        WorldlyPermission permission = new WorldlyPermission(args[1]);

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

        if (!permissions.allow.contains(permission.permission) && !permissions.deny.contains(permission.permission)) {
            sender.sendMessage(Permissions.format("The user does not have this permission set specifically", ChatColor.RED));
            return;
        }

        boolean changed = false;

        if (permissions.deny.contains(permission.permission)) {
            permissions.deny.remove(permission.permission);
            changed = true;
        }

        if (permissions.allow.contains(permission.permission)) {
            permissions.allow.remove(permission.permission);
            changed = true;
        }

        if (changed) {
            sender.sendMessage(Permissions.format("Unset %s from %s", ChatColor.GREEN, permission, username));

            try {
                Permissions.instance.savePermissions();
            } catch (IOException e) {
                sender.sendMessage(ChatColor.DARK_RED + "Applied the changes, but the changes didn't get saved!");
            }
        } else {
            sender.sendMessage(Permissions.format("The group does not have this permission set specifically", ChatColor.RED));
        }

        Permissions.instance.recalculatePermissions(username);
    }

    @Override
    public List<String> getTabCompleteResults(CommandSender sender, String alias, String... args) {
        if (args.length == 1) {
            return null;
        }

        if (args.length == 2) {
            String world = args[1].indexOf(':') < 0 ? "" : args[1].substring(0, args[1].indexOf(':'));
            PlayerPermissions permissions = Permissions.instance.getManager().getPlayer(args[0]);

            if (world.isEmpty()) {
                return TabHelper.tabSetPermission(args[1], permissions);
            } else {
                return TabHelper.tabSetPermission(args[1], permissions.getWorldPermissions(world));
            }
        }

        return emptyTabResult;
    }

    @Override
    public String[] getHelpMessage(CommandSender sender) {
        return new String[] { this.name + " <player> [world:]<permission>" };
    }

}
