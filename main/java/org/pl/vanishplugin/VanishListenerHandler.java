package org.pl.vanishplugin;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import java.util.HashMap;
import java.util.Map;

public class VanishListenerHandler implements Listener {
    private final VanishPlugin plugin;

    private Map<Player, Long> lastMessageTime = new HashMap<>();

    public VanishListenerHandler(VanishPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        if (plugin.getDisabledPickupPlayers().contains(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (plugin.getDisabledUsePlayers().contains(player)) {
            event.setCancelled(true);
        }
    }


}