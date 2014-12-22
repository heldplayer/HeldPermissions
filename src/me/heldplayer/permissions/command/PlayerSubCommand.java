package me.heldplayer.permissions.command;

import me.heldplayer.permissions.command.player.*;
import net.specialattack.bukkit.core.command.AbstractMultiSubCommand;
import net.specialattack.bukkit.core.command.ISubCommandHolder;

public class PlayerSubCommand extends AbstractMultiSubCommand {

    public PlayerSubCommand(ISubCommandHolder command, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);

        new PlayerGroupsCommand(this, "groups", "permissions.command.player.groups");
        new PlayerSetGroupCommand(this, "setgroup", "permissions.command.player.setgroup");
        new PlayerAddGroupCommand(this, "addgroup", "permissions.command.player.addgroup");
        new PlayerRemoveGroupCommand(this, "removegroup", "permissions.command.player.removegroup");
        new PlayerSetPermCommand(this, "setperm", "permissions.command.player.setperm");
        new PlayerUnsetPermCommand(this, "unsetperm", "permissions.command.player.unsetperm");
    }

}
