package me.heldplayer.permissions.command;

import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.command.node.NodeAddChildCommand;
import me.heldplayer.permissions.command.node.NodeDefaultCommand;
import me.heldplayer.permissions.command.node.NodeDescriptionCommand;
import me.heldplayer.permissions.command.node.NodeNewCommand;
import me.heldplayer.permissions.command.node.NodeRemoveChildCommand;
import net.specialattack.spacore.api.command.AbstractMultiSubCommand;
import net.specialattack.spacore.api.command.ISubCommandHolder;

public class NodeSubCommand extends AbstractMultiSubCommand {

    public NodeSubCommand(ISubCommandHolder command, Permissions plugin, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);

        new NodeNewCommand(this, plugin, "new", "permissions.command.node.new");
        new NodeDescriptionCommand(this, plugin, "description", "permissions.command.node.description", "desc");
        new NodeAddChildCommand(this, plugin, "addchild", "permissions.command.node.addchild");
        new NodeRemoveChildCommand(this, plugin, "removechild", "permissions.command.node.removechild");
        new NodeDefaultCommand(this, plugin, "default", "permissions.command.node.default");
    }
}
