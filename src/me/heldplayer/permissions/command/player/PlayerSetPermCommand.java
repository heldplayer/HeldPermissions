package me.heldplayer.permissions.command.player;

import java.io.IOException;
import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.command.easy.WorldlyPermissionEasyParameter;
import me.heldplayer.permissions.core.BasePermissions;
import me.heldplayer.permissions.core.PlayerPermissions;
import me.heldplayer.permissions.util.WorldlyPermission;
import net.specialattack.bukkit.core.command.AbstractSubCommand;
import net.specialattack.bukkit.core.command.ISubCommandHolder;
import net.specialattack.bukkit.core.command.easy.EasyCollection;
import net.specialattack.bukkit.core.command.easy.parameter.AnyPlayerCollectionEasyParameter;
import net.specialattack.bukkit.core.command.easy.parameter.BooleanEasyParameter;
import net.specialattack.bukkit.core.util.ChatFormat;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class PlayerSetPermCommand extends AbstractSubCommand {

    private final AnyPlayerCollectionEasyParameter players;
    private final WorldlyPermissionEasyParameter permission;
    private final BooleanEasyParameter permissionValue;

    public PlayerSetPermCommand(ISubCommandHolder command, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);
        this.addParameter(this.players = new AnyPlayerCollectionEasyParameter().setName("players"));
        this.addParameter(this.permission = new WorldlyPermissionEasyParameter());
        this.addParameter(this.permissionValue = new BooleanEasyParameter());
        this.finish();
    }

    @Override
    public void runCommand(final CommandSender sender) {
        EasyCollection<String> players = this.players.get();
        final WorldlyPermission permission = this.permission.get();
        final boolean permissionValue = this.permissionValue.get();

        players.forEach(player -> {
            BasePermissions permissions = Permissions.instance.getPermissionsManager().getPlayer(player);

            if (permissions == null) {
                sender.sendMessage(ChatFormat.format("%s does not exist", ChatColor.RED, player));
                return;
            }

            if (permission.world != null) {
                permissions = ((PlayerPermissions) permissions).getWorldPermissions(permission.world);
            }

            if (permissionValue) {
                permissions.allow.add(permission.permission);

                if (permissions.deny.contains(permission.permission)) {
                    permissions.deny.remove(permission.permission);
                }
            } else {
                permissions.deny.add(permission.permission);

                if (permissions.allow.contains(permission.permission)) {
                    permissions.allow.remove(permission.permission);
                }
            }

            sender.sendMessage(ChatFormat.format("Set %s for %s to %s", ChatColor.GREEN, permission, player, permissionValue));

            try {
                Permissions.instance.savePermissions();
            } catch (IOException e) {
                sender.sendMessage(ChatColor.DARK_RED + "Applied the changes, but the changes didn't get saved!");
            }

            Permissions.instance.recalculatePermissions(player);
        });
    }
}
