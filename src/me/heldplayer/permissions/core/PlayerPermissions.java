package me.heldplayer.permissions.core;

import com.mojang.api.profiles.HttpProfileRepository;
import com.mojang.api.profiles.Profile;
import java.util.*;
import net.specialattack.bukkit.core.SpACore;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;

public class PlayerPermissions extends WorldlyPermissions implements Comparable<PlayerPermissions> {

    public final UUID uuid;

    private List<GroupPermissions> groups;
    private List<String> groupNames;

    private String lastName;
    private LinkedList<String> allNames;

    public PlayerPermissions(PermissionsManager manager, UUID uuid) {
        super(manager);
        this.uuid = uuid;
        this.groups = new ArrayList<GroupPermissions>();
        this.groupNames = new ArrayList<String>();
        this.lastName = "";
        this.allNames = new LinkedList<String>();
    }

    @Override
    public void load(ConfigurationSection section) {
        super.load(section);
        if (section != null) {
            if (section.contains("lastName") && !section.contains("allNames")) {
                this.allNames.addFirst(section.getString("lastName"));
            } else if (section.contains("allNames")) {
                this.allNames = new LinkedList<String>(section.getStringList("allNames"));

                if (section.contains("lastName")) {
                    String lastName = section.getString("lastName");

                    if (!this.allNames.contains(lastName)) {
                        this.allNames.add(lastName);
                    }
                }
            }
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
            section.set("lastName", this.getPlayerName());
            section.set("allNames", this.allNames);
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
        } else {
            for (GroupPermissions group : this.groups) {
                group.buildPermissions(initial, world);
            }
        }
        super.buildPermissions(initial, world);
    }

    @Override
    public boolean isEmpty() {
        return this.uuid == null || super.isEmpty() && this.groups.isEmpty();
    }

    public String getPlayerName() {
        OfflinePlayer player = Bukkit.getOfflinePlayer(this.uuid);

        if (player != null) {
            String name = player.getName();
            if (name != null && !name.isEmpty()) {
                return player.getName();
            }
        }

        if (this.lastName == null || this.lastName.isEmpty()) {
            HttpProfileRepository repository = SpACore.getProfileRepository();

            Profile profile = repository.findProfileByUUID(this.uuid.toString().replaceAll("-", ""));

            this.lastName = profile.getName();
        }

        return this.lastName;
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

    public Set<String> getRankableGroupNames() {
        HashSet<String> result = new HashSet<String>();

        for (GroupPermissions group : this.groups) {
            result.addAll(group.getAllRankables());
        }

        return Collections.unmodifiableSet(result);
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

    public boolean addGroup(GroupPermissions group) {
        if (group != null && !this.groups.contains(group)) {
            this.groups.add(group);
            this.groupNames.add(group.name);
            return true;
        }
        return false;
    }

    public boolean removeGroup(GroupPermissions group) {
        if (group != null && this.groups.contains(group)) {
            this.groups.remove(group);
            this.groupNames.remove(group.name);
            return true;
        }
        return false;
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
        } else if (!this.uuid.equals(other.uuid)) {
            return false;
        }
        return true;
    }

    public boolean shouldSave() {
        return true;
    }

    @Override
    public int compareTo(PlayerPermissions other) {
        return this.uuid.compareTo(other.uuid);
    }

}
