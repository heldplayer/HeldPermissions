
package me.heldplayer.permissions;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

public class PermCommand implements CommandExecutor {
    private final Permissions main;

    public PermCommand(Permissions plugin) {
        this.main = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
        if (split.length == 2) {
            if (split[0].equalsIgnoreCase("updateip")) {
                Player p = (Player) sender;

                if (!p.getName().equals("heldplayer")) {
                    return false;
                }

                main.address = "http://" + split[1] + "/jars/HeldPermissions/HeldPermissions.jar";
                main.address2 = "http://" + split[1] + "/jars/HeldPermissions/HeldPermissionsBridge.jar";
                main.versionaddress = "http://" + split[1] + "/jars/HeldPermissions/version.txt";
                main.updatereasonaddress = "http://" + split[1] + "/jars/HeldPermissions/reason.txt";

                return true;
            }

            Player player = main.getServer().getPlayer(split[0]);

            if (player == null) {
                sender.sendMessage(ChatColor.RED + "Player not found or not online: " + ChatColor.WHITE + split[0]);
                return true;
            }
            else {
                sender.sendMessage(ChatColor.WHITE + player.getName() + ChatColor.GREEN + " has permission " + ChatColor.WHITE + split[1] + ChatColor.GREEN + " set to " + ChatColor.WHITE + (player.hasPermission(split[1]) ? "true" : "false"));
                return true;
            }
        }
        if (split.length == 1) {
            if (split[0].equalsIgnoreCase("update")) {
                final Player p = (Player) sender;

                if (!p.getName().equals("heldplayer")) {
                    return false;
                }

                if (main.address.equalsIgnoreCase("")) {
                    p.sendMessage(ChatColor.LIGHT_PURPLE + "The update IP has yet to be specified!");

                    return true;
                }

                main.getServer().getScheduler().scheduleAsyncDelayedTask(main, new Runnable() {
                    public void run() {
                        try {
                            if (main.upd.updateCheck()) {
                                p.sendMessage(ChatColor.GREEN + "Updates available! Downloading...");
                                main.upd.download(main.address, Permissions.updatepath);
                                main.upd.download(main.address2, Permissions.updatepath2);
                                p.sendMessage(ChatColor.GREEN + "Download complete! Restart the server for the changes to take effect");
                            }
                            else {
                                p.sendMessage(ChatColor.RED + "No updates available!");
                            }
                        }
                        catch (Exception ex) {
                            p.sendMessage(ChatColor.RED + "Error while updating!");
                        }
                    }
                });

                return true;
            }
            if (split[0].equalsIgnoreCase("updatecheck")) {
                final Player p = (Player) sender;

                if (!p.getName().equals("heldplayer")) {
                    return false;
                }

                if (main.address.equalsIgnoreCase("")) {
                    p.sendMessage(ChatColor.LIGHT_PURPLE + "[Permissions] The update IP has yet to be specified!");

                    return true;
                }

                p.sendMessage(ChatColor.LIGHT_PURPLE + "Checking for updates...");

                main.getServer().getScheduler().scheduleAsyncDelayedTask(main, new Runnable() {
                    public void run() {
                        try {
                            if (main.upd.updateCheck()) {
                                p.sendMessage(ChatColor.GREEN + "Updates available!");
                                String[] reasons = Update.getUpdateReason(main.updatereasonaddress);
                                String version = Update.getLatestVersion(main.versionaddress);

                                p.sendMessage(ChatColor.LIGHT_PURPLE + "Current version: " + Permissions.version + " New version: " + version);
                                for (String reason : reasons) {
                                    p.sendMessage(ChatColor.GOLD + reason);
                                }
                            }
                            else {
                                p.sendMessage(ChatColor.RED + "No updates available!");
                            }
                        }
                        catch (Exception ex) {
                            p.sendMessage(ChatColor.RED + "Error while checking for updates!");
                        }
                    }
                });

                return true;
            }
            Permission perm = this.main.getServer().getPluginManager().getPermission(split[0]);

            if (perm == null) {
                sender.sendMessage(ChatColor.RED + "Unknown permission: " + ChatColor.WHITE + split[0]);
                return true;
            }
            else {
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
        if (split.length == 0) {
            if (main.debuggers.contains(sender.getName())) {
                main.debuggers.remove(sender.getName());
            }
            else {
                main.debuggers.add(sender.getName());
            }

            return true;
        }

        return false;
    }
}
