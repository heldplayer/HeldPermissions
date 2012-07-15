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

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class Permissions extends JavaPlugin {
	protected PermissionsListener playerListener;
	protected YamlConfiguration permissions;
	protected ArrayList<String> debuggers;
	// Update manager
	public Update upd;
	public String address = "";
	public String address2 = "";
	public String versionaddress = "";
	public String updatereasonaddress = "";
	public static String updatepath = "plugins" + File.separator + "HeldPermissions.jar";
	public static String updatepath2 = "plugins" + File.separator + "HeldPermissionsBridge.jar";
	public static String version;

	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();

		this.getLogger().info(pdfFile.getFullName() + " is now disabled!");
	}

	public void onEnable() {
		PluginDescriptionFile pdfFile = this.getDescription();

		getCommand("permissions").setExecutor(new PermissionsCommand(this));
		getCommand("rank").setExecutor(new RankCommand(this));
		getCommand("perm").setExecutor(new PermCommand(this));

		upd = new Update(this);

		playerListener = new PermissionsListener(this);

		getServer().getPluginManager().registerEvents(playerListener, this);

		File dataFolder = getDataFolder();

		if (!dataFolder.exists()) {
			dataFolder.mkdir();
		}

		try {
			load();
		} catch (Exception e) {
			this.getLogger().severe("Failed to load permissions file!");
			e.printStackTrace();
		}

		debuggers = new ArrayList<String>();

		version = getDescription().getVersion();

		this.getLogger().info(pdfFile.getFullName() + " is now enabled!");
	}

	public void debug(String message) {
		Player[] players = getServer().getOnlinePlayers();

		for (Player player : players) {
			try {
				for (String playerName : debuggers) {
					if (player.getName().equalsIgnoreCase(playerName)) {
						player.sendMessage(ChatColor.DARK_AQUA + "!" + ChatColor.AQUA + message);
					}
				}
			} catch (Exception ex) {
			}
		}

		//this.getLogger().info("[DEBUG] " + message);
	}

	public void recalculatePermissions() {
		Player[] players = getServer().getOnlinePlayers();

		for (Player player : players) {
			initPermissions(player);
		}
	}

	public void recalculatePermissions(String playerName) {
		Player player = getServer().getPlayer(playerName);

		if (player != null) {
			initPermissions(player);
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

	protected void initPermissions(Player player) {
		debug("Recalculating permissions for " + player.getName());

		// Thanks codename_B! You're epic!
		Set<PermissionAttachment> att2 = new HashSet<PermissionAttachment>();

		for (PermissionAttachmentInfo attachment : player.getEffectivePermissions()) {
			if (attachment.getAttachment() != null) {
				att2.add(attachment.getAttachment());
			}
		}

		if (att2.size() > 0) {
			for (PermissionAttachment at : att2) {
				at.remove();
			}
		}

		HashMap<String, Boolean> perms = new HashMap<String, Boolean>();

		if (permissions.contains("users." + player.getName())) {
			if (permissions.contains("users." + player.getName() + ".groups")) {
				List<String> keys = permissions.getStringList("users." + player.getName() + ".groups");

				if (!keys.isEmpty()) {
					for (String key : keys) {
						debug("Player " + player.getName() + " has group '" + key + "'");
						HashMap<String, Boolean> groupPerms = getGroupPermissions("groups." + key.toLowerCase(), player.getWorld().getName());
						joinMaps(perms, groupPerms);
					}
				} else {
					debug("Player " + player.getName() + " has an empty group entry, defaulting to '" + permissions.getString("default").toLowerCase() + "'");
					HashMap<String, Boolean> groupPerms = getGroupPermissions("groups." + permissions.getString("default").toLowerCase(), player.getWorld().getName());
					joinMaps(perms, groupPerms);
				}
			} else {
				debug("Player " + player.getName() + " does not have a group entry, defaulting to '" + permissions.getString("default").toLowerCase() + "'");
				HashMap<String, Boolean> groupPerms = getGroupPermissions("groups." + permissions.getString("default").toLowerCase(), player.getWorld().getName());
				joinMaps(perms, groupPerms);
			}

			HashMap<String, Boolean> playerPerms = getGroupPermissions("users." + player.getName(), player.getWorld().getName());
			joinMaps(perms, playerPerms);
		} else {
			debug("Player " + player.getName() + " does not have an entry, defaulting to '" + permissions.getString("default").toLowerCase() + "'");
			HashMap<String, Boolean> groupPerms = getGroupPermissions("groups." + permissions.getString("default").toLowerCase(), player.getWorld().getName());
			joinMaps(perms, groupPerms);
		}

		//Set<String> permSet = perms.keySet();

		//for (String permission : permSet) {
		//player.addAttachment(this, permission, perms.get(permission));
		//}

		// Thanks codename_B! You're epic!
		PermissionAttachment att = player.addAttachment(this);

		try {
			@SuppressWarnings("unchecked")
			Map<String, Boolean> orig = (Map<String, Boolean>) Permissions.perms.get(att);

			orig.clear();

			orig.putAll(perms);

			att.getPermissible().recalculatePermissions();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
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
		debug("Checking permissions on path '" + path + "'");

		HashMap<String, Boolean> result = new HashMap<String, Boolean>();

		if (permissions.contains(path + ".inherits")) {
			//debug("Path contains inheritage '" + permissions.getString(path + ".inherits").toLowerCase() + "'");
			joinMaps(result, getGroupPermissions("groups." + permissions.getString(path + ".inherits").toLowerCase(), world));
		}

		joinMaps(result, getPermissions(path + ".permissions"));

		joinMaps(result, getPermissions(path + ".worlds." + world));

		return result;
	}

	public HashMap<String, Boolean> getPermissions(String path) {
		//debug("Checking permissions on path '" + path + "'");

		HashMap<String, Boolean> result = new HashMap<String, Boolean>();

		if (permissions.contains(path + ".allow")) {
			List<String> keys = permissions.getStringList(path + ".allow");

			for (String key : keys) {
				//this.getLogger().info("[DEBUG] '" + key + "' set to true");

				result.put(key, true);
			}
		}

		if (permissions.contains(path + ".deny")) {
			List<String> keys = permissions.getStringList(path + ".deny");

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

		if (permissions.contains("users." + player.getName())) {
			if (permissions.contains("users." + player.getName() + ".groups")) {
				List<String> keys = permissions.getStringList("users." + player.getName() + ".groups");

				for (String key : keys) {
					ranks.addAll(getRankableGroups("groups." + key.toLowerCase()));
				}
			}
		}
		return ranks;
	}

	public List<String> getRankableGroups(String path) {
		List<String> ranks = new ArrayList<String>();

		if (permissions.contains(path + ".inherits")) {
			ranks.addAll(getRankableGroups("groups." + permissions.getString(path + ".inherits").toLowerCase()));
		}

		if (permissions.contains(path + ".rankables")) {
			List<String> keys = permissions.getStringList(path + ".rankables");
			for (String key : keys) {
				ranks.add(key.toLowerCase());
			}
		}

		return ranks;
	}

	protected void load() {
		permissions = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "permissions.yml"));

		recalculatePermissions();
	}

	public List<String> getGroups(String player, boolean deep) {
		ArrayList<String> groups = new ArrayList<String>();

		if (permissions.contains("users." + player)) {
			if (permissions.contains("users." + player + ".groups")) {
				List<String> keys = permissions.getStringList("users." + player + ".groups");

				for (String key : keys) {
					groups.addAll(getGroupGroups(key.toLowerCase()));
				}
			} else {
				groups.add(permissions.getString("default").toLowerCase());
			}
		} else {
			groups.add(permissions.getString("default").toLowerCase());
		}

		return groups;
	}

	public List<String> getGroupGroups(String group) {
		ArrayList<String> groups = new ArrayList<String>();

		if (!groups.contains(group.toLowerCase())) {
			groups.add(group.toLowerCase());
		}

		if (permissions.contains("groups." + group + ".inherits")) {
			String groupS = permissions.getString("groups." + group + ".inherits");

			for (String key : getGroupGroups(groupS.toLowerCase())) {
				if (!groups.contains(key.toLowerCase())) {
					groups.add(key.toLowerCase());
				}
			}
		}

		return groups;
	}

	public List<String> getPlayersInGroup(String group) {
		ArrayList<String> result = new ArrayList<String>();

		for (Iterator<String> i = permissions.getConfigurationSection("users").getKeys(false).iterator(); i.hasNext();) {
			String name = i.next().toLowerCase();

			if (permissions.contains("users." + name + ".groups")) {
				List<String> keys = permissions.getStringList("users." + name + ".groups");

				for (String key : keys) {
					if (key.equalsIgnoreCase(group.toLowerCase())) {
						result.add(name);
					}
				}
			} else {
				if (permissions.getString("default").equalsIgnoreCase(group)) {
					result.add(name);
				}
			}
		}

		return result;
	}

	public List<String> getAllGroups() {
		ArrayList<String> result = new ArrayList<String>();

		for (Iterator<String> i = permissions.getConfigurationSection("groups").getKeys(false).iterator(); i.hasNext();) {
			String name = i.next().toLowerCase();

			result.add(name);
		}

		return result;
	}

	public HashMap<String, Boolean> getWorldPermissions(String name, String world) {
		HashMap<String, Boolean> perms = new HashMap<String, Boolean>();

		if (permissions.contains("users." + name)) {
			if (permissions.contains("users." + name + ".groups")) {
				List<String> keys = permissions.getStringList("users." + name + ".groups");

				if (!keys.isEmpty()) {
					for (String key : keys) {
						HashMap<String, Boolean> groupPerms = getGroupPermissions("groups." + key.toLowerCase(), world);
						joinMaps(perms, groupPerms);
					}
				} else {
					HashMap<String, Boolean> groupPerms = getGroupPermissions(permissions.getString("default").toLowerCase(), world);
					joinMaps(perms, groupPerms);
				}
			} else {
				HashMap<String, Boolean> groupPerms = getGroupPermissions(permissions.getString("default").toLowerCase(), world);
				joinMaps(perms, groupPerms);
			}

			HashMap<String, Boolean> playerPerms = getGroupPermissions("users." + name, world);
			joinMaps(perms, playerPerms);
		} else {
			HashMap<String, Boolean> groupPerms = getGroupPermissions(permissions.getString("default").toLowerCase(), world);
			joinMaps(perms, groupPerms);
		}

		return perms;
	}

	public List<String> getAllWorlds(String path) {
		ArrayList<String> result = new ArrayList<String>();

		if (permissions.contains(path + ".worlds")) {
			Set<String> keys = permissions.getConfigurationSection(path + ".worlds").getKeys(false);

			for (String key : keys) {
				result.add(key);
			}
		}

		return result;
	}
}
