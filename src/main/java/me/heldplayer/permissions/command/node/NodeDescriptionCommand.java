package me.heldplayer.permissions.command.node;

import java.io.IOException;
import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.command.easy.AddedPermissionEasyParameter;
import me.heldplayer.permissions.core.added.AddedPermission;
import net.specialattack.spacore.api.command.AbstractSubCommand;
import net.specialattack.spacore.api.command.ISubCommandHolder;
import net.specialattack.spacore.api.command.parameter.AbstractEasyParameter;
import net.specialattack.spacore.api.command.parameter.StringEasyParameter;
import net.specialattack.spacore.util.ChatFormat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

public class NodeDescriptionCommand extends AbstractSubCommand {

    private final Permissions plugin;

    private final AbstractEasyParameter<AddedPermission> permission;
    private final AbstractEasyParameter<String> description;

    public NodeDescriptionCommand(ISubCommandHolder command, Permissions plugin, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);
        this.plugin = plugin;
        this.addParameter(this.permission = new AddedPermissionEasyParameter(plugin));
        this.addParameter(this.description = new StringEasyParameter().setTakeAll().setName("description"));
        this.finish();
    }

    @Override
    public void runCommand(CommandSender sender) {
        AddedPermission permission = this.permission.get();
        String description = this.description.get();

        Permission node = Bukkit.getPluginManager().getPermission(permission.name);

        if (node == null) {
            sender.sendMessage(ChatFormat.format("Permission '%s' doesn't exist?!", ChatColor.RED, permission.name));
            return;
        }

        permission.description = description;
        node.setDescription(description);

        sender.sendMessage(ChatFormat.format("Changed the description of '%s' to '%s'", ChatColor.GREEN, permission.name, description));

        try {
            this.plugin.saveAddedPermissions();
        } catch (IOException e) {
            sender.sendMessage(ChatColor.DARK_RED + "Applied the changes, but the changes didn't get saved!");
        }
    }
}
