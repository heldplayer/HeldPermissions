
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

public class GroupSubCommand extends AbstractSubCommand {

    private final String permission;
    private final List<String> primPossibles;
    private final List<String> trueFalseResult;

    public GroupSubCommand(AbstractMultiCommand command, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);

        this.permission = permissions;
        this.primPossibles = new ArrayList<String>();
        this.primPossibles.add("list");
        this.primPossibles.add("players");
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

        if (args[0].equalsIgnoreCase("list")) {
            if (args.length != 1) {
                sender.sendMessage(Permissions.format("Expected %s parameter, no more, no less.", ChatColor.RED, 1));
                return;
            }

            List<String> groups = Permissions.instance.getAllGroups();

            String message = "Groups: %s";

            for (int i = 1; i < groups.size(); i++) {
                message += ", %s";
            }

            sender.sendMessage(Permissions.format(message, ChatColor.GREEN, groups.toArray()));
        }
        if (args[0].equalsIgnoreCase("players")) {
            if (args.length != 2) {
                sender.sendMessage(Permissions.format("Expected %s parameter, no more, no less.", ChatColor.RED, 2));
                return;
            }

            List<String> groups = Permissions.instance.getPlayersInGroup(args[1]);

            String message = "Players in group: %s";

            for (int i = 1; i < groups.size(); i++) {
                message += ", %s";
            }

            sender.sendMessage(Permissions.format(message, ChatColor.GREEN, groups.toArray()));
        }
        if (args[0].equalsIgnoreCase("setperm")) {
            if (args.length != 4) {
                sender.sendMessage(Permissions.format("Expected %s parameters, no more, no less.", ChatColor.RED, 4));
                return;
            }

            String path = "groups." + args[1];
            if (args[2].indexOf(":") > 0) {
                path += "." + args[2].split(":", 2)[0];
                args[2] = args[2].split(":", 2)[1];
            }
            else {
                if (args[2].indexOf(":") == 0) {
                    args[2] = args[2].substring(1);
                }
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

                Permissions.instance.recalculatePermissions();
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

                Permissions.instance.recalculatePermissions();
            }
        }
        if (args[0].equalsIgnoreCase("unsetperm")) {
            if (args.length != 3) {
                sender.sendMessage(Permissions.format("Expected %s parameters, no more, no less.", ChatColor.RED, 3));
                return;
            }

            String path = "groups." + args[1];
            if (args[2].indexOf(":") > 0) {
                path += "." + args[2].split(":", 2)[0];
                args[2] = args[2].split(":", 2)[1];
            }
            else {
                if (args[2].indexOf(":") == 0) {
                    args[2] = args[2].substring(1);
                }
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

            Permissions.instance.recalculatePermissions();
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
            if (args[0].equalsIgnoreCase("setperm")) {
                return Permissions.instance.getAllGroups();
            }
            if (args[0].equalsIgnoreCase("unsetperm")) {
                return Permissions.instance.getAllGroups();
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

        return emptyTabResult;
    }

    @Override
    public String[] getHelpMessage() {
        return new String[] { this.name + " list", this.name + " players <group>", this.name + " setperm <group> [world:]<permission> <true/false>", this.name + " unsetperm <group> [world:]<permission>" };
    }

}
