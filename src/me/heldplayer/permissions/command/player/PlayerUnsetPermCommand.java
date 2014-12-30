package me.heldplayer.permissions.command.player;

import java.io.IOException;
import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.command.easy.WorldlyPermissionEasyParameter;
import me.heldplayer.permissions.core.BasePermissions;
import me.heldplayer.permissions.core.PlayerPermissions;
import me.heldplayer.permissions.core.WorldlyPermissions;
import me.heldplayer.permissions.util.WorldlyPermission;
import net.specialattack.bukkit.core.command.AbstractSubCommand;
import net.specialattack.bukkit.core.command.ISubCommandHolder;
import net.specialattack.bukkit.core.command.easy.parameter.AnyPlayerEasyParameter;
import net.specialattack.bukkit.core.command.easy.parameter.IEasySource;
import net.specialattack.bukkit.core.command.easy.parameter.OfflinePlayerEasyParameter;
import net.specialattack.bukkit.core.util.ChatFormat;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public class PlayerUnsetPermCommand extends AbstractSubCommand {

    private final AnyPlayerEasyParameter player;
    private final WorldlyPermissionEasyParameter.Only permission;

    public PlayerUnsetPermCommand(ISubCommandHolder command, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);
        this.addParameter(this.player = new AnyPlayerEasyParameter());
        this.addParameter(this.permission = new WorldlyPermissionEasyParameter.Only(new IEasySource<WorldlyPermissions>() {
            @Override
            public WorldlyPermissions getValue() {
                return Permissions.instance.getPermissionsManager().getPlayer(PlayerUnsetPermCommand.this.player.getValue());
            }
        }));
        this.finish();
    }

    @Override
    public void runCommand(CommandSender sender) {
        String player = this.player.getValue();
        WorldlyPermission permission = this.permission.getValue();

        BasePermissions permissions = Permissions.instance.getPermissionsManager().getPlayer(player);

        if (permissions == null) {
            sender.sendMessage(ChatFormat.format("%s does not exist", ChatColor.RED, player));
            return;
        }

        if (permission.world != null) {
            permissions = ((PlayerPermissions) permissions).getWorldPermissions(permission.world);
        }

        boolean changed = false;

        if (permissions.deny.contains(permission.permission)) {
            permissions.deny.remove(permission.permission);
            changed = true;
        }

        if (permissions.allow.contains(permission.permission)) {
            permissions.allow.remove(permission.permission);
            changed = true;
        }

        if (changed) {
            sender.sendMessage(ChatFormat.format("Unset %s from %s", ChatColor.GREEN, permission, player));

            try {
                Permissions.instance.savePermissions();
            } catch (IOException e) {
                sender.sendMessage(ChatColor.DARK_RED + "Applied the changes, but the changes didn't get saved!");
            }
        } else {
            sender.sendMessage(ChatFormat.format("%s did not have %s set specifically", ChatColor.GREEN, player, permission));
            sender.sendMessage(ChatFormat.format("The player does not have this permission set specifically", ChatColor.RED));
        }

        Permissions.instance.recalculatePermissions(player);
    }

}
