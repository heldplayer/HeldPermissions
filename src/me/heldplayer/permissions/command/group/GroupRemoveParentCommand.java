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

public class GroupRemoveParentCommand extends AbstractSubCommand {

    private final GroupEasyParameter child;
    private final GroupEasyParameter.Parents parent;

    public GroupRemoveParentCommand(ISubCommandHolder command, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);
        this.addParameter(this.child = new GroupEasyParameter());
        this.addParameter(this.parent = new GroupEasyParameter.Parents(this.child, true).setName("parent"));
        this.finish();
    }

    @Override
    public void runCommand(CommandSender sender) {
        GroupPermissions child = this.child.get();
        GroupPermissions parent = this.parent.get();

        if (!child.doesInheritFrom(parent)) {
            sender.sendMessage(ChatFormat.format("Group '%s' isn't a parent of '%s'", ChatColor.RED, parent.name, child.name));
            return;
        }

        child.removeParent(parent);
        sender.sendMessage(ChatFormat.format("Removed parent '%s' of '%s'", ChatColor.GREEN, parent.name, child.name));

        Permissions.instance.recalculatePermissions();

        try {
            Permissions.instance.savePermissions();
        } catch (IOException e) {
            sender.sendMessage(ChatColor.DARK_RED + "Applied the changes, but the changes didn't get saved!");
        }
    }
}
