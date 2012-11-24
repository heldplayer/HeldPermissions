
package me.heldplayer.permissions;

import org.bukkit.ChatColor;
import org.bukkit.event.player.*;
import org.bukkit.event.block.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class PermissionsListener implements Listener {
    private final Permissions main;

    public PermissionsListener(Permissions instance) {
        main = instance;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerBucketFill(PlayerBucketFillEvent event) {
        if (!event.getPlayer().hasPermission("permissions.build")) {
            event.getPlayer().sendMessage(ChatColor.RED + "You do not have permissions in this world.");
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        if (!event.getPlayer().hasPermission("permissions.build")) {
            event.getPlayer().sendMessage(ChatColor.RED + "You do not have permissions in this world.");
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!event.getPlayer().hasPermission("permissions.build")) {
            event.getPlayer().sendMessage(ChatColor.RED + "You do not have permissions in this world.");
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!event.getPlayer().hasPermission("permissions.build")) {
            event.getPlayer().sendMessage(ChatColor.RED + "You do not have permissions in this world.");
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        //main.getLogger().info("[DEBUG] PlayerChangedWorldEvent");

        main.initPermissions(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        //main.getLogger().info("[DEBUG] PlayerRespawnEvent");

        main.initPermissions(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        //main.getLogger().info("[DEBUG] PlayerJoinEvent");

        main.initPermissions(event.getPlayer());
    }
}
