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

public class RankCommand implements CommandExecutor {
	private final Permissions main;

	public RankCommand(Permissions plugin) {
		this.main = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
		if(split.length == 1){
			Player player = main.getServer().getPlayer(split[0]);

			String path = "";
			
			if (player == null) {
				if(main.permissions.contains("users." + path)){
					path = split[0];
				} else {
					sender.sendMessage(ChatColor.RED + "Player not found or not online");
					return true;
				}
			} else {
				path = player.getName();
			}

			String ranks = "";
			boolean flag = true;
			
			for (String rank : main.permissions.getStringList("users." + path + ".groups")) {
				if (flag) {
					ranks += rank;

					flag = false;
				} else {
					ranks += ", " + rank;
				}
			}
			
			sender.sendMessage(ChatColor.GRAY + "Player ranks: " + ChatColor.YELLOW + ranks);
			
			return true;
		} else if (split.length > 1) {
			Player player = main.getServer().getPlayer(split[0]);

			String path = "";
			
			if (player == null) {
				if(main.permissions.contains("users." + path)){
					path = split[0];
				} else {
					sender.sendMessage(ChatColor.RED + "Player not found or not online");
					return true;
				}
			} else {
				path = player.getName();
			}

			boolean flag = true;
			boolean flag2 = true;

			List<String> rankables = null;

			if (!sender.isOp()) {
				Player pSender = (Player) sender;

				rankables = main.getRankableGroups(pSender);
			}

			List<String> effectiveRanks = new ArrayList<String>();

			String ranks = "";

			for (String param : split) {
				if (flag) {
					flag = false;
					continue;
				}

				if (!sender.isOp()) {
					if (rankables.contains(param.toLowerCase())) {
						effectiveRanks.add(param.toLowerCase());

						if (flag2) {
							ranks += ChatColor.GREEN + param;

							flag2 = false;
						} else {
							ranks += ", " + ChatColor.GREEN + param;
						}
					} else {
						if (flag2) {
							ranks += ChatColor.RED + param;

							flag2 = false;
						} else {
							ranks += ", " + ChatColor.RED + param;
						}
					}
				} else {
					effectiveRanks.add(param.toLowerCase());

					if (flag2) {
						ranks += ChatColor.GREEN + param;

						flag2 = false;
					} else {
						ranks += ", " + ChatColor.GREEN + param;
					}
				}
			}

			for (String rank : main.permissions.getStringList("users." + path + ".groups")) {
				if (!sender.isOp()) {
					if (rankables.contains(rank.toLowerCase())) {
						if (flag2) {
							ranks += ChatColor.DARK_GREEN + rank;

							flag2 = false;
						} else {
							ranks += ", " + ChatColor.DARK_GREEN + rank;
						}
					} else {
						effectiveRanks.add(rank.toLowerCase());

						if (flag2) {
							ranks += ChatColor.DARK_RED + rank;

							flag2 = false;
						} else {
							ranks += ", " + ChatColor.DARK_RED + rank;
						}
					}
				} else {
					if (flag2) {
						ranks += ChatColor.DARK_GREEN + rank;

						flag2 = false;
					} else {
						ranks += ", " + ChatColor.DARK_GREEN + rank;
					}
				}
			}

			main.permissions.set("users." + path + ".groups", effectiveRanks);

			if (sender instanceof Player) {
				sender.sendMessage(ChatColor.WHITE + "Applied ranks (" + ChatColor.GREEN + "applied" + ChatColor.WHITE + " | " + ChatColor.RED + "failed" + ChatColor.WHITE + " | " + ChatColor.DARK_GREEN + "removed" + ChatColor.WHITE + " | " + ChatColor.DARK_RED + "retained" + ChatColor.WHITE + "): ");
			} else {
				sender.sendMessage("Applied ranks:");
			}

			sender.sendMessage(ranks);

			try {
				main.permissions.save(new File(main.getDataFolder(), "permissions.yml"));
			} catch (IOException e) {
				sender.sendMessage(ChatColor.DARK_RED + "Applied the ranks, but the ranks didn't get saved!");
			}

			main.recalculatePermissions(path);

			return true;
		} else {
			return false;
		}
	}
}