package me.heldplayer.permissions.command;

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
import net.specialattack.bukkit.core.command.AbstractMultiSubCommand;
import net.specialattack.bukkit.core.command.ISubCommandHolder;

public class GroupSubCommand extends AbstractMultiSubCommand {

    public GroupSubCommand(ISubCommandHolder command, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);

        new GroupListCommand(this, "list", "permissions.command.group.list");
        new GroupPlayersCommand(this, "players", "permissions.command.group.players");
        new GroupSetPermCommand(this, "setperm", "permissions.command.group.setperm");
        new GroupUnsetPermCommand(this, "unsetperm", "permissions.command.group.unsetperm");
        new GroupInfoCommand(this, "info", "permissions.command.group.info");
        new GroupNewCommand(this, "new", "permissions.command.group.new");
        new GroupDeleteCommand(this, "delete", "permissions.command.group.delete");
        new GroupEmptyCommand(this, "empty", "permissions.command.group.empty");
        new GroupAddParentCommand(this, "addparent", "permissions.command.group.addparent");
        new GroupRemoveParentCommand(this, "removeparent", "permissions.command.group.removeparent");
        new GroupAddRankableCommand(this, "addrankable", "permissions.command.group.addrankable");
        new GroupRemoveRankableCommand(this, "removerankable", "permissions.command.group.removerankable");
    }
}
