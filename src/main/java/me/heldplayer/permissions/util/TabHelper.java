package me.heldplayer.permissions.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import me.heldplayer.permissions.core.BasePermissions;
import me.heldplayer.permissions.core.PermissionsManager;
import me.heldplayer.permissions.core.PlayerPermissions;
import me.heldplayer.permissions.core.added.AddedPermission;
import me.heldplayer.permissions.core.added.AddedPermissionsManager;
import net.specialattack.spacore.util.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

public final class TabHelper {

    private TabHelper() {
    }

    public static List<String> tabAnyPermissionWorldly(String input) {
        if (input.indexOf(':') >= 0) {
            String world = input.substring(0, input.indexOf(':'));
            String permission = input.indexOf(':') >= input.length() ? "" : input.substring(input.indexOf(':') + 1);
            permission = permission.toLowerCase(Locale.ENGLISH);

            Set<String> result = new TreeSet<>();

            for (Permission perm : Bukkit.getPluginManager().getPermissions()) {
                String name = perm.getName().toLowerCase(Locale.ENGLISH);
                if (permission.isEmpty()) { // Permission is empty, give a few broad categories
                    if (name.indexOf('.') < 0) {
                        result.add(world + ":" + name);
                    } else {
                        String sub = name.substring(0, name.indexOf('.') + 1);
                        result.add(world + ":" + sub);
                    }
                } else { // Permission has been started, go full in
                    if (name.startsWith(permission)) {
                        result.add(world + ":" + perm.getName());
                    }
                }
            }

            return new ArrayList<>(result);
        } else {
            List<String> possibles = new ArrayList<>();
            possibles.add(":");
            possibles.addAll(Bukkit.getWorlds().stream().map(world -> world.getName() + ":").collect(Collectors.toList()));

            return possibles;
        }
    }

    public static List<String> tabAnyPermission(String input) {
        Set<String> result = new TreeSet<>();

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

        return new ArrayList<>(result);
    }

    public static List<String> tabSetPermission(String input, BasePermissions perms) {
        if (input.indexOf(':') >= 0) {
            String world = input.substring(0, input.indexOf(':'));

            return perms.getDefinitions().stream()
                    .map(str -> world + ":" + str.permission)
                    .collect(Collectors.toList());
        } else {
            List<String> possibles = new ArrayList<>();
            possibles.add(":");
            possibles.addAll(Bukkit.getWorlds().stream().map(world -> world.getName() + ":").collect(Collectors.toList()));

            return possibles;
        }
    }

    public static List<String> tabAnyGroup(PermissionsManager manager) {
        return new ArrayList<>(manager.getAllGroupNames());
    }

    public static List<String> tabRankableGroup(PermissionsManager manager, CommandSender sender) {
        if (sender.isOp()) {
            return TabHelper.tabAnyGroup(manager);
        }
        if (sender instanceof Player) {
            return new ArrayList<>(manager.getPlayer((Player) sender).getRankableGroupNames());
        }

        return ChatUtil.TAB_RESULT_EMPTY;
    }

    public static List<String> tabAnyGroupExcept(PermissionsManager manager, PlayerPermissions player) {
        if (player == null) {
            return ChatUtil.TAB_RESULT_EMPTY;
        }

        List<String> result = new ArrayList<>(manager.getAllGroupNames());

        result.removeAll(player.getAllGroupNames());

        return result;
    }

    public static List<String> tabAnyGroupExcept(PermissionsManager manager, Collection<String> groupnames) {
        if (groupnames == null) {
            return ChatUtil.TAB_RESULT_EMPTY;
        }

        List<String> result = new ArrayList<>(manager.getAllGroupNames());

        result.removeAll(groupnames);

        return result;
    }

    public static List<String> tabAnyGroupExcept(PermissionsManager manager, Collection<String> groupnames, String... others) {
        if (groupnames == null) {
            return ChatUtil.TAB_RESULT_EMPTY;
        }

        List<String> result = new ArrayList<>(manager.getAllGroupNames());

        result.removeAll(groupnames);
        result.removeAll(Arrays.asList(others));

        return result;
    }

    public static List<String> tabAnyGroupIn(PlayerPermissions player) {
        if (player == null) {
            return ChatUtil.TAB_RESULT_EMPTY;
        }

        return new ArrayList<>(player.getGroupNames());
    }

    public static List<String> tabAnyAddedPermission(AddedPermissionsManager manager, String input) {
        Set<String> result = new TreeSet<>();

        for (AddedPermission permission : manager.addedPermissions) {
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

        return new ArrayList<>(result);
    }

    public static List<String> tabAnyAddedChild(AddedPermission parent, String input) {
        if (parent == null) {
            return Collections.emptyList();
        }

        Set<String> result = new TreeSet<>();

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

        return new ArrayList<>(result);
    }

    public static List<String> tabAnyChild(String input) {
        Set<String> result = new TreeSet<>();

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

        return new ArrayList<>(result);
    }
}
