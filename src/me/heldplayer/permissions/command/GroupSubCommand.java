package me.heldplayer.permissions.command;

import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.command.group.GroupAddParentCommand;
import me.heldplayer.permissions.command.group.GroupAddRankableCommand;
import me.heldplayer.permissions.command.group.GroupDeleteCommand;
import me.heldplayer.permissions.command.group.GroupEmptyCommand;
import me.heldplayer.permissions.command.group.GroupInfoCommand;
import me.heldplayer.permissions.command.group.GroupListCommand;
import me.heldplayer.permissions.command.group.GroupNewCommand;
import me.heldplayer.permissions.command.group.GroupPlayersCommand;
import me.heldplayer.permissions.command.group.GroupRemoveParentCommand;
import me.heldplayer.permissions.command.group.GroupRemoveRankableCommand;
import me.heldplayer.permissions.command.group.GroupSetPermCommand;
import me.heldplayer.permissions.command.group.GroupUnsetPermCommand;
import net.specialattack.spacore.api.command.AbstractMultiSubCommand;
import net.specialattack.spacore.api.command.ISubCommandHolder;

public class GroupSubCommand extends AbstractMultiSubCommand {

    public GroupSubCommand(ISubCommandHolder command, Permissions plugin, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);

        new GroupListCommand(this, plugin, "list", "permissions.command.group.list");
        new GroupPlayersCommand(this, plugin, "players", "permissions.command.group.players");
        new GroupSetPermCommand(this, plugin, "setperm", "permissions.command.group.setperm");
        new GroupUnsetPermCommand(this, plugin, "unsetperm", "permissions.command.group.unsetperm");
        new GroupInfoCommand(this, plugin, "info", "permissions.command.group.info");
        new GroupNewCommand(this, plugin, "new", "permissions.command.group.new");
        new GroupDeleteCommand(this, plugin, "delete", "permissions.command.group.delete");
        new GroupEmptyCommand(this, plugin, "empty", "permissions.command.group.empty");
        new GroupAddParentCommand(this, plugin, "addparent", "permissions.command.group.addparent");
        new GroupRemoveParentCommand(this, plugin, "removeparent", "permissions.command.group.removeparent");
        new GroupAddRankableCommand(this, plugin, "addrankable", "permissions.command.group.addrankable");
        new GroupRemoveRankableCommand(this, plugin, "removerankable", "permissions.command.group.removerankable");
    }
}
