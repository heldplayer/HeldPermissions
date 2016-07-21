package me.heldplayer.permissions.command.node;

import java.io.IOException;
import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.command.easy.AddedPermissionEasyParameter;
import me.heldplayer.permissions.core.added.AddedPermission;
import net.specialattack.spacore.api.command.AbstractSubCommand;
import net.specialattack.spacore.api.command.ISubCommandHolder;
import net.specialattack.spacore.api.command.parameter.AbstractEasyParameter;
import net.specialattack.spacore.util.ChatFormat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

public class NodeRemoveChildCommand extends AbstractSubCommand {

    private final Permissions plugin;

    private final AbstractEasyParameter<AddedPermission> parent;
    private final AbstractEasyParameter<String> child;

    public NodeRemoveChildCommand(ISubCommandHolder command, Permissions plugin, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);
        this.plugin = plugin;
        this.addParameter(this.parent = new AddedPermissionEasyParameter(plugin));
        this.addParameter(this.child = new AddedPermissionEasyParameter.Child(this.parent).setName("child"));
        this.finish();
    }

    @Override
    public void runCommand(CommandSender sender) {
        AddedPermission parent = this.parent.get();
        String child = this.child.get();

        Permission parentNode = Bukkit.getPluginManager().getPermission(parent.name);

        if (parentNode == null) {
            sender.sendMessage(ChatFormat.format("Permission '%s' doesn't exist?!", ChatColor.RED, parent.name));
            return;
        }

        parent.children.remove(child);
        parentNode.getChildren().remove(child);

        sender.sendMessage(ChatFormat.format("'%s' is no longer a child of '%s'", ChatColor.GREEN, child, parent.name));

        try {
            this.plugin.saveAddedPermissions();
        } catch (IOException e) {
            sender.sendMessage(ChatColor.DARK_RED + "Applied the changes, but the changes didn't get saved!");
        }
    }
}
