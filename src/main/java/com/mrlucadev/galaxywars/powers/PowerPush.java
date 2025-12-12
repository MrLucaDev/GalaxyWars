package com.mrlucadev.galaxywars.powers;

import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class PowerPush extends PowerAbility {

    @Override
    public String getId() { return "push"; }

    @Override
    public String getDisplayName() { return "Power Push"; }

    @Override
    public int getCooldown() { return 0; } // 0 Seconds

    @Override
    public int getCost() { return 20; }

    @Override
    public boolean cast(Player player) {
        Vector direction = player.getLocation().getDirection();
        
        // Visuals
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WARDEN_SONIC_BOOM, 1f, 1.5f);
        player.getWorld().spawnParticle(Particle.SONIC_BOOM, player.getEyeLocation().add(direction), 1);

        // Logic
        for (Entity entity : player.getNearbyEntities(10, 10, 10)) {
            if (entity instanceof LivingEntity && entity != player) {
                Vector toEntity = entity.getLocation().toVector().subtract(player.getLocation().toVector());
                if (toEntity.angle(direction) <= Math.toRadians(45)) {
                    entity.setVelocity(toEntity.normalize().multiply(2.5).setY(0.5));
                }
            }
        }
        return true;
        
    }
}