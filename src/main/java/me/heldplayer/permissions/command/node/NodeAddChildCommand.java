package me.heldplayer.permissions.command.node;

import java.io.IOException;
import me.heldplayer.permissions.Consts;
import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.command.easy.AddedPermissionEasyParameter;
import me.heldplayer.permissions.command.easy.PermissionEasyParameter;
import me.heldplayer.permissions.core.added.AddedPermission;
import net.specialattack.spacore.api.command.AbstractSubCommand;
import net.specialattack.spacore.api.command.ISubCommandHolder;
import net.specialattack.spacore.api.command.parameter.AbstractEasyParameter;
import net.specialattack.spacore.util.ChatFormat;
import net.specialattack.spacore.util.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class NodeAddChildCommand extends AbstractSubCommand {

    private final Permissions plugin;

    private final AbstractEasyParameter<AddedPermission> parent;
    private final AbstractEasyParameter<String> child;

    public NodeAddChildCommand(ISubCommandHolder command, Permissions plugin, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);
        this.plugin = plugin;
        this.addParameter(this.parent = new AddedPermissionEasyParameter(plugin));
        this.addParameter(this.child = new PermissionEasyParameter().setName("child"));
        this.finish();
    }

    @Override
    public void runCommand(CommandSender sender) {
        AddedPermission parent = this.parent.get();
        String child = this.child.get();

        Permission parentNode = Bukkit.getPluginManager().getPermission(parent.name);
        Permission childNode = Bukkit.getPluginManager().getPermission(child);

        if (parentNode == null) {
            sender.sendMessage(ChatFormat.format("Permission '%s' doesn't exist?!", ChatColor.RED, parent.name));
            return;
        }

        if (childNode == null) {
            childNode = new Permission(child, PermissionDefault.OP);
            Bukkit.getPluginManager().addPermission(childNode);
        }

        parent.children.add(child);
        parentNode.getChildren().put(child, true);

        Permissions.notify(ChatUtil.constructMessage(ChatColor.GREEN, "Made '", ChatColor.WHITE,
                child, ChatColor.RESET, "' a child of '", ChatColor.WHITE,
                parent.name, ChatColor.RESET, "'"), sender, Consts.PERM_LISTEN_CONFIG);

        try {
            this.plugin.saveAddedPermissions();
        } catch (IOException e) {
            sender.sendMessage(ChatColor.DARK_RED + "Applied the changes, but the changes didn't get saved!");
        }
    }
}
