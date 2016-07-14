package me.heldplayer.permissions.command.node;

import java.io.IOException;
import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.core.added.AddedPermission;
import me.heldplayer.permissions.core.added.AddedPermissionsManager;
import net.specialattack.bukkit.core.command.AbstractSubCommand;
import net.specialattack.bukkit.core.command.ISubCommandHolder;
import net.specialattack.bukkit.core.command.easy.parameter.StringEasyParameter;
import net.specialattack.bukkit.core.util.ChatFormat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

public class NodeNewCommand extends AbstractSubCommand {

    private final StringEasyParameter permission;

    public NodeNewCommand(ISubCommandHolder command, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);
        this.addParameter(this.permission = new StringEasyParameter().setName("permission"));
        this.finish();
    }

    @Override
    public void runCommand(CommandSender sender) {
        String permission = this.permission.get();

        AddedPermissionsManager manager = Permissions.instance.getAddedPermissionsManager();

        AddedPermission permissions = manager.getPermission(permission);

        if (permissions != null) {
            sender.sendMessage(ChatFormat.format("Permissions definition '%s' already exists", ChatColor.RED, permission));
            return;
        }

        Permission node = Bukkit.getPluginManager().getPermission(permission);

        if (node != null) {
            sender.sendMessage(ChatFormat.format("Permission '%s' already exists", ChatColor.RED, permission));
            return;
        }

        permissions = new AddedPermission(permission);
        node = new Permission(permission);
        manager.addedPermissions.add(permissions);
        Bukkit.getPluginManager().addPermission(node);

        sender.sendMessage(ChatFormat.format("Created a new permissions definition '%s'", ChatColor.GREEN, permission));

        try {
            Permissions.instance.saveAddedPermissions();
        } catch (IOException e) {
            sender.sendMessage(ChatColor.DARK_RED + "Applied the changes, but the changes didn't get saved!");
        }
    }
}
