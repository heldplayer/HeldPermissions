
package me.heldplayer.permissions.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.core.BasePermissions;
import me.heldplayer.permissions.core.PlayerPermissions;
import net.specialattack.bukkit.core.command.AbstractSubCommand;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

public final class TabHelper {

    private TabHelper() {}

    public static List<String> tabAnyPermissionWorldly(String input) {
        if (input.indexOf(':') >= 0) {
            String world = input.substring(0, input.indexOf(':'));
            String permission = input.indexOf(':') >= input.length() ? "" : input.substring(input.indexOf(':') + 1);

            Set<String> result = new TreeSet<String>();

            for (Permission perm : Bukkit.getPluginManager().getPermissions()) {
                String name = perm.getName();
                if (permission.isEmpty()) {
                    if (name.indexOf('.') < 0) {
                        result.add(world + ":" + name);
                    }
                    else {
                        String sub = name.substring(0, name.indexOf('.') + 1);
                        result.add(world + ":" + sub);
                    }
                }
                else {
                    if (name.startsWith(permission)) {
                        result.add(world + ":" + name);
                    }
                }
            }

            return new ArrayList<String>(result);
        }
        else {
            List<String> possibles = new ArrayList<String>();
            possibles.add(":");
            for (World world : Bukkit.getWorlds()) {
                possibles.add(world.getName() + ":");
            }

            return possibles;
        }
    }

    public static List<String> tabAnyPermission(String input) {
        Set<String> result = new TreeSet<String>();

        for (Permission perm : Bukkit.getPluginManager().getPermissions()) {
            String name = perm.getName();
            if (input.isEmpty()) {
                if (name.indexOf('.') < 0) {
                    result.add(name);
                }
                else {
                    String sub = name.substring(0, name.indexOf('.') + 1);
                    result.add(sub);
                }
            }
            else {
                if (name.startsWith(input)) {
                    result.add(name);
                }
            }
        }

        return new ArrayList<String>(result);
    }

    public static List<String> tabSetPermission(String input, BasePermissions perms) {
        if (input.indexOf(':') >= 0) {
            String world = input.substring(0, input.indexOf(':'));

            Set<String> result = new TreeSet<String>();

            for (String str : perms.allow) {
                result.add(world + ":" + str);
            }
            for (String str : perms.deny) {
                result.add(world + ":" + str);
            }
            return new ArrayList<String>(result);
        }
        else {
            List<String> possibles = new ArrayList<String>();
            possibles.add(":");
            for (World world : Bukkit.getWorlds()) {
                possibles.add(world.getName() + ":");
            }

            return possibles;
        }
    }

    public static List<String> tabAnyGroup() {
        return new ArrayList<String>(Permissions.instance.getManager().getAllGroupNames());
    }

    public static List<String> tabRankableGroup(CommandSender sender) {
        if (sender.isOp()) {
            return TabHelper.tabAnyGroup();
        }

        return new ArrayList<String>(Permissions.instance.getManager().getPlayer(sender.getName()).getRankableGroupNames());
    }

    public static List<String> tabAnyGroupExcept(PlayerPermissions player) {
        if (player == null) {
            return AbstractSubCommand.emptyTabResult;
        }

        List<String> result = new ArrayList<String>(Permissions.instance.getManager().getAllGroupNames());

        result.removeAll(player.getAllGroupNames());

        return result;
    }

    public static List<String> tabAnyGroupIn(PlayerPermissions player) {
        if (player == null) {
            return AbstractSubCommand.emptyTabResult;
        }

        return new ArrayList<String>(player.getGroupNames());
    }

}
