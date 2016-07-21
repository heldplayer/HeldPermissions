package me.heldplayer.permissions.command.easy;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.core.GroupPermissions;
import me.heldplayer.permissions.util.TabHelper;
import net.specialattack.spacore.api.command.CommandException;
import net.specialattack.spacore.api.command.parameter.AbstractEasyParameter;
import org.bukkit.command.CommandSender;

public class GroupEasyParameter extends AbstractEasyParameter<GroupPermissions> {

    private final Permissions plugin;

    public GroupEasyParameter(Permissions plugin) {
        this.setName("group");
        this.plugin = plugin;
    }

    @Override
    public boolean parse(CommandSender sender, String value) {
        GroupPermissions result = this.plugin.getPermissionsManager().getGroup(value);
        this.setValue(result);
        if (result == null) {
            throw new CommandException("Group %s does not exist!", value);
        }
        return true;
    }

    @Override
    public List<String> getTabComplete(CommandSender sender, String input) {
        return TabHelper.tabAnyGroup(this.plugin.getPermissionsManager());
    }

    public static class Parents extends AbstractEasyParameter<GroupPermissions> {

        private final Permissions plugin;

        private final Supplier<? extends GroupPermissions> source;
        private final boolean allow;

        public Parents(Permissions plugin, Supplier<? extends GroupPermissions> source, boolean allow) {
            this.setName("parent");
            this.plugin = plugin;
            this.source = source;
            this.allow = allow;
        }

        @Override
        public boolean parse(CommandSender sender, String value) {
            GroupPermissions parent = this.source.get();
            if (this.allow) {
                for (String child : parent.getParents()) {
                    if (child.equalsIgnoreCase(value)) {
                        this.setValue(this.plugin.getPermissionsManager().getGroup(child));
                        return true;
                    }
                }
                this.setValue(null);
                return false;
            } else {
                for (String child : parent.getAllGroupNames()) {
                    if (child.equalsIgnoreCase(value)) {
                        this.setValue(null);
                        throw new CommandException("%s is a parent of %s!", value, parent.name);
                    }
                }
                GroupPermissions result = this.plugin.getPermissionsManager().getGroup(value);
                this.setValue(result);
                if (result == null) {
                    throw new CommandException("Group %s does not exist!", value);
                }
                return true;
            }
        }

        @Override
        public List<String> getTabComplete(CommandSender sender, String input) {
            GroupPermissions parent = this.source.get();
            if (this.allow) {
                return new ArrayList<>(parent.getParents());
            } else {
                return TabHelper.tabAnyGroupExcept(this.plugin.getPermissionsManager(), parent.getAllGroupNames(), parent.name);
            }
        }
    }

    public static class Rankables extends AbstractEasyParameter<GroupPermissions> {

        private final Permissions plugin;

        private final Supplier<? extends GroupPermissions> source;
        private final boolean allow;

        public Rankables(Permissions plugin, Supplier<? extends GroupPermissions> source, boolean allow) {
            this.setName("rankable");
            this.plugin = plugin;
            this.source = source;
            this.allow = allow;
        }

        @Override
        public boolean parse(CommandSender sender, String value) {
            GroupPermissions group = this.source.get();
            if (this.allow) {
                for (String child : group.getRankables()) {
                    if (child.equalsIgnoreCase(value)) {
                        this.setValue(this.plugin.getPermissionsManager().getGroup(child));
                        return true;
                    }
                }
                this.setValue(null);
                return false;
            } else {
                for (String child : group.getAllRankables()) {
                    if (child.equalsIgnoreCase(value)) {
                        this.setValue(null);
                        throw new CommandException("%s can already rank %s!", group.name, value);
                    }
                }
                GroupPermissions result = this.plugin.getPermissionsManager().getGroup(value);
                this.setValue(result);
                if (result == null) {
                    throw new CommandException("Group %s does not exist!", value);
                }
                return true;
            }
        }

        @Override
        public List<String> getTabComplete(CommandSender sender, String input) {
            GroupPermissions group = this.source.get();
            if (this.allow) {
                return new ArrayList<>(group.getRankables());
            } else {
                return TabHelper.tabAnyGroupExcept(this.plugin.getPermissionsManager(), group.getAllRankables());
            }
        }
    }
}
