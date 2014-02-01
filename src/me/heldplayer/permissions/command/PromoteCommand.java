
package me.heldplayer.permissions.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.core.GroupPermissions;
import me.heldplayer.permissions.core.PlayerPermissions;
import net.specialattack.core.command.AbstractSubCommand;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class PromoteCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 2) {
            Player player = Bukkit.getPlayer(args[0]);

            GroupPermissions group = Permissions.instance.getManager().getGroup(args[1]);
            if (group == null) {
                sender.sendMessage(Permissions.format("Unknown group %s", ChatColor.RED, args[1]));
                return true;
            }

            PlayerPermissions permissions = null;
            if (player == null) {
                permissions = Permissions.instance.getManager().getPlayer(args[0]);
            }
            else {
                permissions = Permissions.instance.getManager().getPlayer(player.getName());
            }

            List<String> rankables = null;

            if (!sender.isOp()) {
                rankables = Permissions.instance.getManager().getPlayer(sender.getName()).getRankableGroupNames();
                if (!rankables.contains(group.name)) {
                    sender.sendMessage(ChatColor.RED + "You cannot give this rank");
                    return true;
                }
            }

            List<GroupPermissions> groups = permissions.getGroups();

            List<GroupPermissions> effectiveRanks = new ArrayList<GroupPermissions>();

            boolean changed = false;

            for (int i = 0; i < groups.size(); i++) {
                GroupPermissions currentGroup = groups.get(i);

                if (!sender.isOp()) {
                    if (rankables.contains(currentGroup.name)) {
                        if (currentGroup.name.equals(group.name)) {
                            sender.sendMessage(ChatColor.RED + "The player already has this rank");
                            effectiveRanks.add(currentGroup);
                        }
                        else if (currentGroup.doesInheritFrom(group)) {
                            sender.sendMessage(Permissions.format("'%s' is a sub-group of '%s'", ChatColor.RED, group.name, currentGroup.name));
                            effectiveRanks.add(currentGroup);
                        }
                        else if (group.doesInheritFrom(currentGroup)) {
                            sender.sendMessage(Permissions.format("Promoted the player from '%s' to '%s'", ChatColor.GREEN, currentGroup.name, group.name));
                            if (!effectiveRanks.contains(group)) {
                                effectiveRanks.add(group);
                            }
                            changed = true;
                        }
                    }
                    else {
                        sender.sendMessage(Permissions.format("You do not have permissions to modify the group '%s', looking for another group...", ChatColor.RED, currentGroup.name));
                        effectiveRanks.add(currentGroup);
                    }
                }
                else {
                    if (currentGroup.name.equals(group.name)) {
                        sender.sendMessage(ChatColor.RED + "The player already has this rank");
                        effectiveRanks.add(currentGroup);
                    }
                    else if (currentGroup.doesInheritFrom(group)) {
                        sender.sendMessage(Permissions.format("'%s' is a sub-group of '%s'", ChatColor.RED, group.name, currentGroup.name));
                        effectiveRanks.add(currentGroup);
                    }
                    else if (group.doesInheritFrom(currentGroup)) {
                        sender.sendMessage(Permissions.format("Promoted the player from '%s' to '%s'", ChatColor.GREEN, currentGroup.name, group.name));
                        if (!effectiveRanks.contains(group)) {
                            effectiveRanks.add(group);
                        }
                        changed = true;
                    }
                }
            }

            if (!changed) {
                sender.sendMessage(ChatColor.RED + "Couldn't promote the player");
                return true;
            }

            permissions.setGroups(effectiveRanks);

            try {
                Permissions.instance.savePermissions();
            }
            catch (IOException e) {
                sender.sendMessage(ChatColor.DARK_RED + "Applied the ranks, but the ranks didn't get saved!");
            }

            Permissions.instance.recalculatePermissions(permissions.playerName);

            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return null;
        }
        if (args.length != 2) {
            return AbstractSubCommand.emptyTabResult;
        }

        if (sender.isOp()) {
            return Permissions.instance.getManager().getAllGroupNames();
        }

        return Permissions.instance.getManager().getPlayer(sender.getName()).getRankableGroupNames();
    }
}
