package me.heldplayer.permissions;

import java.util.UUID;
import me.heldplayer.permissions.core.PlayerPermissions;
import net.specialattack.spacore.util.ChatFormat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

public class PermCommand implements CommandExecutor {

    private final Permissions plugin;

    public PermCommand(Permissions plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
        if (split.length == 2) {
            PlayerPermissions permissions = this.plugin.getPermissionsManager().getPlayer(split[0]);

            if (permissions == null) {
                sender.sendMessage(ChatFormat.format("Player %s does not exist", ChatColor.RED, split[0]));

                return true;
            }

            World world = null;
            Player player = Bukkit.getPlayer(permissions.uuid);
            if (player != null) {
                world = player.getWorld();
            }

            sender.sendMessage(ChatFormat.format("%s has permission %s set to %s", ChatColor.GREEN, permissions.getPlayerName(), split[1], permissions.hasPermission(split[1], world)));

            return true;
        }
        if (split.length == 1) {
            Permission perm = this.plugin.getServer().getPluginManager().getPermission(split[0]);

            if (perm == null) {
                sender.sendMessage(ChatFormat.format("Unknown permission: %s", ChatColor.RED, split[0]));
                return true;
            } else {
                sender.sendMessage(ChatFormat.format("Info on permission %s:", ChatColor.GREEN, perm.getName()));
                sender.sendMessage(ChatFormat.format("Default: %s", ChatColor.GREEN, perm.getDefault()));
                if ((perm.getDescription() != null) && (perm.getDescription().length() > 0)) {
                    sender.sendMessage(ChatFormat.format("Description: %s", ChatColor.GREEN, perm.getDescription()));
                }
                if ((perm.getChildren() != null) && (perm.getChildren().size() > 0)) {
                    sender.sendMessage(ChatFormat.format("Children: %s", ChatColor.GREEN, perm.getChildren().size()));
                }

                return true;
            }
        }
        if (split.length == 0) {
            if (sender instanceof Player) {
                UUID uuid = ((Player) sender).getUniqueId();
                if (this.plugin.debuggers.contains(uuid)) {
                    this.plugin.debuggers.remove(uuid);
                } else {
                    this.plugin.debuggers.add(uuid);
                }

                return true;
            }
        }

        return false;
    }

}
