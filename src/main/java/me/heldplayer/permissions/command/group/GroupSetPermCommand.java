package me.heldplayer.permissions.command.group;

import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.command.easy.GroupEasyParameter;
import me.heldplayer.permissions.command.easy.WorldlyPermissionEasyParameter;
import me.heldplayer.permissions.core.BasePermissions;
import me.heldplayer.permissions.core.GroupPermissions;
import me.heldplayer.permissions.core.Perm;
import me.heldplayer.permissions.util.WorldlyPermission;
import net.specialattack.spacore.api.command.AbstractSubCommand;
import net.specialattack.spacore.api.command.ISubCommandHolder;
import net.specialattack.spacore.api.command.parameter.AbstractEasyParameter;
import net.specialattack.spacore.api.command.parameter.EnumEasyParameter;
import net.specialattack.spacore.util.ChatFormat;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class GroupSetPermCommand extends AbstractSubCommand {

    private final Permissions plugin;

    private final AbstractEasyParameter<GroupPermissions> group;
    private final AbstractEasyParameter<WorldlyPermission> permission;
    private final AbstractEasyParameter<Perm.Value> value;

    public GroupSetPermCommand(ISubCommandHolder command, Permissions plugin, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);
        this.plugin = plugin;
        this.addParameter(this.group = new GroupEasyParameter(plugin));
        this.addParameter(this.permission = new WorldlyPermissionEasyParameter());
        this.addParameter(this.value = new EnumEasyParameter<>(Perm.Value.values()).setName("allow/deny/never"));
        this.finish();
    }

    @Override
    public void runCommand(CommandSender sender) {
        GroupPermissions group = this.group.get();
        WorldlyPermission permission = this.permission.get();
        Perm.Value value = this.value.get();

        BasePermissions permissions = group;

        if (permission.world != null) {
            permissions = group.getWorldPermissions(permission.world);
        }

        permissions.setPermission(permission.permission, value);

        sender.sendMessage(ChatFormat.format("Set %s for %s to %s", ChatColor.GREEN, permission.permission, group.name, value));

        this.plugin.savePermissionsBy(sender);

        this.plugin.recalculatePermissions();
    }
}
