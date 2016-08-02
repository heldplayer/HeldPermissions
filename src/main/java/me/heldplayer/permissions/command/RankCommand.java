package me.heldplayer.permissions.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import me.heldplayer.permissions.Consts;
import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.core.GroupPermissions;
import me.heldplayer.permissions.core.PermissionsManager;
import me.heldplayer.permissions.core.PlayerPermissions;
import me.heldplayer.permissions.util.TabHelper;
import net.specialattack.spacore.util.ChatFormat;
import net.specialattack.spacore.util.ChatJoinCollector;
import net.specialattack.spacore.util.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class RankCommand implements CommandExecutor, TabCompleter {

    private final Permissions plugin;

    public RankCommand(Permissions plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        PermissionsManager manager = this.plugin.getPermissionsManager();
        if (args.length == 1) {
            Player player = Bukkit.getPlayer(args[0]);

            PlayerPermissions permissions;
            if (player == null) {
                permissions = manager.getPlayer(args[0]);
            } else {
                permissions = manager.getPlayer(player.getName());
            }

            if (permissions == null) {
                sender.sendMessage(ChatFormat.format("Player %s does not exist", ChatColor.RED, args[0]));

                return true;
            }

            String ranks = "";

            Collection<GroupPermissions> groups = permissions.getGroups();

            if (groups.isEmpty()) {
                ranks = manager.defaultGroup == null ? "(none)" : manager.defaultGroup.name + " (default)";
            } else {
                for (GroupPermissions group : groups) {
                    if (ranks.length() > 0) {
                        ranks += ", ";
                    }
                    ranks += group.name;
                }
            }

            sender.sendMessage(ChatColor.GRAY + "Player ranks: " + ChatColor.YELLOW + ranks);

            return true;
        } else if (args.length > 1) {
            Player player = Bukkit.getPlayer(args[0]);

            PlayerPermissions permissions;
            if (player == null) {
                permissions = manager.getPlayer(args[0]);
            } else {
                permissions = manager.getPlayer(player.getName());
            }

            if (permissions == null) {
                sender.sendMessage(ChatFormat.format("Player %s does not exist", ChatColor.RED, args[0]));

                return true;
            }

            boolean removeAll = args.length == 2 && args[1].equals("REMOVE");

            Collection<String> rankables = null;

            if (!sender.isOp()) {
                if (sender instanceof Player) {
                    rankables = manager.getPlayer((Player) sender).getRankableGroupNames();
                } else {
                    sender.sendMessage(ChatColor.RED + "You cannot use this command");
                    return true;
                }
            }

            List<GroupPermissions> effectiveRanks = new ArrayList<>();

            String ranks = "";

            boolean first = true;

            if (!removeAll) {
                for (int i = 1; i < args.length; i++) { // Add all new groups
                    GroupPermissions group = manager.getGroup(args[i]);
                    if (group == null) {
                        sender.sendMessage(ChatFormat.format("Unknown group %s", ChatColor.RED, args[i]));
                        return true;
                    }
                    if (!sender.isOp()) {
                        if (rankables != null && rankables.contains(group.name)) { // Can rank group
                            effectiveRanks.add(group);

                            if (!first) {
                                ranks += ChatColor.WHITE + ", ";
                            }

                            ranks += ChatColor.GREEN + args[i];

                            first = false;
                        } else { // Cannot rank group
                            if (!first) {
                                ranks += ChatColor.WHITE + ", ";
                            }

                            ranks += ChatColor.RED + args[i];

                            first = false;
                        }
                    } else { // Can rank all groups because is op
                        effectiveRanks.add(group);

                        if (!first) {
                            ranks += ChatColor.WHITE + ", ";
                        }

                        ranks += ChatColor.GREEN + args[i];

                        first = false;
                    }
                }
            }

            Collection<GroupPermissions> groups = permissions.getGroups();

            for (GroupPermissions group : groups) { // Remove groups
                if (!sender.isOp()) {
                    if (rankables != null && rankables.contains(group.name)) { // Can unrank group
                        if (!first) {
                            ranks += ChatColor.WHITE + ", ";
                        }

                        ranks += ChatColor.DARK_GREEN + group.name;

                        first = false;
                    } else { // Cannot unrank group
                        effectiveRanks.add(group);

                        if (!first) {
                            ranks += ChatColor.WHITE + ", ";
                        }

                        ranks += ChatColor.DARK_RED + group.name;

                        first = false;
                    }
                } else { // Can unrank all groups because is op
                    if (!first) {
                        ranks += ChatColor.WHITE + ", ";
                    }

                    ranks += ChatColor.DARK_GREEN + group.name;

                    first = false;
                }
            }

            permissions.setGroups(effectiveRanks);

            if (sender instanceof Player) {
                String description = ChatColor.GREEN + "applied" + ChatColor.WHITE
                        + " | " + ChatColor.RED + "failed" + ChatColor.WHITE
                        + " | " + ChatColor.DARK_GREEN + "removed" + ChatColor.WHITE
                        + " | " + ChatColor.DARK_RED + "retained" + ChatColor.WHITE;
                sender.sendMessage("Applied ranks (" + description + "):");
            } else {
                sender.sendMessage("Applied ranks:");
            }

            sender.sendMessage(ranks);

            Permissions.notifyExcept(ChatUtil.constructMessage(ChatColor.GREEN, "Set rank of ", permissions.getPlayerName(), ": ",
                    permissions.getGroupNames().stream().collect(new ChatJoinCollector())), sender, Consts.PERM_LISTEN_RANK);

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

        String lower = args[args.length - 1].toLowerCase();

        return TabHelper.tabRankableGroup(this.plugin.getPermissionsManager(), sender).stream()
                .map(String::toLowerCase)
                .filter(possible -> possible.startsWith(lower))
                .collect(Collectors.toList());
    }
}
