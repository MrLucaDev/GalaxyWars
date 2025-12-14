package com.mrlucadev.galaxywars.powers;

import org.bukkit.entity.Player;

import net.kyori.adventure.text.Component;

public abstract class PowerAbility {

	public abstract String getId(); // Internal ID (e.g., "push")

	public abstract Component getDisplayName(); // Display Name (e.g., "Power Push")

	public abstract int getCooldown(); // In seconds

	public abstract int getCost(); // Energy cost

	public abstract boolean cast(Player player);

}
