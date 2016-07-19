package me.heldplayer.permissions.command;

import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.command.easy.PermissionEasyParameter;
import me.heldplayer.permissions.core.added.AddedPermission;
import net.specialattack.spacore.api.command.AbstractSubCommand;
import net.specialattack.spacore.api.command.ISubCommandHolder;
import net.specialattack.spacore.util.ChatFormat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

public class InfoSubCommand extends AbstractSubCommand {

    private final Permissions plugin;

    private final PermissionEasyParameter permission;

    public InfoSubCommand(ISubCommandHolder command, Permissions plugin, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);
        this.plugin = plugin;
        this.addParameter(this.permission = new PermissionEasyParameter());
        this.finish();
    }

    @Override
    public void runCommand(CommandSender sender) {
        String permission = this.permission.get();
        Permission perm = Bukkit.getPluginManager().getPermission(permission);

        if (perm == null) {
            AddedPermission addedPermission = this.plugin.getAddedPermissionsManager().getPermission(permission);
            if (addedPermission == null) {
                sender.sendMessage(ChatFormat.format("Unknown permission %s", ChatColor.RED, permission));
            } else {
                sender.sendMessage(ChatFormat.format("Permission %s is manually defined", ChatColor.GREEN, addedPermission.name));
                sender.sendMessage(ChatFormat.format("Default: %s", ChatColor.GREEN, addedPermission.defaultValue));
                if ((addedPermission.description != null) && (addedPermission.description.length() > 0)) {
                    sender.sendMessage(ChatFormat.format("Description: %s", ChatColor.GREEN, addedPermission.description));
                }
                if ((addedPermission.children != null) && (addedPermission.children.size() > 0)) {
                    sender.sendMessage(ChatFormat.format("Children: %s", ChatColor.GREEN, addedPermission.children.size()));
                }
            }
        } else {
            sender.sendMessage(ChatFormat.format("Info on permission %s:", ChatColor.GREEN, perm.getName()));
            sender.sendMessage(ChatFormat.format("Default: %s", ChatColor.GREEN, perm.getDefault()));
            if ((perm.getDescription() != null) && (perm.getDescription().length() > 0)) {
                sender.sendMessage(ChatFormat.format("Description: %s", ChatColor.GREEN, perm.getDescription()));
            }
            if ((perm.getChildren() != null) && (perm.getChildren().size() > 0)) {
                sender.sendMessage(ChatFormat.format("Children: %s", ChatColor.GREEN, perm.getChildren().size()));
            }
        }
    }
}
