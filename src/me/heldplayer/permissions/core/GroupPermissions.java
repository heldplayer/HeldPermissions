
package me.heldplayer.permissions.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import org.bukkit.configuration.ConfigurationSection;

public class GroupPermissions extends WorldlyPermissions {

    public final String name;

    public List<GroupPermissions> inheritance;
    private List<String> inheritedNames;
    private List<String> rankables;

    public GroupPermissions(PermissionsManager manager, String name) {
        super(manager);
        this.name = name;
        this.inheritance = new ArrayList<GroupPermissions>();
        this.inheritedNames = new ArrayList<String>();
        this.rankables = new ArrayList<String>();
    }

    @Override
    public void load(ConfigurationSection section) {
        super.load(section);
        if (section != null) {
            List<String> inheritance = section.getStringList("inheritance");
            for (String group : inheritance) {
                this.inheritance.add(this.manager.getGroup(group));
                this.inheritedNames.add(group.toLowerCase());
            }

            List<String> rankables = section.getStringList("rankables");
            for (String group : rankables) {
                GroupPermissions permissions = this.manager.getGroup(group);
                if (permissions != null) {
                    this.rankables.add(permissions.name);
                }
            }
        }
    }

    @Override
    public void save(ConfigurationSection section) {
        super.save(section);
        if (section != null) {
            if (!this.inheritedNames.isEmpty()) {
                section.set("inheritance", this.inheritedNames);
            }
            if (!this.rankables.isEmpty()) {
                section.set("rankables", this.rankables);
            }
        }
    }

    @Override
    public void release() {
        this.inheritance.clear();
        this.inheritance = null;
        this.rankables.clear();
        this.rankables = null;
        super.release();
    }

    @Override
    public void buildPermissions(HashMap<String, Boolean> initial, String world) {
        for (GroupPermissions parent : this.inheritance) {
            parent.buildPermissions(initial, world);
        }
        super.buildPermissions(initial, world);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    public List<String> getAllGroupNames() {
        ArrayList<String> result = new ArrayList<String>();

        result.addAll(this.inheritedNames);

        for (GroupPermissions group : this.inheritance) {
            result.addAll(group.getAllGroupNames());
        }

        return result;
    }

    public boolean doesInheritFrom(GroupPermissions group) {
        List<String> groups = this.getAllGroupNames();

        for (String currentGroup : groups) {
            if (group.name.equalsIgnoreCase(currentGroup)) {
                return true;
            }
        }

        return false;
    }

    public List<String> getRankables() {
        TreeSet<String> result = new TreeSet<String>();

        result.addAll(this.rankables);

        for (GroupPermissions group : this.inheritance) {
            result.addAll(group.getRankables());
        }

        return new ArrayList<String>(result);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof GroupPermissions)) {
            return false;
        }
        GroupPermissions other = (GroupPermissions) obj;
        if (this.name == null) {
            if (other.name != null) {
                return false;
            }
        }
        else if (!this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

}
