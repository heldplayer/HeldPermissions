package me.heldplayer.permissions.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.core.GroupPermissions;
import me.heldplayer.permissions.core.PlayerPermissions;
import me.heldplayer.permissions.util.TabHelper;
import net.specialattack.bukkit.core.util.ChatFormat;
import net.specialattack.bukkit.core.util.Util;
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

            GroupPermissions group = Permissions.instance.getPermissionsManager().getGroup(args[1]);
            if (group == null) {
                sender.sendMessage(ChatFormat.format("Unknown group %s", ChatColor.RED, args[1]));
                return true;
            }

            PlayerPermissions permissions;
            if (player == null) {
                permissions = Permissions.instance.getPermissionsManager().getPlayer(args[0]);
            } else {
                permissions = Permissions.instance.getPermissionsManager().getPlayer(player.getName());
            }

            if (permissions == null) {
                sender.sendMessage(ChatFormat.format("Player %s does not exist", ChatColor.RED, args[0]));

                return true;
            }

            Collection<String> rankables = null;

            if (!sender.isOp()) {
                rankables = Permissions.instance.getPermissionsManager().getPlayer(sender.getName()).getRankableGroupNames();
                if (!rankables.contains(group.name)) {
                    sender.sendMessage(ChatColor.RED + "You cannot give this rank");
                    return true;
                }
            }

            Collection<GroupPermissions> groups = permissions.getGroups();

            List<GroupPermissions> effectiveRanks = new ArrayList<>();

            boolean changed = false;

            for (GroupPermissions currentGroup : groups) {
                if (!sender.isOp()) {
                    if (rankables != null && rankables.contains(currentGroup.name)) {
                        if (currentGroup.name.equals(group.name)) {
                            sender.sendMessage(ChatFormat.format("The player already has the '%s' rank", ChatColor.RED, group.name));
                            effectiveRanks.add(currentGroup);
                        } else if (currentGroup.doesInheritFrom(group)) {
                            sender.sendMessage(ChatFormat.format("'%s' is a sub-group of '%s'", ChatColor.RED, group.name, currentGroup.name));
                            effectiveRanks.add(currentGroup);
                        } else if (group.doesInheritFrom(currentGroup)) {
                            sender.sendMessage(ChatFormat.format("Promoted the player from '%s' to '%s'", ChatColor.GREEN, currentGroup.name, group.name));
                            if (!effectiveRanks.contains(group)) {
                                effectiveRanks.add(group);
                            }
                            changed = true;
                        }
                    } else {
                        sender.sendMessage(ChatFormat.format("You do not have permissions to modify the group '%s', looking for another group...", ChatColor.RED, currentGroup.name));
                        effectiveRanks.add(currentGroup);
                    }
                } else {
                    if (currentGroup.name.equals(group.name)) {
                        sender.sendMessage(ChatFormat.format("The player already has the '%s' rank", ChatColor.RED, group.name));
                        effectiveRanks.add(currentGroup);
                    } else if (currentGroup.doesInheritFrom(group)) {
                        sender.sendMessage(ChatFormat.format("'%s' is a sub-group of '%s'", ChatColor.RED, group.name, currentGroup.name));
                        effectiveRanks.add(currentGroup);
                    } else if (group.doesInheritFrom(currentGroup)) {
                        sender.sendMessage(ChatFormat.format("Promoted the player from '%s' to '%s'", ChatColor.GREEN, currentGroup.name, group.name));
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
            } catch (IOException e) {
                sender.sendMessage(ChatColor.DARK_RED + "Applied the ranks, but the ranks didn't get saved!");
            }

            Permissions.instance.recalculatePermissions(Bukkit.getPlayer(permissions.uuid));

            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return null;
        }
        if (args.length != 2) {
            return Util.TAB_RESULT_EMPTY;
        }

        String lower = args[args.length - 1].toLowerCase();

        return TabHelper.tabRankableGroup(sender).stream().map(String::toLowerCase).filter(possible -> possible.startsWith(lower)).collect(Collectors.toList());
    }
}
