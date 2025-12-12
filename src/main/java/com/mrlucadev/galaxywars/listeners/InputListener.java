package com.mrlucadev.galaxywars.listeners;

import com.mrlucadev.galaxywars.managers.PowerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public class InputListener implements Listener {

    @EventHandler
    public void onInput(PlayerSwapHandItemsEvent event) {
        // Always cancel the swap so items don't move around
        event.setCancelled(true);

        if (event.getPlayer().isSneaking()) {
            // SHIFT + F = CYCLE
            PowerManager.cyclePower(event.getPlayer());
        } else {
            // F (Normal) = CAST
            PowerManager.castCurrent(event.getPlayer());
        }
    }
}