
package me.heldplayer.permissions.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.core.BasePermissions;
import me.heldplayer.permissions.core.GroupPermissions;
import me.heldplayer.permissions.core.PlayerPermissions;
import net.specialattack.bukkit.core.command.AbstractMultiCommand;
import net.specialattack.bukkit.core.command.AbstractSubCommand;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

public class PlayerSubCommand extends AbstractSubCommand {

    private final String permission;
    private final List<String> primPossibles;

    public PlayerSubCommand(AbstractMultiCommand command, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);

        this.permission = permissions;
        this.primPossibles = Collections.unmodifiableList(Arrays.asList("groups", "setgroup", "addgroup", "removegroup", "setperm", "unsetperm"));
    }

    @Override
    public void runCommand(CommandSender sender, String alias, String... args) {
        if (args.length == 0) {
            sender.sendMessage(Permissions.format("Expected at least %s parameter.", ChatColor.RED, 1));
            return;
        }

        if (args[0].equalsIgnoreCase("groups")) {
            if (args.length != 2) {
                sender.sendMessage(Permissions.format("Expected %s parameters, no more, no less.", ChatColor.RED, 2));
                return;
            }

            String username = args[1].toLowerCase();

            PlayerPermissions permissions = Permissions.instance.getManager().getPlayer(username);

            if (permissions == null) {
                sender.sendMessage(Permissions.format("Player %s does not exist", ChatColor.RED, args[0]));

                return;
            }

            Collection<String> groups = permissions.getGroupNames();
            Collection<String> subGroups = permissions.getAllGroupNames();
            subGroups.removeAll(groups);

            String message = "Groups: ";

            for (int i = 0; i < groups.size(); i++) {
                if (i != 0) {
                    message += ", ";
                }
                message += "%s";
            }

            sender.sendMessage(Permissions.format(message, ChatColor.GREEN, groups.toArray()));

            if (subGroups.size() > 0) {
                message = "Sub-groups: ";

                for (int i = 0; i < subGroups.size(); i++) {
                    if (i != 0) {
                        message += ", ";
                    }
                    message += "%s";
                }

                sender.sendMessage(Permissions.format(message, ChatColor.GREEN, subGroups.toArray()));
            }
        }
        if (args[0].equalsIgnoreCase("setgroup")) {
            if (args.length < 3) {
                sender.sendMessage(Permissions.format("Expected %s parameters or more.", ChatColor.RED, 3));
                return;
            }

            String username = args[1].toLowerCase();

            PlayerPermissions permissions = Permissions.instance.getManager().getPlayer(username);

            if (permissions == null) {
                sender.sendMessage(Permissions.format("Player %s does not exist", ChatColor.RED, args[0]));

                return;
            }

            Set<GroupPermissions> groups = new TreeSet<GroupPermissions>();
            Set<String> groupNames = new TreeSet<String>();

            String message = "New groups: %s";

            GroupPermissions group = Permissions.instance.getManager().getGroup(args[2]);
            if (group == null) {
                sender.sendMessage(Permissions.format("Unknown group %s", ChatColor.RED, args[2]));
                return;
            }
            groups.add(group);
            groupNames.add(args[2]);

            for (int i = 3; i < args.length; i++) {
                message += ", %s";
                group = Permissions.instance.getManager().getGroup(args[i]);
                if (group == null) {
                    sender.sendMessage(Permissions.format("Unknown group %s", ChatColor.RED, args[i]));
                    return;
                }
                groups.add(group);
                groupNames.add(args[i]);
            }

            permissions.setGroups(groups);

            sender.sendMessage(Permissions.format(message, ChatColor.GREEN, groupNames.toArray()));

            try {
                Permissions.instance.savePermissions();
            }
            catch (IOException e) {
                sender.sendMessage(ChatColor.DARK_RED + "Applied the changes, but the changes didn't get saved!");
            }

            Permissions.instance.recalculatePermissions(username);
        }
        if (args[0].equalsIgnoreCase("addgroup")) {
            if (args.length < 3) {
                sender.sendMessage(Permissions.format("Expected %s parameters or more.", ChatColor.RED, 3));
                return;
            }

            String username = args[1].toLowerCase();

            PlayerPermissions permissions = Permissions.instance.getManager().getPlayer(username);

            if (permissions == null) {
                sender.sendMessage(Permissions.format("Player %s does not exist", ChatColor.RED, args[0]));

                return;
            }

            Set<GroupPermissions> groups = new TreeSet<GroupPermissions>(permissions.getGroups());
            Set<String> added = new TreeSet<String>();

            String message = "Added groups: ";

            boolean changed = false;

            for (int i = 2; i < args.length; i++) {
                GroupPermissions group = Permissions.instance.getManager().getGroup(args[i]);
                if (group == null) {
                    sender.sendMessage(Permissions.format("Unknown group %s", ChatColor.RED, args[i]));
                    return;
                }
                if (!groups.contains(group)) {
                    groups.add(group);
                    added.add(args[i]);

                    if (changed) {
                        message += ", ";
                    }
                    message += "%s";

                    changed = true;
                }
            }

            if (changed) {
                permissions.setGroups(groups);

                sender.sendMessage(Permissions.format(message, ChatColor.GREEN, added.toArray()));

                try {
                    Permissions.instance.savePermissions();
                }
                catch (IOException e) {
                    sender.sendMessage(ChatColor.DARK_RED + "Applied the changes, but the changes didn't get saved!");
                }

                Permissions.instance.recalculatePermissions(username);
            }
            else {
                sender.sendMessage(ChatColor.RED + "The player already is in all these groups");
            }
        }
        if (args[0].equalsIgnoreCase("removegroup")) {
            if (args.length < 3) {
                sender.sendMessage(Permissions.format("Expected %s parameters or more.", ChatColor.RED, 3));
                return;
            }

            String username = args[1].toLowerCase();

            PlayerPermissions permissions = Permissions.instance.getManager().getPlayer(username);

            if (permissions == null) {
                sender.sendMessage(Permissions.format("Player %s does not exist", ChatColor.RED, args[0]));

                return;
            }

            Set<GroupPermissions> groups = new TreeSet<GroupPermissions>(permissions.getGroups());
            Set<String> removed = new TreeSet<String>();

            String message = "Removed groups: ";

            boolean changed = false;

            for (int i = 2; i < args.length; i++) {
                GroupPermissions group = Permissions.instance.getManager().getGroup(args[i]);
                if (group == null) {
                    sender.sendMessage(Permissions.format("Unknown group %s", ChatColor.RED, args[i]));
                    return;
                }
                if (groups.contains(group)) {
                    groups.remove(group);
                    removed.add(args[i]);

                    if (changed) {
                        message += ", ";
                    }
                    message += "%s";

                    changed = true;
                }
            }

            if (changed) {
                permissions.setGroups(groups);

                sender.sendMessage(Permissions.format(message, ChatColor.GREEN, removed.toArray()));

                try {
                    Permissions.instance.savePermissions();
                }
                catch (IOException e) {
                    sender.sendMessage(ChatColor.DARK_RED + "Applied the changes, but the changes didn't get saved!");
                }

                Permissions.instance.recalculatePermissions(username);
            }
            else {
                sender.sendMessage(ChatColor.RED + "The player was not in any of these groups");
            }
        }
        if (args[0].equalsIgnoreCase("setperm")) {
            if (args.length != 4) {
                sender.sendMessage(Permissions.format("Expected %s parameters, no more, no less.", ChatColor.RED, 4));
                return;
            }

            String username = args[1].toLowerCase();

            String world = null;
            String permission = args[2];
            if (args[2].indexOf(":") > 0) {
                world = args[2].split(":", 2)[0];
                permission = args[2].split(":", 2)[1];
            }
            else {
                if (args[2].indexOf(":") == 0) {
                    permission = args[2].substring(1);
                }
            }

            boolean bool = Boolean.valueOf(args[3]);

            BasePermissions permissions = null;

            if (world != null) {
                permissions = Permissions.instance.getManager().getPlayer(username).getWorldPermissions(world);
            }
            else {
                permissions = Permissions.instance.getManager().getPlayer(username);
            }

            if (permissions == null) {
                sender.sendMessage(Permissions.format("Player %s does not exist", ChatColor.RED, args[0]));

                return;
            }

            if (bool) {
                permissions.allow.add(permission);

                if (permissions.deny.contains(permission)) {
                    permissions.deny.remove(permission);
                }

                sender.sendMessage(Permissions.format("Set %s for %s to %s", ChatColor.GREEN, permission, username, "true"));
            }
            else {
                permissions.deny.add(permission);

                if (permissions.allow.contains(permission)) {
                    permissions.allow.remove(permission);
                }

                sender.sendMessage(Permissions.format("Set %s for %s to %s", ChatColor.GREEN, permission, username, "false"));
            }

            try {
                Permissions.instance.savePermissions();
            }
            catch (IOException e) {
                sender.sendMessage(ChatColor.DARK_RED + "Applied the changes, but the changes didn't get saved!");
            }

            Permissions.instance.recalculatePermissions(username);
        }
        if (args[0].equalsIgnoreCase("unsetperm")) {
            if (args.length != 3) {
                sender.sendMessage(Permissions.format("Expected %s parameters, no more, no less.", ChatColor.RED, 3));
                return;
            }

            String username = args[1].toLowerCase();

            String world = null;
            String permission = args[2];
            if (args[2].indexOf(":") > 0) {
                world = args[2].split(":", 2)[0];
                permission = args[2].split(":", 2)[1];
            }
            else {
                if (args[2].indexOf(":") == 0) {
                    permission = args[2].substring(1);
                }
            }

            BasePermissions permissions = null;

            if (world != null) {
                permissions = Permissions.instance.getManager().getPlayer(username).getWorldPermissions(world);
            }
            else {
                permissions = Permissions.instance.getManager().getPlayer(username);
            }

            if (permissions == null) {
                sender.sendMessage(Permissions.format("Player %s does not exist", ChatColor.RED, args[0]));

                return;
            }

            if (!permissions.allow.contains(permission) && !permissions.deny.contains(permission)) {
                sender.sendMessage(Permissions.format("The user does not have this permission set specifically", ChatColor.RED));
                return;
            }

            boolean changed = false;

            if (permissions.deny.contains(permission)) {
                permissions.deny.remove(permission);
                changed = true;
            }

            if (permissions.allow.contains(permission)) {
                permissions.allow.remove(permission);
                changed = true;
            }

            if (changed) {
                sender.sendMessage(Permissions.format("Unset %s from %s", ChatColor.GREEN, permission, username));

                try {
                    Permissions.instance.savePermissions();
                }
                catch (IOException e) {
                    sender.sendMessage(ChatColor.DARK_RED + "Applied the changes, but the changes didn't get saved!");
                }
            }
            else {
                sender.sendMessage(Permissions.format("The group does not have this permission set specifically", ChatColor.RED));
            }

            Permissions.instance.recalculatePermissions(username);
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
        if (args.length == 1) {
            return this.primPossibles;
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("groups")) {
                return null;
            }
            if (args[0].equalsIgnoreCase("setgroup")) {
                return null;
            }
            if (args[0].equalsIgnoreCase("addgroup")) {
                return null;
            }
            if (args[0].equalsIgnoreCase("removegroup")) {
                return null;
            }
            if (args[0].equalsIgnoreCase("setperm")) {
                return null;
            }
            if (args[0].equalsIgnoreCase("unsetperm")) {
                return null;
            }
        }
        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("setperm")) {
                List<String> possibles = new ArrayList<String>();
                possibles.add(":");
                for (World world : Bukkit.getWorlds()) {
                    possibles.add(world.getName() + ":");
                }

                return possibles;
            }
            if (args[0].equalsIgnoreCase("unsetperm")) {
                List<String> possibles = new ArrayList<String>();
                possibles.add(":");
                for (World world : Bukkit.getWorlds()) {
                    possibles.add(world.getName() + ":");
                }

                return possibles;
            }
        }

        if (args.length == 4) {
            if (args[0].equalsIgnoreCase("setperm")) {
                return trueFalseResult;
            }
        }

        if (args.length >= 3) {
            if (args[0].equalsIgnoreCase("setgroup")) {
                return new ArrayList<String>(Permissions.instance.getManager().getAllGroupNames());
            }
            if (args[0].equalsIgnoreCase("addgroup")) {
                List<String> result = new ArrayList<String>(Permissions.instance.getManager().getAllGroupNames());

                PlayerPermissions permissions = Permissions.instance.getManager().getPlayer(args[1]);

                if (permissions == null) {
                    return emptyTabResult;
                }

                result.removeAll(permissions.getGroupNames());

                return result;
            }
            if (args[0].equalsIgnoreCase("removegroup")) {
                PlayerPermissions permissions = Permissions.instance.getManager().getPlayer(args[1]);

                if (permissions == null) {
                    return emptyTabResult;
                }

                return new ArrayList<String>(permissions.getGroupNames());
            }
        }

        return emptyTabResult;
    }

    @Override
    public String[] getHelpMessage() {
        return new String[] { this.name + " groups <player>", this.name + " setgroup <player> <group> [group2 [group3] ...]", this.name + " addgroup <player> <group> [group2 [group3] ...]", this.name + " removegroup <player> <group> [group2 [group3] ...]", this.name + " setperm <player> [world:]<permission> <true/false>", this.name + " unsetperm <player> [world:]<permission>" };
    }

}
