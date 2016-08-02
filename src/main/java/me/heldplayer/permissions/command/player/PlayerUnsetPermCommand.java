package me.heldplayer.permissions.command.player;

import me.heldplayer.permissions.Consts;
import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.command.easy.WorldlyPermissionEasyParameter;
import me.heldplayer.permissions.core.BasePermissions;
import me.heldplayer.permissions.core.PlayerPermissions;
import me.heldplayer.permissions.util.WorldlyPermission;
import net.specialattack.spacore.api.command.AbstractSubCommand;
import net.specialattack.spacore.api.command.ISubCommandHolder;
import net.specialattack.spacore.api.command.parameter.AbstractEasyParameter;
import net.specialattack.spacore.api.command.parameter.AnyPlayerEasyParameter;
import net.specialattack.spacore.util.ChatFormat;
import net.specialattack.spacore.util.ChatUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class PlayerUnsetPermCommand extends AbstractSubCommand {

    private final Permissions plugin;

    private final AbstractEasyParameter<String> player;
    private final AbstractEasyParameter<WorldlyPermission> permission;

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

        if (!permissions.isDefined(permission.permission)) {
            sender.sendMessage(ChatFormat.format("The group does not have this permission set specifically", ChatColor.RED));
            return;
        }

        permissions.setPermission(permission.permission, null);

        Permissions.notify(ChatUtil.constructMessage(ChatColor.GREEN, "Unset ", ChatColor.WHITE, permission,
                ChatColor.RESET, " for ", ChatColor.WHITE, player), sender, Consts.PERM_LISTEN_CONFIG);

        this.plugin.savePermissionsBy(sender);

        this.plugin.recalculatePermissions(player);
    }
}
