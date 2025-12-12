package com.mrlucadev.galaxywars.powers;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class PowerJump extends PowerAbility {

    @Override
    public String getId() { return "jump"; }

    @Override
    public String getDisplayName() { return "Power Jump"; }

    @Override
    public int getCooldown() { return 0; }

    @Override
    public int getCost() { return 10; }

    @Override
    public boolean cast(Player player) {
    	Material blockType = player.getLocation().subtract(0, 1, 0).getBlock().getType();
    	if(!blockType.isSolid() ) { return false; }
        Vector velocity = player.getLocation().getDirection().multiply(1.5).setY(1.0);
        player.setVelocity(velocity);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GHAST_SHOOT, 0.5f, 2.0f);
        return true;
    }
}