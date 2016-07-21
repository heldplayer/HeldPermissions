package me.heldplayer.permissions.core;

import java.util.ArrayList;
import java.util.Locale;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class BasePermissions {

    private Perms definitions;
    private Perms uDefinitions;
    @Nonnull
    protected final PermissionsManager manager;

    public BasePermissions(@Nonnull PermissionsManager manager) {
        this.manager = manager;
        this.definitions = new Perms();
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
    public Perms getDefinitions() {
        return this.uDefinitions;
    }

    public void setPermission(@Nonnull String permission, Perm.@Nullable Value value) {
        if (value == null) {
            this.definitions.remove(permission);
        } else {
            this.definitions.put(permission, value);
        }
    }

    public boolean isSet(@Nonnull String permission) {
        return this.definitions.has(permission);
    }

    public void buildPermissions(@Nonnull Perms initial, @Nullable String world) {
        this.manager.plugin.debug("Adding base permissions");

        this.definitions.stream()
                .filter(entry -> !initial.has(entry.permission) || initial.get(entry.permission) != Perm.Value.NEVER) // Filter definitions that have deny set
                .forEach(initial::add);
    }

    public boolean hasPermission(@Nonnull String permission, @Nullable String world) {
        Perms result = new Perms();
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

    public static class Perms {

        private final TreeSet<Perm> values; // Not properly sorted, only add after removing with attemptRemove()
        private final TreeMap<String, Perm.Value> valuesMap;

        public Perms() {
            this.values = new TreeSet<>();
            this.valuesMap = new TreeMap<>();
        }

        public Perms makeCopy() {
            return new Copy(this);
        }

        public boolean isEmpty() {
            return this.values.isEmpty();
        }

        public void clear() {
            this.values.clear();
            this.valuesMap.clear();
        }

        public long size() {
            return this.values.size();
        }

        public boolean has(@Nonnull String key) {
            return this.valuesMap.containsKey(key.toLowerCase(Locale.ENGLISH));
        }

        public Perm.Value get(@Nonnull String key) {
            return this.valuesMap.get(key.toLowerCase(Locale.ENGLISH));
        }

        private boolean attemptRemove(String permission) {
            return this.valuesMap.remove(permission) == null
                    || this.values.remove(new Perm(permission, Perm.Value.ALLOW))
                    || this.values.remove(new Perm(permission, Perm.Value.DENY))
                    || this.values.remove(new Perm(permission, Perm.Value.NEVER));
        }

        public void put(@Nonnull String permission, @Nonnull Perm.Value value) {
            permission = permission.toLowerCase(Locale.ENGLISH);
            if (!this.attemptRemove(permission)) {
                throw new IllegalStateException("Permission removed from Map but not from Set");
            }
            this.valuesMap.put(permission, value);
            this.values.add(new Perm(permission, value));
        }

        public void add(@Nonnull Perm perm) {
            if (!this.attemptRemove(perm.permission)) {
                throw new IllegalStateException("Permission removed from Map but not from Set");
            }
            this.valuesMap.put(perm.permission, perm.value);
            this.values.add(perm);
        }

        public void remove(@Nonnull String permission) {
            if (!this.attemptRemove(permission.toLowerCase(Locale.ENGLISH))) {
                throw new IllegalStateException("Permission removed from Map but not from Set");
            }
        }

        public void forEach(@Nonnull BiConsumer<String, Perm.Value> consumer) {
            for (Perm perm : this.values) {
                consumer.accept(perm.permission, perm.value);
            }
        }

        public Stream<@Nonnull Perm> stream() {
            return this.values.stream();
        }
    }

    private static class Copy extends Perms {

        private final Perms original;

        private Copy(Perms original) {
            this.original = original;
        }

        @Override
        public Perms makeCopy() {
            return this;
        }

        @Override
        public boolean isEmpty() {
            return this.original.isEmpty();
        }

        @Override
        public void clear() {
            throw new IllegalStateException("Cannot modify read-only Permissions");
        }

        @Override
        public long size() {
            return this.original.size();
        }

        @Override
        public boolean has(@Nonnull String key) {
            return this.original.has(key);
        }

        @Override
        @Nullable
        public Perm.Value get(@Nonnull String key) {
            return this.original.get(key);
        }

        @Override
        public void put(@Nonnull String permission, @Nonnull Perm.Value value) {
            throw new UnsupportedOperationException("Cannot modify read-only Permissions");
        }

        @Override
        public void add(@Nonnull Perm perm) {
            throw new UnsupportedOperationException("Cannot modify read-only Permissions");
        }

        @Override
        public void remove(@Nonnull String permission) {
            throw new UnsupportedOperationException("Cannot modify read-only Permissions");
        }

        @Override
        public void forEach(@Nonnull BiConsumer<String, Perm.Value> consumer) {
            this.original.forEach(consumer);
        }

        @Override
        public Stream<@Nonnull Perm> stream() {
            return this.original.stream();
        }
    }
}
