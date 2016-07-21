package me.heldplayer.permissions.command.group;

import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.command.easy.GroupEasyParameter;
import me.heldplayer.permissions.core.GroupPermissions;
import net.specialattack.spacore.api.command.AbstractSubCommand;
import net.specialattack.spacore.api.command.ISubCommandHolder;
import net.specialattack.spacore.api.command.parameter.AbstractEasyParameter;
import net.specialattack.spacore.util.ChatFormat;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class GroupAddParentCommand extends AbstractSubCommand {

    private final Permissions plugin;

    private final AbstractEasyParameter<GroupPermissions> child;
    private final AbstractEasyParameter<GroupPermissions> parent;

    public GroupAddParentCommand(ISubCommandHolder command, Permissions plugin, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);
        this.plugin = plugin;
        this.addParameter(this.child = new GroupEasyParameter(plugin));
        this.addParameter(this.parent = new GroupEasyParameter.Parents(plugin, this.child, false));
        this.finish();
    }

    @Override
    public void runCommand(CommandSender sender) {
        GroupPermissions child = this.child.get();
        GroupPermissions parent = this.parent.get();

        if (child.doesInheritFrom(parent)) {
            sender.sendMessage(ChatFormat.format("Group '%s' is already a parent of '%s'", ChatColor.RED, parent.name, child.name));
            return;
        }

        if (parent.doesInheritFrom(child)) {
            sender.sendMessage(ChatFormat.format("Group '%s' can't be a parent of '%s' because it is already a child", ChatColor.RED, parent.name, child.name));
            return;
        }

        child.addParent(parent);
        sender.sendMessage(ChatFormat.format("Made '%s' a child of '%s'", ChatColor.GREEN, child.name, parent.name));

        this.plugin.recalculatePermissions();

        this.plugin.savePermissionsBy(sender);
    }
}
