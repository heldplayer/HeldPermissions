
package me.heldplayer.permissions.core;

import java.util.*;

import net.specialattack.bukkit.core.SpACore;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.mojang.api.profiles.HttpProfileRepository;
import com.mojang.api.profiles.Profile;

public class PlayerPermissions extends WorldlyPermissions implements Comparable<PlayerPermissions> {

    public final UUID uuid;

    private Set<GroupPermissions> groups;
    private Set<String> groupNames;

    private String lastName;
    private LinkedList<String> allNames;

    public PlayerPermissions(PermissionsManager manager, UUID uuid) {
        super(manager);
        this.uuid = uuid;
        this.groups = new TreeSet<GroupPermissions>();
        this.groupNames = new TreeSet<String>();
        this.lastName = "";
        this.allNames = new LinkedList<String>();
    }

    @Override
    public void load(ConfigurationSection section) {
        super.load(section);
        if (section != null) {
            if (section.contains("lastName") && !section.contains("allNames")) {
                this.allNames.addFirst(section.getString("lastName"));
            }
            else if (section.contains("allNames")) {
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
                section.set("groups", new ArrayList<String>(this.groupNames));
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
        Player player = Bukkit.getPlayer(this.uuid);

        if (player != null) {
            return player.hasPermission(permission);
        }

        return super.hasPermission(permission, world);
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

    public Collection<String> getGroupNames() {
        return Collections.unmodifiableSet(this.groupNames);
    }

    public Collection<String> getAllGroupNames() {
        ArrayList<String> result = new ArrayList<String>();

        result.addAll(this.groupNames);

        for (GroupPermissions group : this.groups) {
            result.addAll(group.getAllGroupNames());
        }

        return result;
    }

    public Collection<String> getRankableGroupNames() {
        HashSet<String> result = new HashSet<String>();

        for (GroupPermissions group : this.groups) {
            result.addAll(group.getRankables());
        }

        return new ArrayList<String>(result);
    }

    public Collection<GroupPermissions> getGroups() {
        return Collections.unmodifiableSet(this.groups);
    }

    public void setGroups(Set<GroupPermissions> groups) {
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

    public boolean shouldSave() {
        return true;
    }

    @Override
    public int compareTo(PlayerPermissions other) {
        return this.uuid.compareTo(other.uuid);
    }

}
