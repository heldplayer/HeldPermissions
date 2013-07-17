
package me.heldplayer.permissions.command;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.heldplayer.permissions.Permissions;
import net.specialattack.core.command.AbstractMultiCommand;
import net.specialattack.core.command.AbstractSubCommand;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

public class PlayerSubCommand extends AbstractSubCommand {

    private final String permission;
    private final List<String> primPossibles;
    private final List<String> trueFalseResult;

    public PlayerSubCommand(AbstractMultiCommand command, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);

        this.permission = permissions;
        this.primPossibles = new ArrayList<String>();
        this.primPossibles.add("groups");
        this.primPossibles.add("setgroup");
        this.primPossibles.add("addgroup");
        this.primPossibles.add("removegroup");
        this.primPossibles.add("setperm");
        this.primPossibles.add("unsetperm");
        this.trueFalseResult = new ArrayList<String>();
        this.trueFalseResult.add("true");
        this.trueFalseResult.add("false");
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

            List<String> groups = Permissions.instance.getGroups(args[1], false);
            List<String> subGroups = Permissions.instance.getGroups(args[1], true);
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
            if (args.length != 3) {
                sender.sendMessage(Permissions.format("Expected %s parameters or more.", ChatColor.RED, 3));
                return;
            }

            YamlConfiguration permissions = Permissions.instance.permissions;

            List<String> groups = new ArrayList<String>();

            String message = "New groups: %s";

            groups.add(args[2]);
            for (int i = 3; i < args.length; i++) {
                message += ", %s";
                groups.add(args[i]);
            }

            permissions.set("users." + args[1] + ".groups", groups);

            sender.sendMessage(Permissions.format(message, ChatColor.GREEN, groups.toArray()));

            try {
                permissions.save(new File(Permissions.instance.getDataFolder(), "permissions.yml"));
            }
            catch (IOException e) {
                sender.sendMessage(ChatColor.DARK_RED + "Applied the changes, but the changes didn't get saved!");
            }

            Permissions.instance.recalculatePermissions(args[1]);
        }
        if (args[0].equalsIgnoreCase("addgroup")) {
            if (args.length != 3) {
                sender.sendMessage(Permissions.format("Expected %s parameters or more.", ChatColor.RED, 3));
                return;
            }

            YamlConfiguration permissions = Permissions.instance.permissions;

            List<String> groups = permissions.getStringList("users." + args[1] + ".groups");
            List<String> added = new ArrayList<String>();

            String message = "Added groups: ";

            boolean changed = false;

            for (int i = 2; i < args.length; i++) {
                if (!groups.contains(args[i])) {
                    groups.add(args[i]);
                    added.add(args[i]);

                    if (changed) {
                        message += ", ";
                    }
                    message += "%s";

                    changed = true;
                }
            }

            if (changed) {
                permissions.set("users." + args[1] + ".groups", groups);

                sender.sendMessage(Permissions.format(message, ChatColor.GREEN, added.toArray()));

                try {
                    permissions.save(new File(Permissions.instance.getDataFolder(), "permissions.yml"));
                }
                catch (IOException e) {
                    sender.sendMessage(ChatColor.DARK_RED + "Applied the changes, but the changes didn't get saved!");
                }

                Permissions.instance.recalculatePermissions(args[1]);
            }
            else {
                sender.sendMessage(ChatColor.RED + "The player already is in all these groups");
            }
        }
        if (args[0].equalsIgnoreCase("removegroup")) {
            if (args.length != 3) {
                sender.sendMessage(Permissions.format("Expected %s parameters or more.", ChatColor.RED, 3));
                return;
            }

            YamlConfiguration permissions = Permissions.instance.permissions;

            List<String> groups = permissions.getStringList("users." + args[1] + ".groups");
            List<String> removed = new ArrayList<String>();

            String message = "Removed groups: ";

            boolean changed = false;

            for (int i = 2; i < args.length; i++) {
                if (groups.contains(args[i])) {
                    groups.remove(args[i]);
                    removed.add(args[i]);

                    if (changed) {
                        message += ", ";
                    }
                    message += "%s";

                    changed = true;
                }
            }

            if (changed) {
                permissions.set("users." + args[1] + ".groups", groups);

                sender.sendMessage(Permissions.format(message, ChatColor.GREEN, removed.toArray()));

                try {
                    permissions.save(new File(Permissions.instance.getDataFolder(), "permissions.yml"));
                }
                catch (IOException e) {
                    sender.sendMessage(ChatColor.DARK_RED + "Applied the changes, but the changes didn't get saved!");
                }

                Permissions.instance.recalculatePermissions(args[1]);
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

            String path = "users." + args[1];
            if (args[2].indexOf(":") > 0) {
                path += "." + args[2].split(":", 2)[0];
                args[2] = args[2].split(":", 2)[1];
            }
            else {
                args[2] = args[2].substring(1);
                path += ".permissions";
            }

            boolean bool = Boolean.valueOf(args[3]);

            YamlConfiguration permissions = Permissions.instance.permissions;

            if (bool) {
                List<String> allow = permissions.getStringList(path + "allow");

                allow.add(args[2]);

                permissions.set(path + ".allow", allow);

                List<String> deny = permissions.getStringList(path + ".deny");

                if (deny.contains(args[2])) {
                    deny.remove(args[2]);

                    permissions.set(path + ".deny", deny);
                }

                sender.sendMessage(Permissions.format("Set %s for %s to %s", ChatColor.GREEN, args[2], args[1], "true"));

                try {
                    permissions.save(new File(Permissions.instance.getDataFolder(), "permissions.yml"));
                }
                catch (IOException e) {
                    sender.sendMessage(ChatColor.DARK_RED + "Applied the changes, but the changes didn't get saved!");
                }

                Permissions.instance.recalculatePermissions(args[1]);
            }
            else {
                List<String> deny = permissions.getStringList(path + "deny");

                deny.add(args[2]);

                permissions.set(path + ".deny", deny);

                List<String> allow = permissions.getStringList(path + ".allow");

                if (allow.contains(args[2])) {
                    allow.remove(args[2]);

                    permissions.set(path + ".allow", allow);
                }

                sender.sendMessage(Permissions.format("Set %s for %s to %s", ChatColor.GREEN, args[2], args[1], "false"));

                try {
                    permissions.save(new File(Permissions.instance.getDataFolder(), "permissions.yml"));
                }
                catch (IOException e) {
                    sender.sendMessage(ChatColor.DARK_RED + "Applied the changes, but the changes didn't get saved!");
                }

                Permissions.instance.recalculatePermissions(args[1]);
            }
        }
        if (args[0].equalsIgnoreCase("unsetperm")) {
            if (args.length != 3) {
                sender.sendMessage(Permissions.format("Expected %s parameters, no more, no less.", ChatColor.RED, 3));
                return;
            }

            String path = "users." + args[1];
            if (args[2].indexOf(":") > 0) {
                path += "." + args[2].split(":", 2)[0];
                args[2] = args[2].split(":", 2)[1];
            }
            else {
                args[2] = args[2].substring(1);
                path += ".permissions";
            }

            YamlConfiguration permissions = Permissions.instance.permissions;

            if (!permissions.contains(path)) {
                sender.sendMessage(Permissions.format("The group does not have this permission set specifically", ChatColor.RED));
                return;
            }

            boolean changed = false;

            List<String> deny = permissions.getStringList(path + ".deny");

            deny.add(args[2]);

            if (deny.contains(args[2])) {
                deny.remove(args[2]);

                permissions.set(path + ".deny", deny);

                changed = true;
            }

            permissions.set(path + ".deny", deny);

            List<String> allow = permissions.getStringList(path + ".allow");

            if (allow.contains(args[2])) {
                allow.remove(args[2]);

                permissions.set(path + ".allow", allow);

                changed = true;
            }

            if (changed) {
                sender.sendMessage(Permissions.format("Unset %s from %s", ChatColor.GREEN, args[2], args[1]));

                try {
                    permissions.save(new File(Permissions.instance.getDataFolder(), "permissions.yml"));
                }
                catch (IOException e) {
                    sender.sendMessage(ChatColor.DARK_RED + "Applied the changes, but the changes didn't get saved!");
                }
            }
            else {
                sender.sendMessage(Permissions.format("The group does not have this permission set specifically", ChatColor.RED));
            }

            Permissions.instance.recalculatePermissions(args[1]);
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
                return this.trueFalseResult;
            }
        }

        if (args.length >= 3) {
            if (args[0].equalsIgnoreCase("setgroup")) {
                return Permissions.instance.getAllGroups();
            }
            if (args[0].equalsIgnoreCase("addgroup")) {
                List<String> result = Permissions.instance.getAllGroups();

                result.removeAll(Permissions.instance.getGroups(args[1], false));

                return result;
            }
            if (args[0].equalsIgnoreCase("removegroup")) {
                return Permissions.instance.getGroups(args[1], false);
            }
        }

        return emptyTabResult;
    }

}
