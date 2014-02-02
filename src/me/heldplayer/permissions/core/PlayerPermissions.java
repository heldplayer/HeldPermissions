
package me.heldplayer.permissions.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

public class PlayerPermissions extends WorldlyPermissions {

    public final String playerName;

    private List<GroupPermissions> groups;
    private List<String> groupNames;

    public PlayerPermissions(PermissionsManager manager, String name) {
        super(manager);
        this.playerName = name;
        this.groups = new ArrayList<GroupPermissions>();
        this.groupNames = new ArrayList<String>();
    }

    @Override
    public void load(ConfigurationSection section) {
        super.load(section);
        if (section != null) {
            List<String> groups = section.getStringList("groups");
            for (String group : groups) {
                this.groups.add(this.manager.getGroup(group));
                this.groupNames.add(group.toLowerCase());
            }
        }
    }

    @Override
    public void save(ConfigurationSection section) {
        super.save(section);
        if (section != null) {
            if (!this.groupNames.isEmpty()) {
                section.set("groups", this.groupNames);
            }
        }
    }

    @Override
    public void release() {
        this.groups.clear();
        this.groups = null;
        super.release();
    }

    @Override
    public void buildPermissions(HashMap<String, Boolean> initial, String world) {
        if (this.groups.isEmpty()) {
            if (this.manager.defaultGroup != null) {
                this.manager.defaultGroup.buildPermissions(initial, world);
            }
        }
        else {
            for (GroupPermissions group : this.groups) {
                group.buildPermissions(initial, world);
            }
        }
        super.buildPermissions(initial, world);
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty() && this.groups.isEmpty();
    }

    public List<String> getGroupNames() {
        return Collections.unmodifiableList(this.groupNames);
    }

    public List<String> getAllGroupNames() {
        ArrayList<String> result = new ArrayList<String>();

        result.addAll(this.groupNames);

        for (GroupPermissions group : this.groups) {
            result.addAll(group.getAllGroupNames());
        }

        return result;
    }

    public List<String> getRankableGroupNames() {
        HashSet<String> result = new HashSet<String>();

        for (GroupPermissions group : this.groups) {
            result.addAll(group.getRankables());
        }

        return new ArrayList<String>(result);
    }

    public List<GroupPermissions> getGroups() {
        return Collections.unmodifiableList(this.groups);
    }

    public void setGroups(List<GroupPermissions> groups) {
        this.groups = groups;

        this.groupNames.clear();
        for (GroupPermissions group : groups) {
            this.groupNames.add(group.name);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.playerName == null) ? 0 : this.playerName.hashCode());
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
        if (!(obj instanceof PlayerPermissions)) {
            return false;
        }
        PlayerPermissions other = (PlayerPermissions) obj;
        if (this.playerName == null) {
            if (other.playerName != null) {
                return false;
            }
        }
        else if (!this.playerName.equals(other.playerName)) {
            return false;
        }
        return true;
    }

}
