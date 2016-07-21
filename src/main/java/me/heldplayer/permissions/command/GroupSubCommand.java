package me.heldplayer.permissions.command;

import me.heldplayer.permissions.Consts;
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

        new GroupListCommand(this, plugin, "list", Consts.PERM_COMMAND_GROUP_LIST);
        new GroupPlayersCommand(this, plugin, "players", Consts.PERM_COMMAND_GROUP_PLAYERS);
        new GroupSetPermCommand(this, plugin, "setperm", Consts.PERM_COMMAND_GROUP_SETPERM);
        new GroupUnsetPermCommand(this, plugin, "unsetperm", Consts.PERM_COMMAND_GROUP_UNSETPERM);
        new GroupInfoCommand(this, plugin, "info", Consts.PERM_COMMAND_GROUP_INFO);
        new GroupNewCommand(this, plugin, "new", Consts.PERM_COMMAND_GROUP_NEW);
        new GroupDeleteCommand(this, plugin, "delete", Consts.PERM_COMMAND_GROUP_DELETE);
        new GroupEmptyCommand(this, plugin, "empty", Consts.PERM_COMMAND_GROUP_EMPTY);
        new GroupAddParentCommand(this, plugin, "addparent", Consts.PERM_COMMAND_GROUP_ADDPARENT);
        new GroupRemoveParentCommand(this, plugin, "removeparent", Consts.PERM_COMMAND_GROUP_REMOVEPARENT);
        new GroupAddRankableCommand(this, plugin, "addrankable", Consts.PERM_COMMAND_GROUP_ADDRANKABLE);
        new GroupRemoveRankableCommand(this, plugin, "removerankable", Consts.PERM_COMMAND_GROUP_REMOVERANKABLE);
    }
}
