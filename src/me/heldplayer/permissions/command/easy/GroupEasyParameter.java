package me.heldplayer.permissions.command.easy;

import java.util.ArrayList;
import java.util.List;
import me.heldplayer.permissions.Permissions;
import me.heldplayer.permissions.core.GroupPermissions;
import me.heldplayer.permissions.util.TabHelper;
import net.specialattack.bukkit.core.command.CommandException;
import net.specialattack.bukkit.core.command.easy.parameter.AbstractEasyParameter;
import net.specialattack.bukkit.core.util.IDataSource;
import org.bukkit.command.CommandSender;

public class GroupEasyParameter extends AbstractEasyParameter<GroupPermissions> {

    public GroupEasyParameter() {
        this.setName("group");
    }

    @Override
    public boolean parse(CommandSender sender, String value) {
        GroupPermissions result = Permissions.instance.getPermissionsManager().getGroup(value);
        this.setValue(result);
        if (result == null) {
            throw new CommandException("Group %s does not exist!", value);
        }
        return true;
    }

    @Override
    public List<String> getTabComplete(CommandSender sender, String input) {
        return TabHelper.tabAnyGroup();
    }

    public static class Parents extends AbstractEasyParameter<GroupPermissions> {

        private final IDataSource<? extends GroupPermissions> source;
        private final boolean allow;

        public Parents(IDataSource<? extends GroupPermissions> source, boolean allow) {
            this.setName("parent");
            this.source = source;
            this.allow = allow;
        }

        @Override
        public boolean parse(CommandSender sender, String value) {
            GroupPermissions parent = this.source.getValue();
            if (this.allow) {
                for (String child : parent.getParents()) {
                    if (child.equalsIgnoreCase(value)) {
                        this.setValue(Permissions.instance.getPermissionsManager().getGroup(child));
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
                GroupPermissions result = Permissions.instance.getPermissionsManager().getGroup(value);
                this.setValue(result);
                if (result == null) {
                    throw new CommandException("Group %s does not exist!", value);
                }
                return true;
            }
        }

        @Override
        public List<String> getTabComplete(CommandSender sender, String input) {
            GroupPermissions parent = this.source.getValue();
            if (this.allow) {
                return new ArrayList<String>(parent.getParents());
            } else {
                return TabHelper.tabAnyGroupExcept(parent.getAllGroupNames(), parent.name);
            }
        }

    }

    public static class Rankables extends AbstractEasyParameter<GroupPermissions> {

        private final IDataSource<? extends GroupPermissions> source;
        private final boolean allow;

        public Rankables(IDataSource<? extends GroupPermissions> source, boolean allow) {
            this.setName("rankable");
            this.source = source;
            this.allow = allow;
        }

        @Override
        public boolean parse(CommandSender sender, String value) {
            GroupPermissions group = source.getValue();
            if (this.allow) {
                for (String child : group.getRankables()) {
                    if (child.equalsIgnoreCase(value)) {
                        this.setValue(Permissions.instance.getPermissionsManager().getGroup(child));
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
                GroupPermissions result = Permissions.instance.getPermissionsManager().getGroup(value);
                this.setValue(result);
                if (result == null) {
                    throw new CommandException("Group %s does not exist!", value);
                }
                return true;
            }
        }

        @Override
        public List<String> getTabComplete(CommandSender sender, String input) {
            GroupPermissions group = source.getValue();
            if (this.allow) {
                return new ArrayList<String>(group.getRankables());
            } else {
                return TabHelper.tabAnyGroupExcept(group.getAllRankables());
            }
        }

    }

}
