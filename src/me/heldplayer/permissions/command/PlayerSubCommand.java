package me.heldplayer.permissions.command;

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

        new PlayerGroupsCommand(this, plugin, "groups", "permissions.command.player.groups");
        new PlayerSetGroupCommand(this, plugin, "setgroup", "permissions.command.player.setgroup");
        new PlayerAddGroupCommand(this, plugin, "addgroup", "permissions.command.player.addgroup");
        new PlayerRemoveGroupCommand(this, plugin, "removegroup", "permissions.command.player.removegroup");
        new PlayerSetPermCommand(this, plugin, "setperm", "permissions.command.player.setperm");
        new PlayerUnsetPermCommand(this, plugin, "unsetperm", "permissions.command.player.unsetperm");
    }
}
