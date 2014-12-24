package me.heldplayer.permissions;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.heldplayer.permissions.command.PermissionsMainCommand;
import me.heldplayer.permissions.command.PromoteCommand;
import me.heldplayer.permissions.command.RankCommand;
import me.heldplayer.permissions.core.PermissionsManager;
import me.heldplayer.permissions.core.added.AddedPermission;
import me.heldplayer.permissions.core.added.AddedPermissionsManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

    public static Permissions instance;
    public static Logger log;

    public PermissionsListener playerListener;
    private PermissionsManager permissionsManager;
    private AddedPermissionsManager addedPermissionsManager;
    public ArrayList<String> debuggers;

    @Override
    public void onDisable() {
        PluginDescriptionFile pdfFile = this.getDescription();

        this.debug("Removing permissions for all players");

        for (Player player : Bukkit.getOnlinePlayers()) {
            Set<PermissionAttachment> attachments = new HashSet<PermissionAttachment>();

            for (PermissionAttachmentInfo attachmentInfo : player.getEffectivePermissions()) {
                if (attachmentInfo.getAttachment() != null) {
                    attachments.add(attachmentInfo.getAttachment());
                }
            }

            if (attachments.size() > 0) {
                for (PermissionAttachment attachment : attachments) {
                    if (attachment.getPlugin() == this) {
                        attachment.remove();
                    }
                }
            }
        }

        this.permissionsManager.release();
        this.permissionsManager = null;
        this.addedPermissionsManager.release();
        this.addedPermissionsManager = null;

        Permissions.log.info(pdfFile.getFullName() + " is now disabled!");
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onEnable() {
        instance = this;
        log = this.getLogger();

        PluginDescriptionFile pdfFile = this.getDescription();

        this.getCommand("permissions").setExecutor(new PermissionsMainCommand());
        this.getCommand("rank").setExecutor(new RankCommand());
        this.getCommand("perm").setExecutor(new PermCommand(this));
        this.getCommand("promote").setExecutor(new PromoteCommand());

        this.playerListener = new PermissionsListener(this);

        this.getServer().getPluginManager().registerEvents(this.playerListener, this);

        this.loadPermissions();
        this.loadAddedPermissions();

        this.debuggers = new ArrayList<String>();

        Updater.version = this.getDescription().getVersion();

        Permissions.log.info("Hooking into Vault Permissions");

        try {
            Class<? extends JavaPlugin> vaultClass = (Class<? extends JavaPlugin>) Class.forName("net.milkbowl.vault.Vault");
            Method hookPermission = vaultClass.getDeclaredMethod("hookPermission", String.class, Class.class, ServicePriority.class, String[].class);

            hookPermission.setAccessible(true);
            hookPermission.invoke(JavaPlugin.getPlugin(vaultClass), "HeldPermissions", Vault_Permissions.class, ServicePriority.Highest, new String[] { "me.heldplayer.permissions.Permissions" });
            hookPermission.setAccessible(false);
        } catch (Exception e) {
            Permissions.log.log(Level.WARNING, "Failed hooking into Vault Permissions", e);
        }

        Permissions.log.info(pdfFile.getFullName() + " is now enabled!");
    }

    public void loadPermissions() {
        if (this.permissionsManager != null) {
            this.permissionsManager.release();
        }
        this.permissionsManager = new PermissionsManager();
        File dataFolder = this.getDataFolder();

        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }

        File file = new File(this.getDataFolder(), "permissions.yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                log.log(Level.SEVERE, "Failed loading permissions file", e);
                return;
            }
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        boolean shouldSave = this.permissionsManager.load(config);

        if (shouldSave) {
            try {
                Permissions.instance.savePermissions();
            } catch (IOException e) {
                log.log(Level.SEVERE, "Failed saving permissions file", e);
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
                log.log(Level.SEVERE, "Failed loading added permissions file", e);
                return;
            }
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        boolean shouldSave = this.addedPermissionsManager.load(config);

        this.rewriteAddedPermissions();

        if (shouldSave) {
            try {
                Permissions.instance.savePermissions();
            } catch (IOException e) {
                log.log(Level.SEVERE, "Failed saving added permissions file", e);
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

        for (Player player : players) {
            try {
                for (String playerName : this.debuggers) {
                    if (player.getName().equalsIgnoreCase(playerName)) {
                        player.sendMessage(ChatColor.DARK_AQUA + "> " + ChatColor.AQUA + message);
                    }
                }
            } catch (Exception ex) {
            }
        }
    }

    public void recalculatePermissions() {
        Collection<? extends Player> players = this.getServer().getOnlinePlayers();

        for (Player player : players) {
            this.initPermissions(player);
        }
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

    private static Field perms;

    static {
        try {
            perms = PermissionAttachment.class.getDeclaredField("permissions");
            perms.setAccessible(true);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public void initPermissions(Player player) {
        this.debug("Recalculating permissions for " + player.getName());

        Set<PermissionAttachment> attachments = new HashSet<PermissionAttachment>();

        for (PermissionAttachmentInfo attachmentInfo : player.getEffectivePermissions()) {
            if (attachmentInfo.getAttachment() != null) {
                attachments.add(attachmentInfo.getAttachment());
            }
        }

        if (attachments.size() > 0) {
            for (PermissionAttachment attachment : attachments) {
                if (attachment.getPlugin() == this) {
                    attachment.remove();
                }
            }
        }

        HashMap<String, Boolean> perms = this.permissionsManager.getPermissions(player);

        // Thanks codename_B! You're epic!
        PermissionAttachment attachment = player.addAttachment(this);

        try {
            @SuppressWarnings("unchecked") Map<String, Boolean> orig = (Map<String, Boolean>) Permissions.perms.get(attachment);

            orig.clear();

            orig.putAll(perms);

            attachment.getPermissible().recalculatePermissions();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        player.recalculatePermissions();
    }

    public PermissionsManager getPermissionsManager() {
        return this.permissionsManager;
    }

    public AddedPermissionsManager getAddedPermissionsManager() {
        return this.addedPermissionsManager;
    }

    public static String format(String str, ChatColor color, Object... args) {
        for (int i = 0; i < args.length; i++) {
            args[i] = ChatColor.WHITE + args[i].toString() + color;
        }

        return color + String.format(str, args);
    }

}
