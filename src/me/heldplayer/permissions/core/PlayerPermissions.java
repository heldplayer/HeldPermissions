
package me.heldplayer.permissions.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import net.specialattack.bukkit.core.SpACore;

import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class PlayerPermissions extends WorldlyPermissions {

    public final UUID uuid;

    private List<GroupPermissions> groups;
    private List<String> groupNames;

    public String lastName;

    public PlayerPermissions(PermissionsManager manager, UUID uuid) {
        super(manager);
        this.uuid = uuid;
        this.groups = new ArrayList<GroupPermissions>();
        this.groupNames = new ArrayList<String>();
        this.lastName = "";
    }

    @Override
    public void load(ConfigurationSection section) {
        super.load(section);
        if (section != null) {
            this.lastName = section.getString("lastName");
            List<String> groups = section.getStringList("groups");
            for (String group : groups) {
                GroupPermissions permissions = this.manager.getGroup(group);
                if (permissions != null) {
                    this.groups.add(permissions);
                    this.groupNames.add(group.toLowerCase());
                }
            }
        }
    }

    @Override
    public void save(ConfigurationSection section) {
        if (section != null) {
            // Preferably first
            section.set("lastName", this.getPlayerName(true));
        }
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
    public boolean hasPermission(String permission, World world) {
        Player player = SpACore.getPlayer(this.uuid);

        if (player != null) {
            return player.hasPermission(permission);
        }

        return super.hasPermission(permission, world);
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

    @Deprecated
    public String getPlayerName(boolean fast) {
        if (fast) {
            if (this.lastName != null && !this.lastName.isEmpty()) {
                return this.lastName;
            }
        }
        Player player = SpACore.getPlayer(this.uuid);
        return player != null ? player.getName() : "";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.uuid == null) ? 0 : this.uuid.hashCode());
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
        if (this.uuid == null) {
            if (other.uuid != null) {
                return false;
            }
        }
        else if (!this.uuid.equals(other.uuid)) {
            return false;
        }
        return true;
    }

}
