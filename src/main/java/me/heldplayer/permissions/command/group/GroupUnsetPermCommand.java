package me.heldplayer.permissions.command.group;

import me.heldplayer.permissions.Consts;
import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.command.easy.GroupEasyParameter;
import me.heldplayer.permissions.command.easy.WorldlyPermissionEasyParameter;
import me.heldplayer.permissions.core.BasePermissions;
import me.heldplayer.permissions.core.GroupPermissions;
import me.heldplayer.permissions.util.WorldlyPermission;
import net.specialattack.spacore.api.command.AbstractSubCommand;
import net.specialattack.spacore.api.command.ISubCommandHolder;
import net.specialattack.spacore.api.command.parameter.AbstractEasyParameter;
import net.specialattack.spacore.util.ChatFormat;
import net.specialattack.spacore.util.ChatUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class GroupUnsetPermCommand extends AbstractSubCommand {

    private final Permissions plugin;

    private final AbstractEasyParameter<GroupPermissions> group;
    private final AbstractEasyParameter<WorldlyPermission> permission;

    public GroupUnsetPermCommand(ISubCommandHolder command, Permissions plugin, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);
        this.plugin = plugin;
        this.addParameter(this.group = new GroupEasyParameter(plugin));
        this.addParameter(this.permission = new WorldlyPermissionEasyParameter.Only(this.group));
        this.finish();
    }

    @Override
    public void runCommand(CommandSender sender) {
        GroupPermissions group = this.group.get();
        WorldlyPermission permission = this.permission.get();

        BasePermissions permissions = group;

        if (permission.world != null) {
            permissions = group.getWorldPermissions(permission.world);
        }

        if (!permissions.isDefined(permission.permission)) {
            sender.sendMessage(ChatFormat.format("The group does not have this permission set specifically", ChatColor.RED));
            return;
        }

        permissions.setPermission(permission.permission, null);

        Permissions.notify(ChatUtil.constructMessage(ChatColor.GREEN, "Unset ", ChatColor.WHITE, permission,
                ChatColor.RESET, " for ", ChatColor.WHITE, group.name), sender, Consts.PERM_LISTEN_CONFIG);

        this.plugin.savePermissionsBy(sender);

        this.plugin.recalculatePermissions();
    }
}
