package me.heldplayer.permissions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

public class PermissionsCommand implements CommandExecutor {
	private final Permissions main;

	public PermissionsCommand(Permissions plugin) {
		this.main = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
		if (split.length > 0) {
			if (split[0].equalsIgnoreCase("reload")) {
				try {
					main.load();
					sender.sendMessage(ChatColor.GREEN + "Reload succesfull!");
				} catch (Exception e) {
					sender.sendMessage(ChatColor.RED + "Failed to reload permissions!");
				}
				return true;
			}
			if (split[0].equalsIgnoreCase("help")) {
				sender.sendMessage(ChatColor.GRAY + "/permissions check [player] [permission]");

				return true;
			}
			if (split[0].equalsIgnoreCase("check") && split.length == 3) {
				Player player = main.getServer().getPlayer(split[1]);

				if (player == null) {
					sender.sendMessage(ChatColor.RED + "Player not found or not online: " + ChatColor.WHITE + split[1]);
					return true;
				} else {
					sender.sendMessage(ChatColor.GRAY + player.getName() + ChatColor.GREEN + " has permission " + ChatColor.WHITE + split[2] + ChatColor.GREEN + " set to " + ChatColor.WHITE + (player.hasPermission(split[2]) ? "true" : "false"));
					return true;
				}
			}
			if (split[0].equalsIgnoreCase("info") && split.length == 2) {
				Permission perm = this.main.getServer().getPluginManager().getPermission(split[1]);

				if (perm == null) {
					sender.sendMessage(ChatColor.RED + "Unknown permission: " + ChatColor.WHITE + split[1]);
					return true;
				} else {
					sender.sendMessage(ChatColor.GREEN + "Info on permission " + ChatColor.WHITE + perm.getName() + ChatColor.GREEN + ":");
					sender.sendMessage(ChatColor.GREEN + "Default: " + ChatColor.WHITE + perm.getDefault());
					if ((perm.getDescription() != null) && (perm.getDescription().length() > 0)) {
						sender.sendMessage(ChatColor.GREEN + "Description: " + ChatColor.WHITE + perm.getDescription());
					}
					if ((perm.getChildren() != null) && (perm.getChildren().size() > 0)) {
						sender.sendMessage(ChatColor.GREEN + "Children: " + ChatColor.WHITE + perm.getChildren().size());
					}

					return true;
				}
			}
			if (split[0].equalsIgnoreCase("group")) {
				return groupCommand(sender, command, split);
			}
			if (split[0].equalsIgnoreCase("player")) {
				return playerCommand(sender, command, split);
			}
		} else {
			sender.sendMessage(ChatColor.YELLOW + main.getDescription().getFullName() + " by " + main.getDescription().getAuthors().get(0));
			return true;
		}
		return false;
	}

	private boolean groupCommand(CommandSender sender, Command command, String[] split) {
		if (split.length < 2) {
			return false;
		}

		if (split[1].equalsIgnoreCase("list") && split.length == 2) {
			List<String> groups = main.getAllGroups();

			boolean flag = true;
			String message = "";

			for (String group : groups) {
				if (flag) {
					flag = false;
					message += group;
				} else {
					message += ", " + group;
				}
			}

			sender.sendMessage(ChatColor.GREEN + "Groups: " + ChatColor.WHITE + message);

			return true;
		}
		if (split[1].equalsIgnoreCase("players") && split.length == 2) {
			List<String> groups = main.getPlayersInGroup(split[2]);

			boolean flag = true;
			String message = "";

			for (String group : groups) {
				if (flag) {
					flag = false;
					message += group;
				} else {
					message += ", " + group;
				}
			}

			sender.sendMessage(ChatColor.GREEN + "Players in " + ChatColor.WHITE + split[2] + ChatColor.GREEN + ": " + ChatColor.WHITE + message);

			return true;
		}
		if (split[1].equalsIgnoreCase("setperm") && split.length == 5) {
			String path = ".permissions";

			String[] split2 = split[3].split(":", 2);

			if (split2.length > 1) {
				path = ".worlds." + split2[0];
				split[3] = split2[1];
			}

			if (split[4].equalsIgnoreCase("true") || split[4].equalsIgnoreCase("t") || split[4].equalsIgnoreCase("yes") || split[4].equalsIgnoreCase("on")) {
				List<String> perms1 = main.permissions.getStringList("groups." + split[2] + path + ".allow");

				perms1.add(split[3]);

				main.permissions.set("groups." + split[2] + path + ".allow", perms1);

				List<String> perms2 = main.permissions.getStringList("groups." + split[2] + path + ".deny");

				if (perms2.contains(split[3])) {
					perms2.remove(split[3]);
				}

				main.permissions.set("groups." + split[2] + path + ".deny", perms2);

				sender.sendMessage(ChatColor.GREEN + "Set " + ChatColor.WHITE + split[3] + ChatColor.GREEN + " for " + ChatColor.WHITE + split[2] + ChatColor.GREEN + " to " + ChatColor.WHITE + "true");

				try {
					main.permissions.save(new File(main.getDataFolder(), "permissions.yml"));
				} catch (IOException e) {
					sender.sendMessage(ChatColor.DARK_RED + "Applied the changes, but the changes didn't get saved!");
				}

				main.recalculatePermissions();

				return true;
			}
			if (split[4].equalsIgnoreCase("false") || split[4].equalsIgnoreCase("f") || split[4].equalsIgnoreCase("no") || split[4].equalsIgnoreCase("off")) {
				List<String> perms1 = main.permissions.getStringList("groups." + split[2] + path + ".allow");

				if (perms1.contains(split[3])) {
					perms1.remove(split[3]);
				}

				main.permissions.set("groups." + split[2] + path + ".allow", perms1);

				List<String> perms2 = main.permissions.getStringList("groups." + split[2] + path + ".deny");

				perms2.add(split[3]);

				main.permissions.set("groups." + split[2] + path + ".deny", perms2);

				sender.sendMessage(ChatColor.GREEN + "Set " + ChatColor.WHITE + split[3] + ChatColor.GREEN + " for " + ChatColor.WHITE + split[2] + ChatColor.GREEN + " to " + ChatColor.WHITE + "false");

				try {
					main.permissions.save(new File(main.getDataFolder(), "permissions.yml"));
				} catch (IOException e) {
					sender.sendMessage(ChatColor.DARK_RED + "Applied the changes, but the changes didn't get saved!");
				}

				main.recalculatePermissions();

				return true;
			}

			return false;
		}
		if (split[1].equalsIgnoreCase("unsetperm") && split.length == 4) {
			String path = ".permissions";

			String[] split2 = split[3].split(":", 2);

			if (split2.length > 1) {
				path = ".worlds." + split2[0];
				split[3] = split2[1];
			}

			List<String> perms1 = main.permissions.getStringList("groups." + split[2] + path + ".allow");

			if (perms1.contains(split[3])) {
				perms1.remove(split[3]);
			}

			main.permissions.set("groups." + split[2] + path + ".allow", perms1);

			List<String> perms2 = main.permissions.getStringList("groups." + split[2] + path + ".deny");

			if (perms2.contains(split[3])) {
				perms2.remove(split[3]);
			}

			main.permissions.set("groups." + split[2] + path + ".deny", perms2);

			sender.sendMessage(ChatColor.GREEN + "Removed " + ChatColor.WHITE + split[3] + ChatColor.GREEN + " from " + ChatColor.WHITE + split[2]);

			try {
				main.permissions.save(new File(main.getDataFolder(), "permissions.yml"));
			} catch (IOException e) {
				sender.sendMessage(ChatColor.DARK_RED + "Applied the changes, but the changes didn't get saved!");
			}

			main.recalculatePermissions();

			return true;
		}

		return false;
	}

	private boolean playerCommand(CommandSender sender, Command command, String[] split) {
		if (split.length < 2) {
			return false;
		}

		if (split[1].equalsIgnoreCase("groups") && split.length == 3) {
			List<String> groups = main.getGroups(split[2], false);

			boolean flag = true;
			String message = "";

			for (String group : groups) {
				if (flag) {
					flag = false;
					message += group;
				} else {
					message += ", " + group;
				}
			}

			sender.sendMessage(ChatColor.GREEN + "Groups for " + ChatColor.WHITE + split[2] + ChatColor.GREEN + ": " + ChatColor.WHITE + message);

			return true;
		}
		if (split[1].equalsIgnoreCase("setgroup") && split.length >= 4) {
			List<String> groups = new ArrayList<String>();

			boolean flag = true;
			String message = "";

			for (int i = 3; i < split.length; i++) {
				if (flag) {
					flag = false;
					message += split[i];
				} else {
					message += ", " + split[i];
				}

				groups.add(split[i]);
			}

			main.permissions.set("users." + split[2] + ".groups", groups);

			sender.sendMessage(ChatColor.GREEN + "Groups for " + ChatColor.WHITE + split[2] + ChatColor.GREEN + " are now: " + ChatColor.WHITE + message);

			try {
				main.permissions.save(new File(main.getDataFolder(), "permissions.yml"));
			} catch (IOException e) {
				sender.sendMessage(ChatColor.DARK_RED + "Applied the changes, but the changes didn't get saved!");
			}

			main.recalculatePermissions(split[2]);

			return true;
		}
		if (split[1].equalsIgnoreCase("addgroup") && split.length == 4) {
			List<String> groups = main.getGroups(split[2], false);

			groups.add(split[3]);

			main.permissions.set("users." + split[2] + ".groups", groups);

			sender.sendMessage(ChatColor.GREEN + "Added group " + ChatColor.WHITE + split[1] + ChatColor.GREEN + " to " + ChatColor.WHITE + split[2]);

			try {
				main.permissions.save(new File(main.getDataFolder(), "permissions.yml"));
			} catch (IOException e) {
				sender.sendMessage(ChatColor.DARK_RED + "Applied the changes, but the changes didn't get saved!");
			}

			main.recalculatePermissions(split[2]);

			return true;
		}
		if (split[1].equalsIgnoreCase("removegroup") && split.length == 4) {
			List<String> groups = main.getGroups(split[2], false);

			if (groups.contains(split[3])) {
				groups.remove(split[3]);
			}

			main.permissions.set("users." + split[2] + ".groups", groups);

			sender.sendMessage(ChatColor.GREEN + "Removed group " + ChatColor.WHITE + split[1] + ChatColor.GREEN + " from " + ChatColor.WHITE + split[2]);

			try {
				main.permissions.save(new File(main.getDataFolder(), "permissions.yml"));
			} catch (IOException e) {
				sender.sendMessage(ChatColor.DARK_RED + "Applied the changes, but the changes didn't get saved!");
			}

			main.recalculatePermissions(split[2]);

			return true;
		}
		if (split[1].equalsIgnoreCase("removegroup") && split.length == 4) {
			List<String> groups = main.getGroups(split[2], false);

			if (groups.contains(split[3])) {
				groups.remove(split[3]);
			}

			main.permissions.set("users." + split[2] + ".groups", groups);

			sender.sendMessage(ChatColor.GREEN + "Removed group " + ChatColor.WHITE + split[1] + ChatColor.GREEN + " from " + ChatColor.WHITE + split[2]);

			try {
				main.permissions.save(new File(main.getDataFolder(), "permissions.yml"));
			} catch (IOException e) {
				sender.sendMessage(ChatColor.DARK_RED + "Applied the changes, but the changes didn't get saved!");
			}

			main.recalculatePermissions(split[2]);

			return true;
		}
		if (split[1].equalsIgnoreCase("setperm") && split.length == 5) {
			String path = ".permissions";

			String[] split2 = split[3].split(":", 2);

			if (split2.length > 1) {
				path = ".worlds." + split2[0];
				split[3] = split2[1];
			}

			if (split[4].equalsIgnoreCase("true") || split[4].equalsIgnoreCase("t") || split[4].equalsIgnoreCase("yes") || split[4].equalsIgnoreCase("on")) {
				List<String> perms1 = main.permissions.getStringList("users." + split[2] + path + ".allow");

				perms1.add(split[3]);

				main.permissions.set("users." + split[2] + path + ".allow", perms1);

				List<String> perms2 = main.permissions.getStringList("users." + split[2] + path + ".deny");

				if (perms2.contains(split[3])) {
					perms2.remove(split[3]);
				}

				main.permissions.set("users." + split[2] + path + ".deny", perms2);

				sender.sendMessage(ChatColor.GREEN + "Set " + ChatColor.WHITE + split[3] + ChatColor.GREEN + " for " + ChatColor.WHITE + split[2] + ChatColor.GREEN + " to " + ChatColor.WHITE + "true");

				try {
					main.permissions.save(new File(main.getDataFolder(), "permissions.yml"));
				} catch (IOException e) {
					sender.sendMessage(ChatColor.DARK_RED + "Applied the changes, but the changes didn't get saved!");
				}

				main.recalculatePermissions(split[2]);

				return true;
			}
			if (split[4].equalsIgnoreCase("false") || split[4].equalsIgnoreCase("f") || split[4].equalsIgnoreCase("no") || split[4].equalsIgnoreCase("off")) {
				List<String> perms1 = main.permissions.getStringList("users." + split[2] + path + ".allow");

				if (perms1.contains(split[3])) {
					perms1.remove(split[3]);
				}

				main.permissions.set("users." + split[2] + path + ".allow", perms1);

				List<String> perms2 = main.permissions.getStringList("users." + split[2] + path + ".deny");

				perms2.add(split[3]);

				main.permissions.set("users." + split[2] + path + ".deny", perms2);

				sender.sendMessage(ChatColor.GREEN + "Set " + ChatColor.WHITE + split[3] + ChatColor.GREEN + " for " + ChatColor.WHITE + split[2] + ChatColor.GREEN + " to " + ChatColor.WHITE + "false");

				try {
					main.permissions.save(new File(main.getDataFolder(), "permissions.yml"));
				} catch (IOException e) {
					sender.sendMessage(ChatColor.DARK_RED + "Applied the changes, but the changes didn't get saved!");
				}

				main.recalculatePermissions(split[2]);

				return true;
			}

			return false;
		}
		if (split[1].equalsIgnoreCase("unsetperm") && split.length == 4) {
			String path = ".permissions";

			String[] split2 = split[3].split(":", 2);

			if (split2.length > 1) {
				path = ".worlds." + split2[0];
				split[3] = split2[1];
			}

			List<String> perms1 = main.permissions.getStringList("users." + split[2] + path + ".allow");

			if (perms1.contains(split[3])) {
				perms1.remove(split[3]);
			}

			main.permissions.set("users." + split[2] + path + ".allow", perms1);

			List<String> perms2 = main.permissions.getStringList("users." + split[2] + path + ".deny");

			if (perms2.contains(split[3])) {
				perms2.remove(split[3]);
			}

			main.permissions.set("users." + split[2] + path + ".deny", perms2);

			sender.sendMessage(ChatColor.GREEN + "Removed " + ChatColor.WHITE + split[3] + ChatColor.GREEN + " from " + ChatColor.WHITE + split[2]);

			try {
				main.permissions.save(new File(main.getDataFolder(), "permissions.yml"));
			} catch (IOException e) {
				sender.sendMessage(ChatColor.DARK_RED + "Applied the changes, but the changes didn't get saved!");
			}

			main.recalculatePermissions(split[2]);

			return true;
		}

		return false;
	}
}