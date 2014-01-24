
package me.heldplayer.permissions;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import me.heldplayer.permissions.command.PermissionsMainCommand;
import me.heldplayer.permissions.command.PromoteCommand;
import me.heldplayer.permissions.command.RankCommand;

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
    public YamlConfiguration permissions;
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

        File dataFolder = this.getDataFolder();

        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }

        try {
            this.load();
        }
        catch (Exception e) {
            this.getLogger().severe("Failed to load permissions file!");
            e.printStackTrace();
        }

        this.debuggers = new ArrayList<String>();

        Updater.version = this.getDescription().getVersion();

        this.getLogger().info(pdfFile.getFullName() + " is now enabled!");
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

    protected void initPermissions(Player player) {
        this.debug("Recalculating permissions for " + player.getName());

        // Thanks codename_B! You're epic!
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

        HashMap<String, Boolean> perms = new HashMap<String, Boolean>();

        if (this.permissions.contains("users." + player.getName())) {
            if (this.permissions.contains("users." + player.getName() + ".groups")) {
                List<String> keys = this.permissions.getStringList("users." + player.getName() + ".groups");

                if (!keys.isEmpty()) {
                    for (String key : keys) {
                        this.debug("Player " + player.getName() + " has group '" + key + "'");
                        HashMap<String, Boolean> groupPerms = this.getGroupPermissions("groups." + key.toLowerCase(), player.getWorld().getName());
                        this.joinMaps(perms, groupPerms);
                    }
                }
                else {
                    this.debug("Player " + player.getName() + " has an empty group entry, defaulting to '" + this.permissions.getString("default").toLowerCase() + "'");
                    HashMap<String, Boolean> groupPerms = this.getGroupPermissions("groups." + this.permissions.getString("default").toLowerCase(), player.getWorld().getName());
                    this.joinMaps(perms, groupPerms);
                }
            }
            else {
                this.debug("Player " + player.getName() + " does not have a group entry, defaulting to '" + this.permissions.getString("default").toLowerCase() + "'");
                HashMap<String, Boolean> groupPerms = this.getGroupPermissions("groups." + this.permissions.getString("default").toLowerCase(), player.getWorld().getName());
                this.joinMaps(perms, groupPerms);
            }

            HashMap<String, Boolean> playerPerms = this.getGroupPermissions("users." + player.getName(), player.getWorld().getName());
            this.joinMaps(perms, playerPerms);
        }
        else {
            this.debug("Player " + player.getName() + " does not have an entry, defaulting to '" + this.permissions.getString("default").toLowerCase() + "'");
            HashMap<String, Boolean> groupPerms = this.getGroupPermissions("groups." + this.permissions.getString("default").toLowerCase(), player.getWorld().getName());
            this.joinMaps(perms, groupPerms);
        }

        //Set<String> permSet = perms.keySet();

        //for (String permission : permSet) {
        //player.addAttachment(this, permission, perms.get(permission));
        //}

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

    private <K, V> void joinMaps(HashMap<K, V> map1, HashMap<K, V> map2) {
        Set<K> map2Keys = map2.keySet();

        for (K key : map2Keys) {
            if (map1.containsKey(key)) {
                map1.remove(key);
            }

            map1.put(key, map2.get(key));
        }
    }

    private HashMap<String, Boolean> getGroupPermissions(String path, String world) {
        this.debug("Checking permissions on path '" + path + "'");

        HashMap<String, Boolean> result = new HashMap<String, Boolean>();

        if (this.permissions.contains(path + ".inherits")) {
            //debug("Path contains inheritage '" + permissions.getString(path + ".inherits").toLowerCase() + "'");
            this.joinMaps(result, this.getGroupPermissions("groups." + this.permissions.getString(path + ".inherits").toLowerCase(), world));
        }

        this.joinMaps(result, this.getPermissions(path + ".permissions"));

        this.joinMaps(result, this.getPermissions(path + ".worlds." + world));

        return result;
    }

    public HashMap<String, Boolean> getPermissions(String path) {
        //debug("Checking permissions on path '" + path + "'");

        HashMap<String, Boolean> result = new HashMap<String, Boolean>();

        if (this.permissions.contains(path + ".allow")) {
            List<String> keys = this.permissions.getStringList(path + ".allow");

            for (String key : keys) {
                //this.getLogger().info("[DEBUG] '" + key + "' set to true");

                result.put(key, true);
            }
        }

        if (this.permissions.contains(path + ".deny")) {
            List<String> keys = this.permissions.getStringList(path + ".deny");

            for (String key : keys) {
                if (result.containsKey(key)) {
                    result.remove(key);
                }

                //this.getLogger().info("[DEBUG] '" + key + "' set to false");

                result.put(key, false);
            }
        }

        return result;
    }

    public List<String> getRankableGroups(Player player) {
        List<String> ranks = new ArrayList<String>();

        if (this.permissions.contains("users." + player.getName())) {
            if (this.permissions.contains("users." + player.getName() + ".groups")) {
                List<String> keys = this.permissions.getStringList("users." + player.getName() + ".groups");

                for (String key : keys) {
                    ranks.addAll(this.getRankableGroups("groups." + key.toLowerCase()));
                }
            }
        }
        return ranks;
    }

    public List<String> getRankableGroups(String path) {
        List<String> ranks = new ArrayList<String>();

        if (this.permissions.contains(path + ".inherits")) {
            ranks.addAll(this.getRankableGroups("groups." + this.permissions.getString(path + ".inherits").toLowerCase()));
        }

        if (this.permissions.contains(path + ".rankables")) {
            List<String> keys = this.permissions.getStringList(path + ".rankables");
            for (String key : keys) {
                ranks.add(key.toLowerCase());
            }
        }

        return ranks;
    }

    protected void load() {
        this.permissions = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "permissions.yml"));

        this.recalculatePermissions();
    }

    public List<String> getGroups(String player, boolean deep) {
        ArrayList<String> groups = new ArrayList<String>();

        if (this.permissions.contains("users." + player)) {
            if (this.permissions.contains("users." + player + ".groups")) {
                List<String> keys = this.permissions.getStringList("users." + player + ".groups");

                if (deep) {
                    for (String key : keys) {
                        groups.addAll(this.getGroupGroups(key.toLowerCase()));
                    }
                }
                else {
                    groups.addAll(keys);
                }
            }
            else {
                groups.add(this.permissions.getString("default").toLowerCase());
            }
        }
        else {
            groups.add(this.permissions.getString("default").toLowerCase());
        }

        return groups;
    }

    public List<String> getGroupGroups(String group) {
        ArrayList<String> groups = new ArrayList<String>();

        groups.add(group.toLowerCase());

        if (this.permissions.contains("groups." + group + ".inherits")) {
            String groupS = this.permissions.getString("groups." + group + ".inherits");

            for (String key : this.getGroupGroups(groupS.toLowerCase())) {
                if (!groups.contains(key.toLowerCase())) {
                    groups.add(key.toLowerCase());
                }
            }
        }

        return groups;
    }

    public boolean doesGroupInheritFromGroup(String child, String parent) {
        List<String> groups = this.getGroupGroups(child);

        for (String group : groups) {
            if (parent.equalsIgnoreCase(group)) {
                return true;
            }
        }

        Permissions.log.info("It isn't");
        return false;
    }

    public List<String> getPlayersInGroup(String group) {
        ArrayList<String> result = new ArrayList<String>();

        for (Iterator<String> i = this.permissions.getConfigurationSection("users").getKeys(false).iterator(); i.hasNext();) {
            String name = i.next().toLowerCase();

            if (this.permissions.contains("users." + name + ".groups")) {
                List<String> keys = this.permissions.getStringList("users." + name + ".groups");

                for (String key : keys) {
                    if (key.equalsIgnoreCase(group.toLowerCase())) {
                        result.add(name);
                    }
                }
            }
            else {
                if (this.permissions.getString("default").equalsIgnoreCase(group)) {
                    result.add(name);
                }
            }
        }

        return result;
    }

    public List<String> getAllGroups() {
        ArrayList<String> result = new ArrayList<String>();

        for (Iterator<String> i = this.permissions.getConfigurationSection("groups").getKeys(false).iterator(); i.hasNext();) {
            String name = i.next().toLowerCase();

            result.add(name);
        }

        return result;
    }

    public HashMap<String, Boolean> getWorldPermissions(String name, String world) {
        HashMap<String, Boolean> perms = new HashMap<String, Boolean>();

        if (this.permissions.contains("users." + name)) {
            if (this.permissions.contains("users." + name + ".groups")) {
                List<String> keys = this.permissions.getStringList("users." + name + ".groups");

                if (!keys.isEmpty()) {
                    for (String key : keys) {
                        HashMap<String, Boolean> groupPerms = this.getGroupPermissions("groups." + key.toLowerCase(), world);
                        this.joinMaps(perms, groupPerms);
                    }
                }
                else {
                    HashMap<String, Boolean> groupPerms = this.getGroupPermissions(this.permissions.getString("default").toLowerCase(), world);
                    this.joinMaps(perms, groupPerms);
                }
            }
            else {
                HashMap<String, Boolean> groupPerms = this.getGroupPermissions(this.permissions.getString("default").toLowerCase(), world);
                this.joinMaps(perms, groupPerms);
            }

            HashMap<String, Boolean> playerPerms = this.getGroupPermissions("users." + name, world);
            this.joinMaps(perms, playerPerms);
        }
        else {
            HashMap<String, Boolean> groupPerms = this.getGroupPermissions(this.permissions.getString("default").toLowerCase(), world);
            this.joinMaps(perms, groupPerms);
        }

        return perms;
    }

    public List<String> getAllWorlds(String path) {
        ArrayList<String> result = new ArrayList<String>();

        if (this.permissions.contains(path + ".worlds")) {
            Set<String> keys = this.permissions.getConfigurationSection(path + ".worlds").getKeys(false);

            for (String key : keys) {
                result.add(key);
            }
        }

        return result;
    }

    public static String format(String str, ChatColor color, Object... args) {
        for (int i = 0; i < args.length; i++) {
            args[i] = ChatColor.WHITE + args[i].toString() + color;
        }

        return color + String.format(str, args);
    }

}
