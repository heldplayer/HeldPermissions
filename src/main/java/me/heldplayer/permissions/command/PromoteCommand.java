package me.heldplayer.permissions.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.core.GroupPermissions;
import me.heldplayer.permissions.core.PlayerPermissions;
import me.heldplayer.permissions.util.TabHelper;
import net.specialattack.spacore.util.ChatFormat;
import net.specialattack.spacore.util.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class PromoteCommand implements CommandExecutor, TabCompleter {

    private final Permissions plugin;

    public PromoteCommand(Permissions plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 2) {
            Player player = Bukkit.getPlayer(args[0]);

            GroupPermissions group = this.plugin.getPermissionsManager().getGroup(args[1]);
            if (group == null) {
                sender.sendMessage(ChatFormat.format("Unknown group %s", ChatColor.RED, args[1]));
                return true;
            }

            PlayerPermissions permissions;
            if (player == null) {
                permissions = this.plugin.getPermissionsManager().getPlayer(args[0]);
            } else {
                permissions = this.plugin.getPermissionsManager().getPlayer(player.getName());
            }

            if (permissions == null) {
                sender.sendMessage(ChatFormat.format("Player %s does not exist", ChatColor.RED, args[0]));

                return true;
            }

            Collection<String> rankables = null;

            if (!sender.isOp()) {
                if (sender instanceof Player) {
                    rankables = this.plugin.getPermissionsManager().getPlayer((Player) sender).getRankableGroupNames();
                    if (!rankables.contains(group.name)) {
                        sender.sendMessage(ChatColor.RED + "You cannot give this rank");
                        return true;
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "You cannot use this command");
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

            this.plugin.savePermissionsBy(sender);

            this.plugin.recalculatePermissions(Bukkit.getPlayer(permissions.uuid));

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
            return ChatUtil.TAB_RESULT_EMPTY;
        }

        String lower = args[args.length - 1].toLowerCase();

        return TabHelper.tabRankableGroup(this.plugin.getPermissionsManager(), sender).stream()
                .map(String::toLowerCase)
                .filter(possible -> possible.startsWith(lower))
                .collect(Collectors.toList());
    }
}
