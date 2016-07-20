package me.heldplayer.permissions;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.heldplayer.permissions.command.PermissionsMainCommand;
import me.heldplayer.permissions.command.PromoteCommand;
import me.heldplayer.permissions.command.RankCommand;
import me.heldplayer.permissions.core.PermissionsManager;
import me.heldplayer.permissions.core.added.AddedPermission;
import me.heldplayer.permissions.core.added.AddedPermissionsManager;
import net.specialattack.spacore.event.PlayerPermissionsChanged;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
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

        this.permissionsManager.release();
        this.permissionsManager = null;
        this.addedPermissionsManager.release();
        this.addedPermissionsManager = null;

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

        this.loadPermissions();
        this.loadAddedPermissions();

        if (this.getServer().getPluginManager().isPluginEnabled("Vault")) {
            this.registerPermissionsService();
        }

        this.log.info(pdfFile.getFullName() + " is now enabled!");
    }

    private void registerPermissionsService() {
        this.log.info("Registering a permissions handler");
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
            }
        }

        this.recalculatePermissions();
    }

    public void savePermissions() throws IOException {
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
                this.savePermissions();
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
                .filter(attachment -> attachment.getPlugin() == this)
                .forEach(PermissionAttachment::remove);

        HashMap<String, Boolean> perms = this.permissionsManager.getPermissions(player);
        long allowCount = perms.values().stream().filter(value -> value).count();
        this.debug("Got " + allowCount + " ALLOW definitions and " + (perms.size() - allowCount) + " DENY definitions");

        // Thanks codename_B! You're epic!
        PermissionAttachment attachment = player.addAttachment(this);

        try {
            @SuppressWarnings("unchecked") Map<String, Boolean> orig = (Map<String, Boolean>) Permissions.perms.get(attachment);

            orig.clear();

            orig.putAll(perms);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }

        player.recalculatePermissions();

        this.getServer().getPluginManager().callEvent(new PlayerPermissionsChanged(player));
    }

    public PermissionsManager getPermissionsManager() {
        return this.permissionsManager;
    }

    public AddedPermissionsManager getAddedPermissionsManager() {
        return this.addedPermissionsManager;
    }
}
