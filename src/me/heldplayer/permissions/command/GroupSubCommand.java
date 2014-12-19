package me.heldplayer.permissions.command;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.core.BasePermissions;
import me.heldplayer.permissions.core.GroupPermissions;
import me.heldplayer.permissions.util.TabHelper;
import net.specialattack.bukkit.core.command.AbstractMultiCommand;
import net.specialattack.bukkit.core.command.AbstractSubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class GroupSubCommand extends AbstractSubCommand {

    private final String permission;
    private final List<String> primPossibles;

    public GroupSubCommand(AbstractMultiCommand command, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);

        this.permission = permissions;
        this.primPossibles = Collections.unmodifiableList(Arrays.asList("list", "players", "setperm", "unsetperm"));
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

            Collection<String> groups = Permissions.instance.getManager().getAllGroupNames();

            String message = "Groups: %s";

            for (int i = 1; i < groups.size(); i++) {
                message += ", %s";
            }

            sender.sendMessage(Permissions.format(message, ChatColor.GREEN, groups.toArray()));
            sender.sendMessage(Permissions.format("%s groups", ChatColor.GREEN, groups.size()));
        }
        if (args[0].equalsIgnoreCase("players")) {
            if (args.length != 2) {
                sender.sendMessage(Permissions.format("Expected %s parameter, no more, no less.", ChatColor.RED, 2));
                return;
            }

            List<String> players = Permissions.instance.getManager().getPlayersInGroup(args[1]);

            String message = "Players in group: %s";

            for (int i = 1; i < players.size(); i++) {
                message += ", %s";
            }

            sender.sendMessage(Permissions.format(message, ChatColor.GREEN, players.isEmpty() ? "none" : players.toArray()));
            sender.sendMessage(Permissions.format("%s players", ChatColor.GREEN, players.size()));
        }
        if (args[0].equalsIgnoreCase("setperm")) {
            if (args.length != 4) {
                sender.sendMessage(Permissions.format("Expected %s parameters, no more, no less.", ChatColor.RED, 4));
                return;
            }

            String group = args[1];

            String world = null;
            String permission = args[2];
            if (args[2].indexOf(":") > 0) {
                world = args[2].split(":", 2)[0];
                permission = args[2].split(":", 2)[1];
            } else {
                if (args[2].indexOf(":") == 0) {
                    permission = args[2].substring(1);
                }
            }

            boolean bool = Boolean.valueOf(args[3]);

            BasePermissions permissions = null;

            if (world != null) {
                permissions = Permissions.instance.getManager().getGroup(group).getWorldPermissions(world);
            } else {
                permissions = Permissions.instance.getManager().getGroup(group);
            }

            if (bool) {
                permissions.allow.add(permission);

                if (permissions.deny.contains(permission)) {
                    permissions.deny.remove(permission);
                }

                sender.sendMessage(Permissions.format("Set %s for %s to %s", ChatColor.GREEN, permission, group, "true"));

                try {
                    Permissions.instance.savePermissions();
                } catch (IOException e) {
                    sender.sendMessage(ChatColor.DARK_RED + "Applied the changes, but the changes didn't get saved!");
                }

                Permissions.instance.recalculatePermissions();
            } else {
                permissions.deny.add(permission);

                if (permissions.allow.contains(permission)) {
                    permissions.allow.remove(permission);
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
        if (args[0].equalsIgnoreCase("unsetperm")) {
            if (args.length != 3) {
                sender.sendMessage(Permissions.format("Expected %s parameters, no more, no less.", ChatColor.RED, 3));
                return;
            }

            String group = args[1];

            String world = null;
            String permission = args[2];
            if (args[2].indexOf(":") > 0) {
                world = args[2].split(":", 2)[0];
                permission = args[2].split(":", 2)[1];
            } else {
                if (args[2].indexOf(":") == 0) {
                    permission = args[2].substring(1);
                }
            }

            BasePermissions permissions = null;

            if (world != null) {
                permissions = Permissions.instance.getManager().getGroup(group).getWorldPermissions(world);
            } else {
                permissions = Permissions.instance.getManager().getGroup(group);
            }

            if (!permissions.allow.contains(permission) && !permissions.deny.contains(permission)) {
                sender.sendMessage(Permissions.format("The group does not have this permission set specifically", ChatColor.RED));
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
                sender.sendMessage(Permissions.format("Unset %s from %s", ChatColor.GREEN, permission, group));

                try {
                    Permissions.instance.savePermissions();
                } catch (IOException e) {
                    sender.sendMessage(ChatColor.DARK_RED + "Applied the changes, but the changes didn't get saved!");
                }
            } else {
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
                return TabHelper.tabAnyGroup();
            }
            if (args[0].equalsIgnoreCase("unsetperm")) {
                return TabHelper.tabAnyGroup();
            }
            if (args[0].equalsIgnoreCase("players")) {
                return TabHelper.tabAnyGroup();
            }
        }

        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("setperm")) {
                return TabHelper.tabAnyPermissionWorldly(args[2]);
            }
            if (args[0].equalsIgnoreCase("unsetperm")) {
                String world = args[2].indexOf(':') < 0 ? "" : args[2].substring(0, args[2].indexOf(':'));
                GroupPermissions permissions = Permissions.instance.getManager().getGroup(args[1]);

                if (world.isEmpty()) {
                    return TabHelper.tabSetPermission(args[2], permissions);
                } else {
                    return TabHelper.tabSetPermission(args[2], permissions.getWorldPermissions(world));
                }
            }
        }

        if (args.length == 4) {
            if (args[0].equalsIgnoreCase("setperm")) {
                return trueFalseResult;
            }
        }

        return emptyTabResult;
    }

    @Override
    public String[] getHelpMessage() {
        return new String[] { this.name + " list", this.name + " players <group>", this.name + " setperm <group> [world:]<permission> <true/false>", this.name + " unsetperm <group> [world:]<permission>" };
    }

}
