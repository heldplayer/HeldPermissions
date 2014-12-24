package me.heldplayer.permissions.command;

import java.util.HashMap;
import java.util.List;
import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.core.PlayerPermissions;
import me.heldplayer.permissions.util.TabHelper;
import me.heldplayer.permissions.util.WorldlyPermission;
import net.specialattack.bukkit.core.command.AbstractSubCommand;
import net.specialattack.bukkit.core.command.ISubCommandHolder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CheckSubCommand extends AbstractSubCommand {

    public CheckSubCommand(ISubCommandHolder command, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);
    }

    @Override
    public void runCommand(CommandSender sender, String alias, String... args) {
        if (args.length == 0) {
            sender.sendMessage(Permissions.format("Expected %s or more parameters.", ChatColor.RED, 1));
            return;
        }

        WorldlyPermission permission = new WorldlyPermission(args[0]);

        if (args.length == 1) {
            sender.sendMessage(Permissions.format("%s currently has %s set to true", ChatColor.AQUA, sender.getName(), permission.permission, sender.hasPermission(permission.permission)));
            if (sender instanceof Player) {
                PlayerPermissions permissions = Permissions.instance.getPermissionsManager().getPlayer(((Player) sender).getUniqueId());
                if (permissions == null) {
                    sender.sendMessage(Permissions.format("%s doesn't have any permissions set", ChatColor.AQUA, sender.getName()));
                } else {
                    HashMap<String, Boolean> perms = new HashMap<String, Boolean>();
                    permissions.buildPermissions(perms, permission.world);
                    String definition = ChatColor.GRAY + "unset";
                    if (perms.containsKey(permission.permission)) {
                        if (perms.get(permission.permission)) {
                            definition = ChatColor.GREEN + "allow";
                        } else {
                            definition = ChatColor.RED + "deny";
                        }
                    }

                    sender.sendMessage(Permissions.format("%s has permission %s defined as %s", ChatColor.AQUA, permissions.getPlayerName(), permission, definition));
                }
            }
        } else {
            for (int i = 1; i < args.length; i++) {
                PlayerPermissions permissions = Permissions.instance.getPermissionsManager().getPlayer(args[i]);

                if (permissions == null) {
                    sender.sendMessage(Permissions.format("Player %s does not exist", ChatColor.RED, args[i]));

                    continue;
                }

                Player player = Bukkit.getPlayer(permissions.uuid);
                if (player != null) {
                    sender.sendMessage(Permissions.format("%s currently has %s set to true", ChatColor.AQUA, sender.getName(), permission.permission, sender.hasPermission(permission.permission)));
                }

                HashMap<String, Boolean> perms = new HashMap<String, Boolean>();
                permissions.buildPermissions(perms, permission.world);
                String definition = ChatColor.GRAY + "unset";
                if (perms.containsKey(permission.permission)) {
                    if (perms.get(permission.permission)) {
                        definition = ChatColor.GREEN + "allow";
                    } else {
                        definition = ChatColor.RED + "deny";
                    }
                }

                sender.sendMessage(Permissions.format("%s has permission %s defined as %s", ChatColor.AQUA, permissions.getPlayerName(), permission, definition));
            }
        }
    }

    @Override
    public List<String> getTabCompleteResults(CommandSender sender, String alias, String... args) {
        if (args.length == 1) {
            return TabHelper.tabAnyPermissionWorldly(args[0]);
        }

        return null;
    }

    @Override
    public String[] getHelpMessage(CommandSender sender) {
        return new String[] { this.name + " <permission> [player1 [player2 [...]]]" };
    }

}
