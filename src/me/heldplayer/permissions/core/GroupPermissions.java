package me.heldplayer.permissions.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.annotation.Nonnull;
import org.bukkit.configuration.ConfigurationSection;

public class GroupPermissions extends WorldlyPermissions implements Comparable<GroupPermissions> {

    public final String name;

    public Set<GroupPermissions> inheritance;
    private Set<String> inheritedNames;
    private Set<String> rankables;

    public GroupPermissions(PermissionsManager manager, String name) {
        super(manager);
        this.name = name;
        this.inheritance = new TreeSet<>();
        this.inheritedNames = new TreeSet<>();
        this.rankables = new TreeSet<>();
    }

    @Override
    public void load(ConfigurationSection section) {
        super.load(section);
        if (section != null) {
            List<String> inheritance = section.getStringList("inheritance");
            for (String group : inheritance) {
                GroupPermissions permissions = this.manager.getGroup(group);
                if (permissions != null) {
                    if (permissions.doesInheritFrom(this)) {
                        throw new RuntimeException("Circular inheritance error! " + this.name + " and " + permissions.name + " have a circular hierarchy");
                    }
                    this.inheritance.add(permissions);
                    this.inheritedNames.add(group.toLowerCase());
                }
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
                section.set("inheritance", new ArrayList<>(this.inheritedNames));
            }
            if (!this.rankables.isEmpty()) {
                section.set("rankables", new ArrayList<>(this.rankables));
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
        this.manager.plugin.debug("Adding permissions for " + this.name);
        super.buildPermissions(initial, world);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Nonnull
    public Set<String> getParents() {
        return Collections.unmodifiableSet(this.inheritedNames);
    }

    @Nonnull
    public List<String> getAllGroupNames() {
        ArrayList<String> result = new ArrayList<>();

        result.addAll(this.inheritedNames);

        for (GroupPermissions group : this.inheritance) {
            result.addAll(group.getAllGroupNames());
        }

        return result;
    }

    public boolean doesInheritFrom(GroupPermissions group) {
        if (this == group) {
            return true;
        }
        Collection<String> groups = this.getAllGroupNames();

        for (String currentGroup : groups) {
            if (group.name.equalsIgnoreCase(currentGroup)) {
                return true;
            }
        }

        return false;
    }

    @Nonnull
    public Set<String> getRankables() {
        HashSet<String> result = new HashSet<>();

        result.addAll(this.rankables);

        return result;
    }

    @Nonnull
    public Set<String> getAllRankables() {
        HashSet<String> result = new HashSet<>();

        result.addAll(this.rankables);

        for (GroupPermissions group : this.inheritance) {
            result.addAll(group.getAllRankables());
        }

        return result;
    }

    public void addParent(GroupPermissions group) {
        if (group != null) {
            if (group.doesInheritFrom(this)) {
                throw new RuntimeException("Circular inheritance error! " + this.name + " and " + group.name + " have a circular hierarchy");
            }
            this.inheritance.add(group);
            this.inheritedNames.add(group.name);
        }
    }

    public void removeParent(GroupPermissions group) {
        if (group != null) {
            this.inheritance.remove(group);
            this.inheritedNames.remove(group.name);
        }
    }

    public void addRankable(GroupPermissions rankable) {
        if (rankable != null) {
            this.rankables.add(rankable.name);
        }
    }

    public void removeRankable(GroupPermissions rankable) {
        if (rankable != null) {
            this.rankables.remove(rankable.name);
        }
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
        } else if (!this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(@Nonnull GroupPermissions other) {
        return this.name.compareTo(other.name);
    }
}
