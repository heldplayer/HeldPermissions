package me.heldplayer.permissions.command;

import java.util.List;
import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.core.PermCollection;
import me.heldplayer.permissions.command.easy.WorldlyPermissionEasyParameter;
import me.heldplayer.permissions.core.Perm;
import me.heldplayer.permissions.core.PlayerPermissions;
import me.heldplayer.permissions.util.WorldlyPermission;
import net.specialattack.spacore.api.command.AbstractSubCommand;
import net.specialattack.spacore.api.command.ISubCommandHolder;
import net.specialattack.spacore.api.command.parameter.AbstractEasyParameter;
import net.specialattack.spacore.api.command.parameter.AnyPlayerCollectionEasyParameter;
import net.specialattack.spacore.util.ChatFormat;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CheckSubCommand extends AbstractSubCommand {

    private final Permissions plugin;

    private final AbstractEasyParameter<WorldlyPermission> permission;
    private final AbstractEasyParameter<List<String>> players;

    public CheckSubCommand(ISubCommandHolder command, Permissions plugin, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);
        this.plugin = plugin;
        this.addParameter(this.permission = new WorldlyPermissionEasyParameter());
        this.addParameter(this.players = new AnyPlayerCollectionEasyParameter().setTakeAll());
        this.finish();
    }

    @Override
    public void runCommand(final CommandSender sender) {
        final WorldlyPermission permission = this.permission.get();
        List<String> players = this.players.get();

        players.forEach(playerName -> {
            PlayerPermissions permissions = this.plugin.getPermissionsManager().getPlayer(playerName);

            Player player = this.plugin.getServer().getPlayer(playerName);
            if (player != null) {
                sender.sendMessage(ChatFormat.format("%s currently has %s set to %s", ChatColor.AQUA, player.getName(), permission.permission, player.hasPermission(permission.permission)));
            }

            if (permissions == null) {
                sender.sendMessage(ChatFormat.format("Player %s does not exist", ChatColor.AQUA, playerName));
                return;
            }

            PermCollection perms = new PermCollection();
            permissions.buildPermissions(perms, permission.world);

            String definition;
            Perm.Value value = perms.get(permission.permission);
            if (value == Perm.Value.ALLOW) {
                definition = ChatColor.GREEN + "ALLOW";
            } else if (value == Perm.Value.DENY) {
                definition = ChatColor.RED + "DENY";
            } else if (value == Perm.Value.NEVER) {
                definition = ChatColor.DARK_RED + "NEVER";
            } else {
                definition = ChatColor.GRAY + "UNSET";
            }

            sender.sendMessage(ChatFormat.format("%s has permission %s defined as %s", ChatColor.AQUA, permissions.getPlayerName(), permission, definition));
        });
    }
}
