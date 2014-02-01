
package me.heldplayer.permissions;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.heldplayer.permissions.command.PermissionsMainCommand;
import me.heldplayer.permissions.command.PromoteCommand;
import me.heldplayer.permissions.command.RankCommand;
import me.heldplayer.permissions.core.PermissionsManager;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class Permissions extends JavaPlugin {

    public static Permissions instance;
    public static Logger log;

    public PermissionsListener playerListener;
    private PermissionsManager manager;
    public ArrayList<String> debuggers;

    @Override
    public void onDisable() {
        PluginDescriptionFile pdfFile = this.getDescription();

        this.getLogger().info(pdfFile.getFullName() + " is now disabled!");
    }

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

        this.debuggers = new ArrayList<String>();

        Updater.version = this.getDescription().getVersion();

        this.getLogger().info(pdfFile.getFullName() + " is now enabled!");
    }

    public void loadPermissions() {
        if (this.manager != null) {
            this.manager.release();
        }
        this.manager = new PermissionsManager();
        File dataFolder = this.getDataFolder();

        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }

        File permissionsFile = new File(this.getDataFolder(), "permissions.yml");

        if (!permissionsFile.exists()) {
            try {
                permissionsFile.createNewFile();
            }
            catch (IOException e) {
                log.log(Level.SEVERE, "Failed loading permissions file", e);
                return;
            }
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(permissionsFile);

        this.manager.load(config);

        this.recalculatePermissions();
    }

    public void savePermissions() throws IOException {
        File dataFolder = this.getDataFolder();

        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }

        File permissionsFile = new File(this.getDataFolder(), "permissions.yml");

        if (!permissionsFile.exists()) {
            permissionsFile.createNewFile();
        }

        YamlConfiguration config = new YamlConfiguration();

        if (this.manager != null) {
            this.manager.save(config);
        }

        config.save(permissionsFile);
    }

    public void debug(String message) {
        Player[] players = this.getServer().getOnlinePlayers();

        for (Player player : players) {
            try {
                for (String playerName : this.debuggers) {
                    if (player.getName().equalsIgnoreCase(playerName)) {
                        player.sendMessage(ChatColor.DARK_AQUA + "> " + ChatColor.AQUA + message);
                    }
                }
            }
            catch (Exception ex) {}
        }
    }

    public void recalculatePermissions() {
        Player[] players = this.getServer().getOnlinePlayers();

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

    private static Field perms;

    static {
        try {
            perms = PermissionAttachment.class.getDeclaredField("permissions");
            perms.setAccessible(true);
        }
        catch (SecurityException e) {
            e.printStackTrace();
        }
        catch (NoSuchFieldException e) {
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
                attachment.remove();
            }
        }

        HashMap<String, Boolean> perms = this.manager.getPermissions(player);

        // Thanks codename_B! You're epic!
        PermissionAttachment attachment = player.addAttachment(this);

        try {
            @SuppressWarnings("unchecked")
            Map<String, Boolean> orig = (Map<String, Boolean>) Permissions.perms.get(attachment);

            orig.clear();

            orig.putAll(perms);

            attachment.getPermissible().recalculatePermissions();
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        player.recalculatePermissions();
    }

    public PermissionsManager getManager() {
        return this.manager;
    }

    public static String format(String str, ChatColor color, Object... args) {
        for (int i = 0; i < args.length; i++) {
            args[i] = ChatColor.WHITE + args[i].toString() + color;
        }

        return color + String.format(str, args);
    }

}
