package me.heldplayer.permissions.command.node;

import java.io.IOException;
import me.heldplayer.permissions.Consts;
import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.command.easy.AddedPermissionEasyParameter;
import me.heldplayer.permissions.core.added.AddedPermission;
import net.specialattack.spacore.api.command.AbstractSubCommand;
import net.specialattack.spacore.api.command.ISubCommandHolder;
import net.specialattack.spacore.api.command.parameter.AbstractEasyParameter;
import net.specialattack.spacore.api.command.parameter.EnumEasyParameter;
import net.specialattack.spacore.util.ChatFormat;
import net.specialattack.spacore.util.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class NodeDefaultCommand extends AbstractSubCommand {

    private final Permissions plugin;

    private final AbstractEasyParameter<AddedPermission> permission;
    private final AbstractEasyParameter<PermissionsDefault> def;

    public NodeDefaultCommand(ISubCommandHolder command, Permissions plugin, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);
        this.plugin = plugin;
        this.addParameter(this.permission = new AddedPermissionEasyParameter(plugin));
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

        Permissions.notify(ChatUtil.constructMessage(ChatColor.GREEN, "Set the default value of '", ChatColor.WHITE,
                permission.name, ChatColor.RESET, "' to '", ChatColor.WHITE,
                def.name(), ChatColor.RESET, "'"), sender, Consts.PERM_LISTEN_CONFIG);

        try {
            this.plugin.saveAddedPermissions();
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
