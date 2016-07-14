package me.heldplayer.permissions.command;

import me.heldplayer.permissions.command.node.NodeAddChildCommand;
import me.heldplayer.permissions.command.node.NodeDefaultCommand;
import me.heldplayer.permissions.command.node.NodeDescriptionCommand;
import me.heldplayer.permissions.command.node.NodeNewCommand;
import me.heldplayer.permissions.command.node.NodeRemoveChildCommand;
import net.specialattack.bukkit.core.command.AbstractMultiSubCommand;
import net.specialattack.bukkit.core.command.ISubCommandHolder;

public class NodeSubCommand extends AbstractMultiSubCommand {

    public NodeSubCommand(ISubCommandHolder command, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);

        new NodeNewCommand(this, "new", "permissions.command.node.new");
        new NodeDescriptionCommand(this, "description", "permissions.command.node.description", "desc");
        new NodeAddChildCommand(this, "addchild", "permissions.command.node.addchild");
        new NodeRemoveChildCommand(this, "removechild", "permissions.command.node.removechild");
        new NodeDefaultCommand(this, "default", "permissions.command.node.default");
    }
}
