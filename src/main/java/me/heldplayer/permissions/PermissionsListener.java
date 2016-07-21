package me.heldplayer.permissions;

import net.specialattack.spacore.event.PlayerPermissionsChangedEvent;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PermissionsListener implements Listener {

    private final Permissions main;

    public PermissionsListener(Permissions instance) {
        this.main = instance;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerBucketFill(PlayerBucketFillEvent event) {
        if (!event.getPlayer().hasPermission(Consts.PERM_BUILD)) {
            event.getPlayer().sendMessage(ChatColor.RED + "You do not have permissions in this world.");
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        if (!event.getPlayer().hasPermission(Consts.PERM_BUILD)) {
            event.getPlayer().sendMessage(ChatColor.RED + "You do not have permissions in this world.");
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!event.getPlayer().hasPermission(Consts.PERM_BUILD)) {
            event.getPlayer().sendMessage(ChatColor.RED + "You do not have permissions in this world.");
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!event.getPlayer().hasPermission(Consts.PERM_BUILD)) {
            event.getPlayer().sendMessage(ChatColor.RED + "You do not have permissions in this world.");
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        this.main.initPermissions(event.getPlayer());
        if (!this.checkGamemode(event.getPlayer(), event.getPlayer().getGameMode())) {
            event.getPlayer().setGameMode(GameMode.SURVIVAL);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        this.main.initPermissions(event.getPlayer());
        if (!this.checkGamemode(event.getPlayer(), event.getPlayer().getGameMode())) {
            event.getPlayer().setGameMode(GameMode.SURVIVAL);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.main.initPermissions(event.getPlayer());
        if (!this.checkGamemode(event.getPlayer(), event.getPlayer().getGameMode())) {
            event.getPlayer().setGameMode(GameMode.SURVIVAL);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
        if (!this.checkGamemode(event.getPlayer(), event.getNewGameMode())) {
            event.setCancelled(true);
            event.getPlayer().setGameMode(GameMode.SURVIVAL);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerPermissionsChanged(PlayerPermissionsChangedEvent event) {
        if (!this.checkGamemode(event.getPlayer(), event.getPlayer().getGameMode())) {
            event.getPlayer().setGameMode(GameMode.SURVIVAL);
        }
    }

    private boolean checkGamemode(Player player, GameMode gameMode) {
        if (gameMode == GameMode.CREATIVE && !player.hasPermission(Consts.PERM_GAMEMODE_CREATIVE)) {
            player.sendMessage(ChatColor.RED + "You do not have permissions to be in creative mode here!");
            return false;
        }
        if (gameMode == GameMode.SPECTATOR && !player.hasPermission(Consts.PERM_GAMEMODE_SPECTATOR)) {
            player.sendMessage(ChatColor.RED + "You do not have permissions to be in spectator mode here!");
            return false;
        }
        return true;
    }
}
