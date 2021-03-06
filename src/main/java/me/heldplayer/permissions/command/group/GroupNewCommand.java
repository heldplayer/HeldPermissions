package me.heldplayer.permissions.command.group;

import me.heldplayer.permissions.Consts;
import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.core.GroupPermissions;
import me.heldplayer.permissions.core.PermissionsManager;
import net.specialattack.spacore.api.command.AbstractSubCommand;
import net.specialattack.spacore.api.command.ISubCommandHolder;
import net.specialattack.spacore.api.command.parameter.AbstractEasyParameter;
import net.specialattack.spacore.api.command.parameter.StringEasyParameter;
import net.specialattack.spacore.util.ChatFormat;
import net.specialattack.spacore.util.ChatUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class GroupNewCommand extends AbstractSubCommand {

    private final Permissions plugin;

    private final AbstractEasyParameter<String> group;

    public GroupNewCommand(ISubCommandHolder command, Permissions plugin, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);
        this.plugin = plugin;
        this.addParameter(this.group = new StringEasyParameter().setName("name"));
        this.finish();
    }

    @Override
    public void runCommand(CommandSender sender) {
        String group = this.group.get();

        PermissionsManager permissionsManager = this.plugin.getPermissionsManager();
        GroupPermissions permissions = permissionsManager.getGroup(group);

        if (permissions != null) {
            sender.sendMessage(ChatFormat.format("Group '%s' already exists", ChatColor.RED, group));
            return;
        }

        permissions = new GroupPermissions(permissionsManager, group);
        permissionsManager.addGroup(permissions);

        Permissions.notify(ChatUtil.constructMessage(ChatColor.GREEN, "Created a new empty group '", ChatColor.WHITE,
                group, ChatColor.RESET, "'"), sender, Consts.PERM_LISTEN_CONFIG);

        this.plugin.savePermissionsBy(sender);
    }
}
