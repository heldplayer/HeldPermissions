package me.heldplayer.permissions.core;

import java.util.Locale;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PermCollection {

    private final TreeSet<Perm> values; // Not properly sorted, only add after removing with attemptRemove()
    private final TreeMap<String, Perm.Value> valuesMap;

    public PermCollection() {
        this.values = new TreeSet<>();
        this.valuesMap = new TreeMap<>();
    }

    public PermCollection makeCopy() {
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

    private static class Copy extends PermCollection {

        private final PermCollection original;

        private Copy(PermCollection original) {
            this.original = original;
        }

        @Override
        public PermCollection makeCopy() {
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
