package me.heldplayer.permissions.core;

import java.util.ArrayList;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class BasePermissions {

    @Nonnull
    protected final PermissionsManager manager;

    private PermCollection definitions;
    private PermCollection uDefinitions;

    public BasePermissions(@Nonnull PermissionsManager manager) {
        this.manager = manager;
        this.definitions = new PermCollection();
        this.uDefinitions = this.definitions.makeCopy();
    }

    public boolean load(@Nonnull ConfigurationSection section, int version) {
        section.getStringList("allow").forEach(perm -> this.definitions.put(perm, Perm.Value.ALLOW));
        section.getStringList("deny").forEach(perm -> this.definitions.put(perm, Perm.Value.DENY));
        section.getStringList("never").forEach(perm -> this.definitions.put(perm, Perm.Value.NEVER));
        return false;
    }

    public void save(@Nonnull ConfigurationSection section) {
        ArrayList<String> allow = new ArrayList<>(), deny = new ArrayList<>(), never = new ArrayList<>();
        this.definitions.forEach((perm, value) -> {
            if (value == Perm.Value.ALLOW) {
                if (allow.isEmpty()) {
                    section.set("allow", allow);
                }
                allow.add(perm);
            } else if (value == Perm.Value.DENY) {
                if (deny.isEmpty()) {
                    section.set("deny", deny);
                }
                deny.add(perm);
            } else if (value == Perm.Value.NEVER) {
                if (never.isEmpty()) {
                    section.set("never", never);
                }
                never.add(perm);
            }
        });
    }

    public void release() {
        this.definitions.clear();
        this.definitions = null;
    }

    @Nonnull
    public PermCollection getDefinitions() {
        return this.uDefinitions;
    }

    public boolean isDefined(@Nonnull String permission) {
        return this.definitions.has(permission);
    }

    public void setPermission(@Nonnull String permission, @Nullable Perm.Value value) {
        if (value == null) {
            this.definitions.remove(permission);
        } else {
            this.definitions.put(permission, value);
        }
    }

    public void buildPermissions(@Nonnull PermCollection initial, @Nullable String world) {
        this.manager.plugin.debug("Adding base permissions");

        this.definitions.stream()
                .filter(entry -> !initial.has(entry.permission) || initial.get(entry.permission) != Perm.Value.NEVER) // Filter definitions that have deny set
                .forEach(initial::add);
    }

    public boolean hasPermission(@Nonnull String permission, @Nullable String world) {
        PermCollection result = new PermCollection();
        this.buildPermissions(result, world);

        if (result.has(permission)) {
            return result.get(permission).value;
        }

        Permission perm = Bukkit.getPluginManager().getPermission(permission);

        if (perm != null) {
            if (perm.getDefault() == PermissionDefault.TRUE) {
                return true;
            }
        }
        return false;
    }

    public boolean hasPermission(@Nonnull String permission, @Nullable World world) {
        return this.hasPermission(permission, world == null ? null : world.getName());
    }

    public boolean isEmpty() {
        return this.definitions.isEmpty();
    }
}
