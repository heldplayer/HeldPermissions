package me.heldplayer.permissions.command.player;

import java.io.IOException;
import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.command.easy.WorldlyPermissionEasyParameter;
import me.heldplayer.permissions.core.BasePermissions;
import me.heldplayer.permissions.core.PlayerPermissions;
import me.heldplayer.permissions.util.WorldlyPermission;
import net.specialattack.spacore.api.command.AbstractSubCommand;
import net.specialattack.spacore.api.command.ISubCommandHolder;
import net.specialattack.spacore.api.command.parameter.AnyPlayerEasyParameter;
import net.specialattack.spacore.util.ChatFormat;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class PlayerUnsetPermCommand extends AbstractSubCommand {

    private final Permissions plugin;

    private final AnyPlayerEasyParameter player;
    private final WorldlyPermissionEasyParameter.Only permission;

    public PlayerUnsetPermCommand(ISubCommandHolder command, Permissions plugin, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);
        this.plugin = plugin;
        this.addParameter(this.player = new AnyPlayerEasyParameter());
        this.addParameter(this.permission = new WorldlyPermissionEasyParameter.Only(() -> this.plugin.getPermissionsManager().getPlayer(this.player.get())));
        this.finish();
    }

    @Override
    public void runCommand(CommandSender sender) {
        String player = this.player.get();
        WorldlyPermission permission = this.permission.get();

        BasePermissions permissions = this.plugin.getPermissionsManager().getPlayer(player);

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
                this.plugin.savePermissions();
            } catch (IOException e) {
                sender.sendMessage(ChatColor.DARK_RED + "Applied the changes, but the changes didn't get saved!");
            }
        } else {
            sender.sendMessage(ChatFormat.format("%s did not have %s set specifically", ChatColor.GREEN, player, permission));
            sender.sendMessage(ChatFormat.format("The player does not have this permission set specifically", ChatColor.RED));
        }

        this.plugin.recalculatePermissions(player);
    }
}
