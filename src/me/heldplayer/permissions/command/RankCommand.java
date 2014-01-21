
package me.heldplayer.permissions.command;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.heldplayer.permissions.Permissions;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class RankCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            Player player = Bukkit.getPlayer(args[0]);

            YamlConfiguration permissions = Permissions.instance.permissions;

            String path = "users.";

            if (player == null) {
                path += args[0];

                if (!permissions.contains(path)) {
                    sender.sendMessage(ChatColor.RED + "Player not found and not online");
                    return true;
                }
            }
            else {
                path += player.getName();
            }

            String ranks = "";

            List<String> groups = permissions.getStringList(path + ".groups");

            for (int i = 0; i < groups.size(); i++) {
                if (i != 0) {
                    ranks += ", ";
                }
                ranks += groups.get(i);
            }

            sender.sendMessage(ChatColor.GRAY + "Player ranks: " + ChatColor.YELLOW + ranks);

            return true;
        }
        else if (args.length > 1) {
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
            }

            List<String> effectiveRanks = new ArrayList<String>();

            String ranks = "";

            boolean first = true;

            for (int i = 1; i < args.length; i++) {
                if (!sender.isOp()) {
                    if (rankables.contains(args[i].toLowerCase())) {
                        effectiveRanks.add(args[i].toLowerCase());

                        if (!first) {
                            ranks += ChatColor.WHITE + ", ";
                        }

                        ranks += ChatColor.GREEN + args[i];

                        first = false;
                    }
                    else {
                        if (!first) {
                            ranks += ChatColor.WHITE + ", ";
                        }

                        ranks += ChatColor.RED + args[i];

                        first = false;
                    }
                }
                else {
                    effectiveRanks.add(args[i].toLowerCase());

                    if (!first) {
                        ranks += ChatColor.WHITE + ", ";
                    }

                    ranks += ChatColor.GREEN + args[i];

                    first = false;
                }
            }

            List<String> groups = permissions.getStringList(path + ".groups");

            for (int i = 0; i < groups.size(); i++) {
                String group = groups.get(i);

                if (!sender.isOp()) {
                    if (rankables.contains(group.toLowerCase())) {
                        if (!first) {
                            ranks += ChatColor.WHITE + ", ";
                        }

                        ranks += ChatColor.DARK_GREEN + group;

                        first = false;
                    }
                    else {
                        effectiveRanks.add(group.toLowerCase());

                        if (!first) {
                            ranks += ChatColor.WHITE + ", ";
                        }

                        ranks += ChatColor.DARK_RED + group;

                        first = false;
                    }
                }
                else {
                    if (!first) {
                        ranks += ChatColor.WHITE + ", ";
                    }

                    ranks += ChatColor.DARK_GREEN + group;

                    first = false;
                }
            }

            permissions.set(path + ".groups", effectiveRanks);

            if (sender instanceof Player) {
                sender.sendMessage(ChatColor.WHITE + "Applied ranks (" + ChatColor.GREEN + "applied" + ChatColor.WHITE + " | " + ChatColor.RED + "failed" + ChatColor.WHITE + " | " + ChatColor.DARK_GREEN + "removed" + ChatColor.WHITE + " | " + ChatColor.DARK_RED + "retained" + ChatColor.WHITE + "): ");
            }
            else {
                sender.sendMessage("Applied ranks:");
            }

            sender.sendMessage(ranks);

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

        if (sender.isOp()) {
            return Permissions.instance.getAllGroups();
        }

        return Permissions.instance.getRankableGroups((Player) sender);
    }
}
