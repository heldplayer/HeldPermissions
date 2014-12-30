package me.heldplayer.permissions.util;

import java.util.*;
import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.core.BasePermissions;
import me.heldplayer.permissions.core.PlayerPermissions;
import me.heldplayer.permissions.core.added.AddedPermission;
import net.specialattack.bukkit.core.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

public final class TabHelper {

    private TabHelper() {
    }

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
                    } else {
                        String sub = name.substring(0, name.indexOf('.') + 1);
                        result.add(world + ":" + sub);
                    }
                } else {
                    if (name.startsWith(permission)) {
                        result.add(world + ":" + name);
                    }
                }
            }

            return new ArrayList<String>(result);
        } else {
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
                } else {
                    String sub = name.substring(0, name.indexOf('.') + 1);
                    result.add(sub);
                }
            } else {
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
        } else {
            List<String> possibles = new ArrayList<String>();
            possibles.add(":");
            for (World world : Bukkit.getWorlds()) {
                possibles.add(world.getName() + ":");
            }

            return possibles;
        }
    }

    public static List<String> tabAnyGroup() {
        return new ArrayList<String>(Permissions.instance.getPermissionsManager().getAllGroupNames());
    }

    public static List<String> tabRankableGroup(CommandSender sender) {
        if (sender.isOp()) {
            return TabHelper.tabAnyGroup();
        }

        return new ArrayList<String>(Permissions.instance.getPermissionsManager().getPlayer(sender.getName()).getRankableGroupNames());
    }

    public static List<String> tabAnyGroupExcept(PlayerPermissions player) {
        if (player == null) {
            return net.specialattack.bukkit.core.util.Util.TAB_RESULT_EMPTY;
        }

        List<String> result = new ArrayList<String>(Permissions.instance.getPermissionsManager().getAllGroupNames());

        result.removeAll(player.getAllGroupNames());

        return result;
    }

    public static List<String> tabAnyGroupExcept(Collection<String> groupnames) {
        if (groupnames == null) {
            return Util.TAB_RESULT_EMPTY;
        }

        List<String> result = new ArrayList<String>(Permissions.instance.getPermissionsManager().getAllGroupNames());

        result.removeAll(groupnames);

        return result;
    }

    public static List<String> tabAnyGroupExcept(Collection<String> groupnames, String... others) {
        if (groupnames == null) {
            return Util.TAB_RESULT_EMPTY;
        }

        List<String> result = new ArrayList<String>(Permissions.instance.getPermissionsManager().getAllGroupNames());

        result.removeAll(groupnames);
        result.removeAll(Arrays.asList(others));

        return result;
    }

    public static List<String> tabAnyGroupIn(PlayerPermissions player) {
        if (player == null) {
            return Util.TAB_RESULT_EMPTY;
        }

        return new ArrayList<String>(player.getGroupNames());
    }

    public static List<String> tabAnyAddedPermission(String input) {
        Set<String> result = new TreeSet<String>();

        for (AddedPermission permission : Permissions.instance.getAddedPermissionsManager().addedPermissions) {
            String name = permission.name;
            if (input.isEmpty()) {
                if (name.indexOf('.') < 0) {
                    result.add(name);
                } else {
                    String sub = name.substring(0, name.indexOf('.') + 1);
                    result.add(sub);
                }
            } else {
                if (name.startsWith(input)) {
                    result.add(name);
                }
            }
        }

        return new ArrayList<String>(result);
    }

    public static List<String> tabAnyAddedChild(AddedPermission parent, String input) {
        if (parent == null) {
            return Collections.emptyList();
        }

        Set<String> result = new TreeSet<String>();

        for (String permission : parent.children) {
            if (input.isEmpty()) {
                if (permission.indexOf('.') < 0) {
                    result.add(permission);
                } else {
                    String sub = permission.substring(0, permission.indexOf('.') + 1);
                    result.add(sub);
                }
            } else {
                if (permission.startsWith(input)) {
                    result.add(permission);
                }
            }
        }

        return new ArrayList<String>(result);
    }

    public static List<String> tabAnyChild(String input) {
        Set<String> result = new TreeSet<String>();

        Permission perm = Bukkit.getPluginManager().getPermission(input);
        if (perm == null) {
            return Collections.emptyList();
        }

        for (String permission : perm.getChildren().keySet()) {
            if (input.isEmpty()) {
                if (permission.indexOf('.') < 0) {
                    result.add(permission);
                } else {
                    String sub = permission.substring(0, permission.indexOf('.') + 1);
                    result.add(sub);
                }
            } else {
                if (permission.startsWith(input)) {
                    result.add(permission);
                }
            }
        }

        return new ArrayList<String>(result);
    }

}
