package me.heldplayer.permissions.command.node;

import java.io.IOException;
import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.command.easy.AddedPermissionEasyParameter;
import me.heldplayer.permissions.core.added.AddedPermission;
import net.specialattack.bukkit.core.command.AbstractSubCommand;
import net.specialattack.bukkit.core.command.ISubCommandHolder;
import net.specialattack.bukkit.core.command.easy.parameter.EnumEasyParameter;
import net.specialattack.bukkit.core.util.ChatFormat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class NodeDefaultCommand extends AbstractSubCommand {

    private final AddedPermissionEasyParameter permission;
    private final EnumEasyParameter<PermissionsDefault> def;

    public NodeDefaultCommand(ISubCommandHolder command, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);
        this.addParameter(this.permission = new AddedPermissionEasyParameter());
        this.addParameter(this.def = new EnumEasyParameter<>(PermissionsDefault.values()));
        this.finish();
    }

    @Override
    public void runCommand(CommandSender sender) {
        AddedPermission permission = this.permission.get();
        PermissionsDefault def = this.def.get();

        Permission node = Bukkit.getPluginManager().getPermission(permission.name);

        if (node == null) {
            sender.sendMessage(ChatFormat.format("Permission '%s' doesn't exist?!", ChatColor.RED, permission.name));
            return;
        }

        node.setDefault(def.value);
        permission.defaultValue = def.value;

        sender.sendMessage(ChatFormat.format("Set the default value of '%s' to %s", ChatColor.GREEN, permission.name, def.name()));

        try {
            Permissions.instance.saveAddedPermissions();
        } catch (IOException e) {
            sender.sendMessage(ChatColor.DARK_RED + "Applied the changes, but the changes didn't get saved!");
        }
    }

    private enum PermissionsDefault {
        TRUE(PermissionDefault.TRUE), FALSE(PermissionDefault.FALSE), OP(PermissionDefault.OP), NOT_OP(PermissionDefault.NOT_OP);

        public final PermissionDefault value;

        PermissionsDefault(PermissionDefault value) {
            this.value = value;
        }
    }
}
