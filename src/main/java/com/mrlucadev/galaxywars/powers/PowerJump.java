package com.mrlucadev.galaxywars.powers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import com.mrlucadev.galaxywars.GalaxyWars;
import com.mrlucadev.galaxywars.utils.Lang;

import net.kyori.adventure.text.Component;

public class PowerJump extends PowerAbility implements Listener {

	private final Map<UUID, BukkitTask> safetyTasks = new HashMap<>();
	private final Map<UUID, Long> jumpStartTimes = new HashMap<>();

	public PowerJump() {
		Bukkit.getPluginManager().registerEvents(this, GalaxyWars.getInstance());
	}

	@Override
	public String getId() {
		return "jump";
	}

	@Override
	public Component getDisplayName() {
		return Lang.getMessage("powers.abilities.jump");
	}

	@Override
	public int getCooldown() {
		return GalaxyWars.getCfg().getInt("powers.jump.cooldown");
	}

	@Override
	public int getCost() {
		return GalaxyWars.getCfg().getInt("powers.jump.energycost");
	}

	@Override
	public boolean cast(Player player) {
		Material blockType = player.getLocation().subtract(0, 1, 0).getBlock().getType();
		if (!blockType.isSolid()) {
			return false;
		}

		// Reset fall distance so the server knows a new jump began
		player.setFallDistance(0);

		Vector velocity = player.getLocation().getDirection().multiply(1.5).setY(1.0);
		player.setVelocity(velocity);
		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GHAST_SHOOT, 0.5f, 2.0f);

		UUID id = player.getUniqueId();
		long now = System.currentTimeMillis();

		if (safetyTasks.containsKey(id)) {
			safetyTasks.get(id).cancel();
		}

		jumpStartTimes.put(id, now);

		// Cancel in case no damage taken (landed in water/cobweb/etc)
		BukkitTask task = Bukkit.getScheduler().runTaskLater(GalaxyWars.getInstance(), () -> {
			safetyTasks.remove(id);
			jumpStartTimes.remove(id);
		}, 100L);

		safetyTasks.put(id, task);

		return true;
	}

	@EventHandler
	public void onFallDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player player) {
			if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
				UUID id = player.getUniqueId();

				if (jumpStartTimes.containsKey(id)) {
					// Always cancel the damage if they are in the list
					event.setCancelled(true);

					long now = System.currentTimeMillis();
					long start = jumpStartTimes.getOrDefault(id, 0L);

					// If the jump started less than 300ms ago, it means we JUST launched.
					// This damage event is likely from the *previous* jump's landing.
					// SO: We CANCEL the damage, but we DO NOT remove the safety yet.
					if ((now - start) < 300) {
						return; // Exit here. Keep them safe for the new jump.
					}

					// If it's been more than 300ms, this is a real landing.
					// Clean up normally.
					safetyTasks.get(id).cancel();
					safetyTasks.remove(id);
					jumpStartTimes.remove(id);

				}
			}
		}
	}
}