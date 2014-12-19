package me.heldplayer.permissions;

import me.heldplayer.permissions.core.PlayerPermissions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

public class PermCommand implements CommandExecutor {

    private final Permissions main;

    public PermCommand(Permissions plugin) {
        this.main = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
        if (split.length == 2) {
            PlayerPermissions permissions = Permissions.instance.getManager().getPlayer(split[0]);

            if (permissions == null) {
                sender.sendMessage(Permissions.format("Player %s does not exist", ChatColor.RED, split[0]));

                return true;
            }

            World world = null;
            Player player = Bukkit.getPlayer(permissions.uuid);
            if (player != null) {
                world = player.getWorld();
            }

            sender.sendMessage(Permissions.format("%s has permission %s set to %s", ChatColor.GREEN, permissions.getPlayerName(), split[1], permissions.hasPermission(split[1], world)));

            return true;
        }
        if (split.length == 1) {
            Permission perm = this.main.getServer().getPluginManager().getPermission(split[0]);

            if (perm == null) {
                sender.sendMessage(Permissions.format("Unknown permission: %s", ChatColor.RED, split[0]));
                return true;
            } else {
                sender.sendMessage(Permissions.format("Info on permission %s:", ChatColor.GREEN, perm.getName()));
                sender.sendMessage(Permissions.format("Default: %s", ChatColor.GREEN, perm.getDefault()));
                if ((perm.getDescription() != null) && (perm.getDescription().length() > 0)) {
                    sender.sendMessage(Permissions.format("Description: %s", ChatColor.GREEN, perm.getDescription()));
                }
                if ((perm.getChildren() != null) && (perm.getChildren().size() > 0)) {
                    sender.sendMessage(Permissions.format("Children: %s", ChatColor.GREEN, perm.getChildren().size()));
                }

                return true;
            }
        }
        if (split.length == 0) {
            if (this.main.debuggers.contains(sender.getName())) {
                this.main.debuggers.remove(sender.getName());
            } else {
                this.main.debuggers.add(sender.getName());
            }

            return true;
        }

        return false;
    }

}
