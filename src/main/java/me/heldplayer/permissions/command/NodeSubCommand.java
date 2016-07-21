package me.heldplayer.permissions.command;

import me.heldplayer.permissions.Consts;
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

        new NodeNewCommand(this, plugin, "new", Consts.PERM_COMMAND_NODE_NEW);
        new NodeDescriptionCommand(this, plugin, "description", Consts.PERM_COMMAND_NODE_DESCRIPTION);
        new NodeAddChildCommand(this, plugin, "addchild", Consts.PERM_COMMAND_NODE_ADDCHILD);
        new NodeRemoveChildCommand(this, plugin, "removechild", Consts.PERM_COMMAND_NODE_REMOVECHILD);
        new NodeDefaultCommand(this, plugin, "default", Consts.PERM_COMMAND_NODE_DEFAULT);
    }
}
