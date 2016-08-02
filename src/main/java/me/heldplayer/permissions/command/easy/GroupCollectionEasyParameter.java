package me.heldplayer.permissions.command.easy;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.core.GroupPermissions;
import me.heldplayer.permissions.core.PlayerPermissions;
import me.heldplayer.permissions.util.TabHelper;
import net.specialattack.spacore.api.command.CommandException;
import net.specialattack.spacore.api.command.parameter.AbstractEasyParameter;
import org.bukkit.command.CommandSender;

public class GroupCollectionEasyParameter extends AbstractEasyParameter.Multi<List<GroupPermissions>> {

    private final Permissions plugin;

    public GroupCollectionEasyParameter(Permissions plugin) {
        this.setName("group");
        this.plugin = plugin;
    }

    @Override
    public boolean parse(CommandSender sender, String value) {
        List<GroupPermissions> result = new ArrayList<>();
        String[] split = value.split(" ");
        for (String str : split) {
            GroupPermissions group = this.plugin.getPermissionsManager().getGroup(str);
            if (group == null) {
                throw new CommandException("Group %s does not exist", str);
            }
            result.add(group);
        }

        if (result.isEmpty()) {
            this.setValue(null);
            return false;
        }

        this.setValue(result);
        return true;
    }

    @Override
    public List<String> getTabComplete(CommandSender sender, String input) {
        return TabHelper.tabAnyGroup(this.plugin.getPermissionsManager());
    }

    public static class PlayerIn extends AbstractEasyParameter.Multi<List<GroupPermissions>> {

        private final Permissions plugin;

        private final Supplier<? extends PlayerPermissions> source;

        public PlayerIn(Permissions plugin, Supplier<? extends PlayerPermissions> source) {
            this.setName("group");
            this.plugin = plugin;
            this.source = source;
        }

        @Override
        public boolean parse(CommandSender sender, String value) {
            List<GroupPermissions> result = new ArrayList<>();
            for (String name : value.split(" ")) {
                GroupPermissions group = this.plugin.getPermissionsManager().getGroup(name);
                if (group == null) {
                    throw new CommandException("Group %s does not exist!", value);
                }
                result.add(group);
            }
            this.setValue(result);
            return true;
        }

        @Override
        public List<String> getTabComplete(CommandSender sender, String input) {
            PlayerPermissions player = this.source.get();
            if (player == null) {
                return TabHelper.tabAnyGroup(this.plugin.getPermissionsManager());
            } else {
                return TabHelper.tabAnyGroupIn(player);
            }
        }
    }
}
