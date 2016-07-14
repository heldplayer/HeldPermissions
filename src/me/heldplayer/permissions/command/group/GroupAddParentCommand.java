package me.heldplayer.permissions.command.group;

import java.io.IOException;
import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.command.easy.GroupEasyParameter;
import me.heldplayer.permissions.core.GroupPermissions;
import net.specialattack.bukkit.core.command.AbstractSubCommand;
import net.specialattack.bukkit.core.command.ISubCommandHolder;
import net.specialattack.bukkit.core.util.ChatFormat;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class GroupAddParentCommand extends AbstractSubCommand {

    private final GroupEasyParameter child;
    private final GroupEasyParameter.Parents parent;

    public GroupAddParentCommand(ISubCommandHolder command, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);
        this.addParameter(this.child = new GroupEasyParameter());
        this.addParameter(this.parent = new GroupEasyParameter.Parents(this.child, false));
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

        Permissions.instance.recalculatePermissions();

        try {
            Permissions.instance.savePermissions();
        } catch (IOException e) {
            sender.sendMessage(ChatColor.DARK_RED + "Applied the changes, but the changes didn't get saved!");
        }
    }
}
