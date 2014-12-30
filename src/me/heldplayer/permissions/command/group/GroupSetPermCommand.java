package me.heldplayer.permissions.command.group;

import java.io.IOException;
import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.command.easy.GroupEasyParameter;
import me.heldplayer.permissions.command.easy.WorldlyPermissionEasyParameter;
import me.heldplayer.permissions.core.BasePermissions;
import me.heldplayer.permissions.core.GroupPermissions;
import me.heldplayer.permissions.util.WorldlyPermission;
import net.specialattack.bukkit.core.command.AbstractSubCommand;
import net.specialattack.bukkit.core.command.ISubCommandHolder;
import net.specialattack.bukkit.core.command.easy.parameter.BooleanEasyParameter;
import net.specialattack.bukkit.core.util.ChatFormat;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class GroupSetPermCommand extends AbstractSubCommand {

    private final GroupEasyParameter group;
    private final WorldlyPermissionEasyParameter permission;
    private final BooleanEasyParameter permissionValue;

    public GroupSetPermCommand(ISubCommandHolder command, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);
        this.addParameter(this.group = new GroupEasyParameter());
        this.addParameter(this.permission = new WorldlyPermissionEasyParameter());
        this.addParameter(this.permissionValue = new BooleanEasyParameter());
        this.finish();
    }

    @Override
    public void runCommand(CommandSender sender) {
        GroupPermissions group = this.group.getValue();
        WorldlyPermission permission = this.permission.getValue();
        boolean permissionValue = this.permissionValue.getValue();

        BasePermissions permissions = group;

        if (permission.world != null) {
            permissions = group.getWorldPermissions(permission.world);
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

        sender.sendMessage(ChatFormat.format("Set %s for %s to %s", ChatColor.GREEN, permission.permission, group.name, permissionValue));

        try {
            Permissions.instance.savePermissions();
        } catch (IOException e) {
            sender.sendMessage(ChatColor.DARK_RED + "Applied the changes, but the changes didn't get saved!");
        }

        Permissions.instance.recalculatePermissions();
    }

}
