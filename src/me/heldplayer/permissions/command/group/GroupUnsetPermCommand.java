package me.heldplayer.permissions.command.group;

import java.io.IOException;
import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.command.easy.GroupEasyParameter;
import me.heldplayer.permissions.command.easy.WorldlyPermissionEasyParameter;
import me.heldplayer.permissions.core.BasePermissions;
import me.heldplayer.permissions.core.GroupPermissions;
import me.heldplayer.permissions.util.WorldlyPermission;
import net.specialattack.spacore.api.command.AbstractSubCommand;
import net.specialattack.spacore.api.command.ISubCommandHolder;
import net.specialattack.spacore.util.ChatFormat;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class GroupUnsetPermCommand extends AbstractSubCommand {

    private final Permissions plugin;

    private final GroupEasyParameter group;
    private final WorldlyPermissionEasyParameter.Only permission;

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

        if (!permissions.allow.contains(permission.permission) && !permissions.deny.contains(permission.permission)) {
            sender.sendMessage(ChatFormat.format("The group does not have this permission set specifically", ChatColor.RED));
            return;
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
            sender.sendMessage(ChatFormat.format("Unset %s from %s", ChatColor.GREEN, permission, group.name));

            try {
                this.plugin.savePermissions();
            } catch (IOException e) {
                sender.sendMessage(ChatColor.DARK_RED + "Applied the changes, but the changes didn't get saved!");
            }
        } else {
            sender.sendMessage(ChatFormat.format("The group does not have this permission set specifically", ChatColor.RED));
        }

        this.plugin.recalculatePermissions();
    }
}
