package com.mrlucadev.galaxywars;

import org.bukkit.plugin.java.JavaPlugin;

import com.mrlucadev.galaxywars.utils.Lang;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class GalaxyWars extends JavaPlugin {

	private static GalaxyWars instance;

	@Override
	public void onEnable() {
		instance = this;
		saveDefaultConfig();
		Lang.load();

        getServer().getPluginManager().registerEvents(new com.mrlucadev.galaxywars.listeners.PowerListener(), this);

		getComponentLogger().info(Component.text("GalaxyWars has engaged hyperdrive!", NamedTextColor.GOLD));
	}

	@Override
	public void onDisable() {
		getComponentLogger().info(Component.text("GalaxyWars systems shutting down...", NamedTextColor.RED));
	}

	public static GalaxyWars getInstance() {
		return instance;
	}
}