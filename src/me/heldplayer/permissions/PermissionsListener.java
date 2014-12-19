package me.heldplayer.permissions;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.*;

public class PermissionsListener implements Listener {

    private final Permissions main;

    public PermissionsListener(Permissions instance) {
        this.main = instance;
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
        this.main.initPermissions(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        this.main.initPermissions(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.main.initPermissions(event.getPlayer());
    }
}
