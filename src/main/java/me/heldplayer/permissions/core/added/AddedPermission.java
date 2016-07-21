package me.heldplayer.permissions.core.added;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;
import javax.annotation.Nonnull;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class AddedPermission implements Comparable<AddedPermission> {

    public final String name;
    public String description;
    public PermissionDefault defaultValue;
    public Set<String> children;
    public Permission permission;

    public AddedPermission(String name) {
        this.name = name;
        this.description = "";
        this.defaultValue = PermissionDefault.OP; // Keep the normal behaviour of permissions, if not defined then it defaults to OP in Bukkit
        this.children = new TreeSet<>();
    }

    @Override
    public int compareTo(@Nonnull AddedPermission other) {
        return this.name.compareTo(other.name);
    }

    public void load(ConfigurationSection section) {
        this.description = section.getString("description", "");
        this.defaultValue = PermissionDefault.getByName(section.getString("default", PermissionDefault.OP.toString()));
        this.children = new TreeSet<>(section.getStringList("children"));
    }

    public void save(ConfigurationSection section) {
        section.set("description", this.description);
        section.set("default", this.defaultValue.name());
        section.set("children", new ArrayList<>(this.children));
    }

    public void release() {
        this.description = null;
        this.defaultValue = null;
        this.children.clear();
        this.children = null;
    }
}
