package me.heldplayer.permissions.command.player;

import java.util.List;
import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.command.easy.WorldlyPermissionEasyParameter;
import me.heldplayer.permissions.core.BasePermissions;
import me.heldplayer.permissions.core.Perm;
import me.heldplayer.permissions.core.PlayerPermissions;
import me.heldplayer.permissions.util.WorldlyPermission;
import net.specialattack.spacore.api.command.AbstractSubCommand;
import net.specialattack.spacore.api.command.ISubCommandHolder;
import net.specialattack.spacore.api.command.parameter.AbstractEasyParameter;
import net.specialattack.spacore.api.command.parameter.AnyPlayerCollectionEasyParameter;
import net.specialattack.spacore.api.command.parameter.EnumEasyParameter;
import net.specialattack.spacore.util.ChatFormat;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class PlayerSetPermCommand extends AbstractSubCommand {

    private final Permissions plugin;

    private final AbstractEasyParameter<List<String>> players;
    private final AbstractEasyParameter<WorldlyPermission> permission;
    private final AbstractEasyParameter<Perm.Value> value;

    public PlayerSetPermCommand(ISubCommandHolder command, Permissions plugin, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);
        this.plugin = plugin;
        this.addParameter(this.players = new AnyPlayerCollectionEasyParameter().setName("players"));
        this.addParameter(this.permission = new WorldlyPermissionEasyParameter());
        this.addParameter(this.value = new EnumEasyParameter<>(Perm.Value.values()).setName("allow/deny/never"));
        this.finish();
    }

    @Override
    public void runCommand(final CommandSender sender) {
        List<String> players = this.players.get();
        WorldlyPermission permission = this.permission.get();
        Perm.Value value = this.value.get();

        players.forEach(player -> {
            BasePermissions permissions = this.plugin.getPermissionsManager().getPlayer(player);

            if (permissions == null) {
                sender.sendMessage(ChatFormat.format("%s does not exist", ChatColor.RED, player));
                return;
            }

            if (permission.world != null) {
                permissions = ((PlayerPermissions) permissions).getWorldPermissions(permission.world);
            }

            permissions.setPermission(permission.permission, value);

            sender.sendMessage(ChatFormat.format("Set %s for %s to %s", ChatColor.GREEN, permission, player, value));

            this.plugin.savePermissionsBy(sender);

            this.plugin.recalculatePermissions(player);
        });
    }
}
