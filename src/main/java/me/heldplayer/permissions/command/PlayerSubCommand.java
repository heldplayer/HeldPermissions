package me.heldplayer.permissions.command;

import me.heldplayer.permissions.Consts;
import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.command.player.PlayerAddGroupCommand;
import me.heldplayer.permissions.command.player.PlayerGroupsCommand;
import me.heldplayer.permissions.command.player.PlayerRemoveGroupCommand;
import me.heldplayer.permissions.command.player.PlayerSetGroupCommand;
import me.heldplayer.permissions.command.player.PlayerSetPermCommand;
import me.heldplayer.permissions.command.player.PlayerUnsetPermCommand;
import net.specialattack.spacore.api.command.AbstractMultiSubCommand;
import net.specialattack.spacore.api.command.ISubCommandHolder;

public class PlayerSubCommand extends AbstractMultiSubCommand {

    public PlayerSubCommand(ISubCommandHolder command, Permissions plugin, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);

        new PlayerGroupsCommand(this, plugin, "groups", Consts.PERM_COMMAND_PLAYER_GROUPS);
        new PlayerSetGroupCommand(this, plugin, "setgroup", Consts.PERM_COMMAND_PLAYER_SETGROUP);
        new PlayerAddGroupCommand(this, plugin, "addgroup", Consts.PERM_COMMAND_PLAYER_ADDGROUP);
        new PlayerRemoveGroupCommand(this, plugin, "removegroup", Consts.PERM_COMMAND_PLAYER_REMOVEGROUP);
        new PlayerSetPermCommand(this, plugin, "setperm", Consts.PERM_COMMAND_PLAYER_SETPERM);
        new PlayerUnsetPermCommand(this, plugin, "unsetperm", Consts.PERM_COMMAND_PLAYER_UNSETPERM);
    }
}
