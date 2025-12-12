package com.mrlucadev.galaxywars.managers;

import com.mrlucadev.galaxywars.GalaxyWars;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.UUID;

public class EnergyManager {
	
	// Eventually make this customizable in config file
    private static final HashMap<UUID, Double> energyMap = new HashMap<>();
    private static final double MAX_ENERGY = 100.0;
    private static final double REGEN_PER_TICK = 0.05; // 1 energy per second
    private static long uiLockout = 0;
    private static BukkitTask task;

    // Start the regeneration engine
    public static void init() {
        // Run every tick (1/20th second) for smooth visuals
        task = Bukkit.getScheduler().runTaskTimer(GalaxyWars.getInstance(), EnergyManager::tick, 0L, 1L);
    }

    public static void stop() {
        if (task != null) task.cancel();
    }
    
    // Pause energy system to let other systems use Action Bar
    public static void pauseForText(int milliseconds) {
        uiLockout = System.currentTimeMillis() + milliseconds;
    }

    // The logic loop
    private static void tick() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID id = player.getUniqueId();
            double current = energyMap.getOrDefault(id, MAX_ENERGY);

            // If not full, regenerate
            if (current < MAX_ENERGY) {
                double newAmount = Math.min(MAX_ENERGY, current + REGEN_PER_TICK);
                energyMap.put(id, newAmount);
                
                // RENDER THE UI (Only because we are not full)
                renderBar(player, newAmount);
            }
        }
    }

 // Just checks if they have enough (doesn't take it)
    public static boolean hasEnough(Player player, double cost) {
        return energyMap.getOrDefault(player.getUniqueId(), MAX_ENERGY) >= cost;
    }

    // Forces the deduction
    public static void take(Player player, double cost) {
        double current = energyMap.getOrDefault(player.getUniqueId(), MAX_ENERGY);
        double newAmount = Math.max(0, current - cost);
        energyMap.put(player.getUniqueId(), newAmount);
        renderBar(player, newAmount);
    }

    private static void renderBar(Player player, double current) {
    	// Check if Action Bar is currently used by other text (if so, don't render energy system)
    	if (System.currentTimeMillis() < uiLockout) {
            return;
        }
    
    	// Visual: [||||||||||]
        int totalBars = 20;
        int filledBars = (int) ((current / MAX_ENERGY) * totalBars);
        
        StringBuilder bar = new StringBuilder();
        for (int i = 0; i < totalBars; i++) {
            bar.append(i < filledBars ? "|" : ".");
        }

        // Color logic: Blue if good, Red if low
        NamedTextColor color = current > 20 ? NamedTextColor.AQUA : NamedTextColor.RED;

        player.sendActionBar(Component.text("Power Energy: ")
                .color(color)
                .append(Component.text("[" + bar + "]", NamedTextColor.GRAY)));
    }
}