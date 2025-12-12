package com.mrlucadev.galaxywars.powers;

import org.bukkit.entity.Player;

public abstract class PowerAbility {
	
	public abstract String getId(); 			// Internal ID (e.g., "push")
	public abstract String getDisplayName();	// Display Name (e.g., "Power Push")
	public abstract int getCooldown();			// In seconds
	public abstract int getCost();				// Energy cost
	
	public abstract boolean cast(Player player);

}
