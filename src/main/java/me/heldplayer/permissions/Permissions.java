package me.heldplayer.permissions;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import me.heldplayer.permissions.command.PermissionsMainCommand;
import me.heldplayer.permissions.command.PromoteCommand;
import me.heldplayer.permissions.command.RankCommand;
import me.heldplayer.permissions.core.Perm;
import me.heldplayer.permissions.core.PermCollection;
import me.heldplayer.permissions.core.PermissionsManager;
import me.heldplayer.permissions.core.added.AddedPermission;
import me.heldplayer.permissions.core.added.AddedPermissionsManager;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.specialattack.spacore.event.PlayerPermissionsChangedEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public class Permissions extends JavaPlugin {

    public Logger log;

    public PermissionsListener playerListener;
    private PermissionsManager permissionsManager;
    private AddedPermissionsManager addedPermissionsManager;
    public ArrayList<UUID> debuggers;

    @Override
    public void onDisable() {
        PluginDescriptionFile pdfFile = this.getDescription();

        this.debug("Removing permissions for all players");

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.getEffectivePermissions().stream()
                    .filter(attachmentInfo -> attachmentInfo.getAttachment() != null)
                    .map(PermissionAttachmentInfo::getAttachment)
                    .filter(attachment -> attachment.getPlugin() == this)
                    .forEach(PermissionAttachment::remove);
        }

        if (this.permissionsManager != null) {
            this.permissionsManager.release();
            this.permissionsManager = null;
        }
        if (this.addedPermissionsManager != null) {
            this.addedPermissionsManager.release();
            this.addedPermissionsManager = null;
        }

        this.log.info(pdfFile.getFullName() + " is now disabled!");
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onEnable() {
        this.log = this.getLogger();
        this.debuggers = new ArrayList<>();

        PluginDescriptionFile pdfFile = this.getDescription();

        this.getCommand("permissions").setExecutor(new PermissionsMainCommand(this));
        this.getCommand("rank").setExecutor(new RankCommand(this));
        this.getCommand("perm").setExecutor(new PermCommand(this));
        this.getCommand("promote").setExecutor(new PromoteCommand(this));

        this.playerListener = new PermissionsListener(this);

        this.getServer().getPluginManager().registerEvents(this.playerListener, this);

        try {
            this.loadPermissions();
        } catch (Exception e) {
            this.log.log(Level.SEVERE, "Failed loading permissions file", e);
        }
        this.loadAddedPermissions();

        if (this.getServer().getPluginManager().isPluginEnabled("Vault")) {
            this.registerPermissionsService();
        }

        this.log.info(pdfFile.getFullName() + " is now enabled!");
    }

    private void registerPermissionsService() {
        this.log.info("Registering Vault Permissions handler");
        this.getServer().getServicesManager().register(net.milkbowl.vault.permission.Permission.class, new VaultPermissions(this), this, ServicePriority.Highest);
    }

    public void loadPermissions() {
        if (this.permissionsManager != null) {
            this.permissionsManager.release();
        }
        this.permissionsManager = new PermissionsManager(this);
        File dataFolder = this.getDataFolder();

        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }

        File file = new File(this.getDataFolder(), "permissions.yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                this.log.log(Level.SEVERE, "Failed loading permissions file", e);
                return;
            }
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        boolean shouldSave = this.permissionsManager.load(config);

        if (shouldSave) {
            try {
                this.savePermissions();
            } catch (IOException e) {
                this.log.log(Level.SEVERE, "Failed saving permissions file", e);
            } catch (SaveException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }

        this.recalculatePermissions();
    }

    public void savePermissionsBy(CommandSender sender) {
        try {
            this.savePermissions();
        } catch (IOException e) {
            sender.sendMessage(ChatColor.DARK_RED + "Applied the changes, but the changes didn't get saved!");
        } catch (SaveException e) {
            sender.sendMessage(ChatColor.DARK_RED + "Applied the changes, but cannot save changes right now: " + e.getMessage());
        }
    }

    public void savePermissions() throws IOException, SaveException {
        File dataFolder = this.getDataFolder();

        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }

        File file = new File(this.getDataFolder(), "permissions.yml");

        if (!file.exists()) {
            file.createNewFile();
        }

        YamlConfiguration config = new YamlConfiguration();

        if (this.permissionsManager != null) {
            this.permissionsManager.save(config);
        }

        config.save(file);
    }

    public void loadAddedPermissions() {
        if (this.addedPermissionsManager != null) {
            this.addedPermissionsManager.release();
        }
        this.addedPermissionsManager = new AddedPermissionsManager();
        File dataFolder = this.getDataFolder();

        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }

        File file = new File(this.getDataFolder(), "added-permissions.yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                this.log.log(Level.SEVERE, "Failed loading added permissions file", e);
                return;
            }
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        boolean shouldSave = this.addedPermissionsManager.load(config);

        this.rewriteAddedPermissions();

        if (shouldSave) {
            try {
                this.saveAddedPermissions();
            } catch (IOException e) {
                this.log.log(Level.SEVERE, "Failed saving added permissions file", e);
            }
        }
    }

    public void saveAddedPermissions() throws IOException {
        File dataFolder = this.getDataFolder();

        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }

        File file = new File(this.getDataFolder(), "added-permissions.yml");

        if (!file.exists()) {
            file.createNewFile();
        }

        YamlConfiguration config = new YamlConfiguration();

        if (this.addedPermissionsManager != null) {
            this.addedPermissionsManager.save(config);
        }

        config.save(file);
    }

    public void rewriteAddedPermissions() {
        for (AddedPermission addedPermission : this.addedPermissionsManager.addedPermissions) {
            Permission permission = Bukkit.getPluginManager().getPermission(addedPermission.name);
            if (permission == null) { // If the permission is missing, create a whole new definition, otherwise keep the old one
                permission = new Permission(addedPermission.name, addedPermission.description, addedPermission.defaultValue);
                Bukkit.getPluginManager().addPermission(permission);
            } else { // Otherwise modify the description and the default value
                permission.setDescription(addedPermission.description);
                permission.setDefault(addedPermission.defaultValue);
            }
            addedPermission.permission = permission;
        }

        for (AddedPermission addedPermission : this.addedPermissionsManager.addedPermissions) {
            if (addedPermission.permission != null) { // null should be impossible
                for (String child : addedPermission.children) {
                    Permission childPermission = Bukkit.getPluginManager().getPermission(child);
                    if (childPermission == null) {
                        childPermission = new Permission(child, PermissionDefault.OP);
                        Bukkit.getPluginManager().addPermission(childPermission); // Add a blank permission
                    }
                    childPermission.addParent(addedPermission.permission, true);
                }
            }
        }
    }

    public void debug(String message) {
        Collection<? extends Player> players = this.getServer().getOnlinePlayers();

        players.stream()
                .filter(player -> this.debuggers.contains(player.getUniqueId()))
                .forEach(player -> player.sendMessage(ChatColor.DARK_AQUA + "> " + ChatColor.AQUA + message));
    }

    public void recalculatePermissions() {
        Collection<? extends Player> players = this.getServer().getOnlinePlayers();

        players.forEach(this::initPermissions);
    }

    public void recalculatePermissions(String playerName) {
        Player player = this.getServer().getPlayer(playerName);

        if (player != null) {
            this.initPermissions(player);
        }
    }

    public void recalculatePermissions(Player player) {
        if (player != null) {
            this.initPermissions(player);
        }
    }

    public void recalculatePermissions(OfflinePlayer player) {
        if (player != null) {
            if (player instanceof Player) {
                this.initPermissions((Player) player);
            } else {
                Player onlinePlayer = Bukkit.getPlayer(player.getUniqueId());
                if (onlinePlayer != null) {
                    this.initPermissions(onlinePlayer);
                }
            }
        }
    }

    private static Field perms;

    static {
        try {
            perms = PermissionAttachment.class.getDeclaredField("permissions");
            perms.setAccessible(true);
        } catch (SecurityException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public void initPermissions(Player player) {
        this.debug("Recalculating permissions for " + player.getName());

        player.getEffectivePermissions().stream()
                .filter(attachmentInfo -> attachmentInfo.getAttachment() != null)
                .map(PermissionAttachmentInfo::getAttachment)
                .distinct()
                .filter(attachment -> attachment.getPlugin() == this)
                .forEach(PermissionAttachment::remove);


        PermCollection perms = this.permissionsManager.getPermissions(player);
        long allowCount = perms.stream().filter(value -> value.value == Perm.Value.ALLOW).count();
        long denyCount = perms.stream().filter(value -> value.value == Perm.Value.DENY).count();
        long neverCount = perms.size() - allowCount - denyCount;
        this.debug(String.format("Got %d ALLOW; %d DENY; %d NEVER", allowCount, denyCount, neverCount));

        // Thanks codename_B! You're epic!
        PermissionAttachment attachment = player.addAttachment(this);

        try {
            @SuppressWarnings("unchecked")
            Map<String, Boolean> orig = (Map<String, Boolean>) Permissions.perms.get(attachment);

            orig.clear();

            perms.forEach((permission, value) -> orig.put(permission.toLowerCase(Locale.ENGLISH), value.value));
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }

        player.recalculatePermissions();

        this.getServer().getPluginManager().callEvent(new PlayerPermissionsChangedEvent(player));
    }

    public PermissionsManager getPermissionsManager() {
        return this.permissionsManager;
    }

    public AddedPermissionsManager getAddedPermissionsManager() {
        return this.addedPermissionsManager;
    }

    public static void notify(@Nonnull TextComponent message, @Nonnull CommandSender sender, @Nonnull String permission) {
        notifyExcept(message, sender, permission);
        if (sender instanceof Player) {
            ((Player) sender).spigot().sendMessage(message);
        } else {
            sender.sendMessage(message.toLegacyText());
        }
    }

    public static void notifyExcept(@Nonnull TextComponent message, @Nonnull CommandSender sender, @Nonnull String permission) {
        TextComponent fullMessage = new TextComponent("[");
        {
            TextComponent name = new TextComponent(sender.getName());
            if (sender instanceof Player) {
                name.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tell " + sender.getName()));
            }
            fullMessage.addExtra(name);
        }
        fullMessage.addExtra(": ");
        fullMessage.addExtra(message);
        fullMessage.addExtra("]");
        fullMessage.setColor(net.md_5.bungee.api.ChatColor.GRAY);
        fullMessage.setItalic(true);
        Predicate<Player> canGet;
        if (sender instanceof Player) {
            canGet = p -> !p.getUniqueId().equals(((Player) sender).getUniqueId());
        } else {
            canGet = p -> true;
        }
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (canGet.test(player) && player.hasPermission(permission)) {
                player.spigot().sendMessage(new TextComponent(fullMessage));
            }
        });
        if (sender != Bukkit.getConsoleSender()) {
            fullMessage.setItalic(null);
            Bukkit.getConsoleSender().sendMessage(fullMessage.toLegacyText());
        }
    }
}
