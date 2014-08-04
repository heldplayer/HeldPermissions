
package me.heldplayer.permissions.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.core.GroupPermissions;
import me.heldplayer.permissions.core.PlayerPermissions;
import me.heldplayer.permissions.util.TabHelper;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class RankCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            Player player = Bukkit.getPlayer(args[0]);

            PlayerPermissions permissions = null;
            if (player == null) {
                permissions = Permissions.instance.getManager().getPlayer(args[0]);
            }
            else {
                permissions = Permissions.instance.getManager().getPlayer(player.getName());
            }

            if (permissions == null) {
                sender.sendMessage(Permissions.format("Player %s does not exist", ChatColor.RED, args[0]));

                return true;
            }

            String ranks = "";

            Collection<GroupPermissions> groups = permissions.getGroups();

            for (GroupPermissions group : groups) {
                if (ranks.length() > 0) {
                    ranks += ", ";
                }
                ranks += group.name;
            }

            sender.sendMessage(ChatColor.GRAY + "Player ranks: " + ChatColor.YELLOW + ranks);

            return true;
        }
        else if (args.length > 1) {
            Player player = Bukkit.getPlayer(args[0]);

            PlayerPermissions permissions = null;
            if (player == null) {
                permissions = Permissions.instance.getManager().getPlayer(args[0]);
            }
            else {
                permissions = Permissions.instance.getManager().getPlayer(player.getName());
            }

            if (permissions == null) {
                sender.sendMessage(Permissions.format("Player %s does not exist", ChatColor.RED, args[0]));

                return true;
            }

            Collection<String> rankables = null;

            if (!sender.isOp()) {
                rankables = Permissions.instance.getManager().getPlayer(sender.getName()).getRankableGroupNames();
            }

            Set<GroupPermissions> effectiveRanks = new TreeSet<GroupPermissions>();

            String ranks = "";

            boolean first = true;

            for (int i = 1; i < args.length; i++) {
                GroupPermissions group = Permissions.instance.getManager().getGroup(args[i]);
                if (group == null) {
                    sender.sendMessage(Permissions.format("Unknown group %s", ChatColor.RED, args[i]));
                    return true;
                }
                if (!sender.isOp()) {
                    if (rankables.contains(group.name)) {
                        effectiveRanks.add(group);

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
                    effectiveRanks.add(group);

                    if (!first) {
                        ranks += ChatColor.WHITE + ", ";
                    }

                    ranks += ChatColor.GREEN + args[i];

                    first = false;
                }
            }

            Collection<GroupPermissions> groups = permissions.getGroups();

            for (GroupPermissions group : groups) {
                if (!sender.isOp()) {
                    if (rankables.contains(group.name)) {
                        if (!first) {
                            ranks += ChatColor.WHITE + ", ";
                        }

                        ranks += ChatColor.DARK_GREEN + group.name;

                        first = false;
                    }
                    else {
                        effectiveRanks.add(group);

                        if (!first) {
                            ranks += ChatColor.WHITE + ", ";
                        }

                        ranks += ChatColor.DARK_RED + group.name;

                        first = false;
                    }
                }
                else {
                    if (!first) {
                        ranks += ChatColor.WHITE + ", ";
                    }

                    ranks += ChatColor.DARK_GREEN + group.name;

                    first = false;
                }
            }

            permissions.setGroups(effectiveRanks);

            if (sender instanceof Player) {
                sender.sendMessage(ChatColor.WHITE + "Applied ranks (" + ChatColor.GREEN + "applied" + ChatColor.WHITE + " | " + ChatColor.RED + "failed" + ChatColor.WHITE + " | " + ChatColor.DARK_GREEN + "removed" + ChatColor.WHITE + " | " + ChatColor.DARK_RED + "retained" + ChatColor.WHITE + "): ");
            }
            else {
                sender.sendMessage("Applied ranks:");
            }

            sender.sendMessage(ranks);

            try {
                Permissions.instance.savePermissions();
            }
            catch (IOException e) {
                sender.sendMessage(ChatColor.DARK_RED + "Applied the ranks, but the ranks didn't get saved!");
            }

            Permissions.instance.recalculatePermissions(Bukkit.getPlayer(permissions.uuid));

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

        ArrayList<String> result = new ArrayList<String>();

        for (String possible : TabHelper.tabRankableGroup(sender)) {
            if (possible.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
                result.add(possible);
            }
        }

        return result;
    }

}
