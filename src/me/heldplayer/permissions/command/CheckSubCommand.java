package me.heldplayer.permissions.command;

import java.util.HashMap;
import java.util.List;
import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.command.easy.WorldlyPermissionEasyParameter;
import me.heldplayer.permissions.core.PlayerPermissions;
import me.heldplayer.permissions.util.WorldlyPermission;
import net.specialattack.spacore.api.command.AbstractSubCommand;
import net.specialattack.spacore.api.command.ISubCommandHolder;
import net.specialattack.spacore.api.command.parameter.OfflinePlayerCollectionEasyParameter;
import net.specialattack.spacore.util.ChatFormat;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CheckSubCommand extends AbstractSubCommand {

    private final Permissions plugin;

    private final WorldlyPermissionEasyParameter permission;
    private final OfflinePlayerCollectionEasyParameter players;

    public CheckSubCommand(ISubCommandHolder command, Permissions plugin, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);
        this.plugin = plugin;
        this.addParameter(this.permission = new WorldlyPermissionEasyParameter());
        this.addParameter(this.players = new OfflinePlayerCollectionEasyParameter().setTakeAll());
        this.finish();
    }

    @Override
    public void runCommand(final CommandSender sender) {
        final WorldlyPermission permission = this.permission.get();
        List<OfflinePlayer> players = this.players.get();

        players.forEach(player -> {
            PlayerPermissions permissions = this.plugin.getPermissionsManager().getPlayer(player.getUniqueId());

            if (permissions == null) {
                sender.sendMessage(ChatFormat.format("Player %s does not exist", ChatColor.RED, player.getName()));

                return;
            }

            if (player instanceof Player) {
                sender.sendMessage(ChatFormat.format("%s currently has %s set to true", ChatColor.AQUA, player.getName(), permission.permission, ((Player) player).hasPermission(permission.permission)));
            }

            HashMap<String, Boolean> perms = new HashMap<>();
            permissions.buildPermissions(perms, permission.world);
            String definition = ChatColor.GRAY + "unset";
            if (perms.containsKey(permission.permission)) {
                if (perms.get(permission.permission)) {
                    definition = ChatColor.GREEN + "allow";
                } else {
                    definition = ChatColor.RED + "deny";
                }
            }

            sender.sendMessage(ChatFormat.format("%s has permission %s defined as %s", ChatColor.AQUA, permissions.getPlayerName(), permission, definition));
        });
    }
}
