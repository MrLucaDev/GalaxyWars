package com.mrlucadev.galaxywars;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.mrlucadev.galaxywars.listeners.InputListener;
import com.mrlucadev.galaxywars.managers.EnergyManager;
import com.mrlucadev.galaxywars.managers.PowerManager;
import com.mrlucadev.galaxywars.powers.PowerJump;
import com.mrlucadev.galaxywars.powers.PowerPull;
import com.mrlucadev.galaxywars.powers.PowerPush;
import com.mrlucadev.galaxywars.utils.Lang;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class GalaxyWars extends JavaPlugin implements Listener {

	private static GalaxyWars instance;

	@Override
	public void onEnable() {
		instance = this;
		saveDefaultConfig();
		Lang.load();

		PowerManager.register(new PowerPush());
		PowerManager.register(new PowerJump());
		PowerManager.register(new PowerPull());

		EnergyManager.init();

		getServer().getPluginManager().registerEvents(new InputListener(), this);
		getServer().getPluginManager().registerEvents(this, this);

		// Temporary Give every player the powers instantly.
		// In real game, they would "learn" these.
		getServer().getOnlinePlayers().forEach(p -> {
			PowerManager.unlockPower(p, "push");
			PowerManager.unlockPower(p, "jump");
			PowerManager.unlockPower(p, "pull");
		});

		getComponentLogger().info(Component.text("GalaxyWars has engaged hyperdrive!", NamedTextColor.GOLD));
	}

	// Add this to ensure players who join later get powers (temporary)
	@org.bukkit.event.EventHandler
	public void onJoin(PlayerJoinEvent e) {
		PowerManager.unlockPower(e.getPlayer(), "push");
		PowerManager.unlockPower(e.getPlayer(), "jump");
		PowerManager.unlockPower(e.getPlayer(), "pull");
	}

	@Override
	public void onDisable() {
		getComponentLogger().info(Component.text("GalaxyWars systems shutting down...", NamedTextColor.RED));
	}

	public static GalaxyWars getInstance() {
		return instance;
	}

	public static FileConfiguration getCfg() {
		return instance.getConfig();
	}
}