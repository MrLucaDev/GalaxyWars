package com.mrlucadev.galaxywars.powers;

import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class PowerPull extends PowerAbility {

    @Override
    public String getId() { return "pull"; }

    @Override
    public String getDisplayName() { return "Power Pull"; }

    @Override
    public int getCooldown() { return 0; }

    @Override
    public int getCost() { return 15; }

    @Override
    public boolean cast(Player player) {
        boolean hitTarget = false;
        Vector direction = player.getLocation().getDirection();

        player.getWorld().playSound(player.getLocation(), Sound.ITEM_TRIDENT_RETURN, 1.0f, 0.8f);

        for (Entity entity : player.getNearbyEntities(15, 15, 15)) {
            if (!(entity instanceof LivingEntity) || entity == player) continue;

            // Cone check
            Vector toEntity = entity.getLocation().toVector().subtract(player.getLocation().toVector());
            if (toEntity.angle(direction) > Math.toRadians(45)) continue;

            // Don't pull if they are already in sword range (3 blocks)
            double distance = player.getLocation().distance(entity.getLocation());
            if (distance < 3.0) continue;

            // Calculate vector from Entity -> Player
            Vector pullVector = player.getLocation().toVector().subtract(entity.getLocation().toVector());
            
            // Normalize (make length 1) -> Multiply speed -> Add slight lift
            // We use slightly less force than Push because pulling is disorienting
            entity.setVelocity(pullVector.normalize().multiply(1.8).setY(0.4));

            entity.getWorld().spawnParticle(Particle.PORTAL, entity.getLocation().add(0, 1, 0), 10, 0.2, 0.2, 0.2, 0.5);
            
            hitTarget = true;
        }

        // Return true only if we actually pulled someone (so we don't waste energy on air)
        return hitTarget;
    }
}