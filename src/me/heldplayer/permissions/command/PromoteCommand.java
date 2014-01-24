
package me.heldplayer.permissions.command;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.heldplayer.permissions.Permissions;
import net.specialattack.core.command.AbstractSubCommand;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class PromoteCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 2) {
            String rank = args[1].toLowerCase();
            Player player = Bukkit.getPlayer(args[0]);

            YamlConfiguration permissions = Permissions.instance.permissions;

            String path = "users.";
            String playerName = "";

            if (player == null) {
                path += args[0];
                playerName = args[0];

                if (!permissions.contains(path)) {
                    sender.sendMessage(ChatColor.RED + "Player not found and not online");
                    return true;
                }
            }
            else {
                path += player.getName();
                playerName = player.getName();
            }

            List<String> rankables = null;

            if (!sender.isOp()) {
                rankables = Permissions.instance.getRankableGroups((Player) sender);
                if (!rankables.contains(rank)) {
                    sender.sendMessage(ChatColor.RED + "You cannot give this rank");
                    return true;
                }
            }

            List<String> groups = permissions.getStringList(path + ".groups");

            List<String> effectiveRanks = new ArrayList<String>();

            boolean changed = false;

            for (int i = 0; i < groups.size(); i++) {
                String group = groups.get(i).toLowerCase();

                if (!sender.isOp()) {
                    if (rankables.contains(group)) {
                        if (Permissions.instance.doesGroupInheritFromGroup(group, rank)) {
                            sender.sendMessage(ChatColor.RED + "'" + rank + "'" + " is a sub-group of '" + group + "'");
                            effectiveRanks.add(group);
                        }
                        else if (Permissions.instance.doesGroupInheritFromGroup(rank, group)) {
                            sender.sendMessage(ChatColor.GREEN + "Promoted the player from '" + group + "'" + " to '" + rank + "'");
                            if (!effectiveRanks.contains(rank)) {
                                effectiveRanks.add(rank);
                            }
                            changed = true;
                        }
                    }
                    else {
                        effectiveRanks.add(group);
                    }
                }
                else {
                    if (Permissions.instance.doesGroupInheritFromGroup(group, rank)) {
                        sender.sendMessage(ChatColor.RED + "'" + rank + "'" + " is a sub-group of '" + group + "'");
                        effectiveRanks.add(group);
                    }
                    else if (Permissions.instance.doesGroupInheritFromGroup(rank, group)) {
                        sender.sendMessage(ChatColor.GREEN + "Promoted the player from '" + group + "'" + " to '" + rank + "'");
                        if (!effectiveRanks.contains(rank)) {
                            effectiveRanks.add(rank);
                        }
                        changed = true;
                    }
                }
            }

            if (!changed) {
                sender.sendMessage(ChatColor.RED + "Nothing has changed");
                return true;
            }

            permissions.set(path + ".groups", effectiveRanks);

            try {
                permissions.save(new File(Permissions.instance.getDataFolder(), "permissions.yml"));
            }
            catch (IOException e) {
                sender.sendMessage(ChatColor.DARK_RED + "Applied the ranks, but the ranks didn't get saved!");
            }

            Permissions.instance.recalculatePermissions(playerName);

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
            return Permissions.instance.getAllGroups();
        }

        return Permissions.instance.getRankableGroups((Player) sender);
    }
}
