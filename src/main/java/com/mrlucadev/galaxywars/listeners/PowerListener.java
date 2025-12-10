package com.mrlucadev.galaxywars.listeners;

import com.mrlucadev.galaxywars.GalaxyWars;
import com.mrlucadev.galaxywars.utils.Lang;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;

public class PowerListener implements Listener {

    // Simple cooldown map (UUID -> System Time in Milliseconds)
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();

    @EventHandler
    public void onCast(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        // 1. Trigger: Sneak + Left Click Air with Empty Hand
        if (event.getAction() != Action.LEFT_CLICK_AIR) return;
        if (!player.isSneaking()) return;
        if (!player.getInventory().getItemInMainHand().getType().isAir()) return;

        // 2. Check Cooldown
        FileConfiguration config = GalaxyWars.getInstance().getConfig();
        int cooldownSeconds = config.getInt("abilities.power-push.cooldown");
        
        if (isOnCooldown(player, cooldownSeconds)) return;

        // 3. Get Settings
        double range = config.getDouble("abilities.power-push.range");
        double force = config.getDouble("abilities.power-push.strength");
        double lift = config.getDouble("abilities.power-push.vertical-lift");
        double angleLimit = config.getDouble("abilities.power-push.angle");

        // 4. The Logic: Find targets in a "Cone"
        int targetsHit = 0;
        Vector direction = player.getLocation().getDirection();

        for (Entity entity : player.getNearbyEntities(range, range, range)) {
            if (!(entity instanceof LivingEntity)) continue; // Ignore items/paintings
            if (entity == player) continue; // Don't push yourself

            // Vector from Player to Target
            Vector toEntity = entity.getLocation().toVector().subtract(player.getLocation().toVector());
            
            // Check Angle (Is it in front of me?)
            if (toEntity.angle(direction) <= Math.toRadians(angleLimit)) {
                
                // 5. Apply Physics (Launch them!)
                // Normalize vector so distance doesn't affect speed, then apply force
                Vector launch = toEntity.normalize().multiply(force).setY(lift);
                entity.setVelocity(launch);
                targetsHit++;
            }
        }

        // 6. Feedback & Effects
        if (targetsHit > 0) {
            // Success
            setCooldown(player);
            player.sendMessage(Lang.getMessage("abilities.push.used"));
            
            // Play Sound (Deep, heavy sound)
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WARDEN_SONIC_BOOM, 1.0f, 1.5f);
            
            // Spawn Particles (A shockwave line)
            Location loc = player.getEyeLocation().add(direction);
            player.getWorld().spawnParticle(Particle.SONIC_BOOM, loc, 1);
            
        } else {
            // Failure (No targets)
            player.sendActionBar(Lang.getMessage("abilities.push.no-targets"));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.5f);
        }
    }

    // --- Cooldown Helpers ---
    
    private boolean isOnCooldown(Player player, int seconds) {
        if (!cooldowns.containsKey(player.getUniqueId())) return false;
        
        long timeElapsed = System.currentTimeMillis() - cooldowns.get(player.getUniqueId());
        if (timeElapsed < seconds * 1000L) {
            long timeLeft = (seconds * 1000L - timeElapsed) / 1000;
            player.sendActionBar(Lang.getMessage("abilities.push.cooldown")
                    .replaceText(b -> b.match("<time>").replacement(String.valueOf(timeLeft))));
            return true;
        }
        return false;
    }

    private void setCooldown(Player player) {
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
    }
}