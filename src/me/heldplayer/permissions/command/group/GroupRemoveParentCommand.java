package me.heldplayer.permissions.command.group;

import java.io.IOException;
import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.command.easy.GroupEasyParameter;
import me.heldplayer.permissions.core.GroupPermissions;
import net.specialattack.spacore.api.command.AbstractSubCommand;
import net.specialattack.spacore.api.command.ISubCommandHolder;
import net.specialattack.spacore.util.ChatFormat;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class GroupRemoveParentCommand extends AbstractSubCommand {

    private final Permissions plugin;

    private final GroupEasyParameter child;
    private final GroupEasyParameter.Parents parent;

    public GroupRemoveParentCommand(ISubCommandHolder command, Permissions plugin, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);
        this.plugin = plugin;
        this.addParameter(this.child = new GroupEasyParameter(plugin));
        this.addParameter(this.parent = new GroupEasyParameter.Parents(plugin, this.child, true).setName("parent"));
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

        this.plugin.recalculatePermissions();

        try {
            this.plugin.savePermissions();
        } catch (IOException e) {
            sender.sendMessage(ChatColor.DARK_RED + "Applied the changes, but the changes didn't get saved!");
        }
    }
}
