package com.mrlucadev.galaxywars;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import com.mrlucadev.galaxywars.managers.PowerManager;
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
		
		// 1. Initialize Managers
        PowerManager.register(new com.mrlucadev.galaxywars.powers.PowerPush());
        PowerManager.register(new com.mrlucadev.galaxywars.powers.PowerJump());

        com.mrlucadev.galaxywars.managers.EnergyManager.init();
        
        // 2. Register Listeners
        getServer().getPluginManager().registerEvents(new com.mrlucadev.galaxywars.listeners.InputListener(), this);
        getServer().getPluginManager().registerEvents(this, this);
        
        // Temporary Debug: Give every player the powers instantly so you can test
        // In real game, they would "learn" these.
        getServer().getOnlinePlayers().forEach(p -> {
            PowerManager.unlockPower(p, "push");
            PowerManager.unlockPower(p, "jump");
        });

		getComponentLogger().info(Component.text("GalaxyWars has engaged hyperdrive!", NamedTextColor.GOLD));
	}
	
	// Add this to ensure players who join later get powers (for testing)
    @org.bukkit.event.EventHandler
    public void onJoin(org.bukkit.event.player.PlayerJoinEvent e) {
        PowerManager.unlockPower(e.getPlayer(), "push");
        PowerManager.unlockPower(e.getPlayer(), "jump");
    }

	@Override
	public void onDisable() {
		getComponentLogger().info(Component.text("GalaxyWars systems shutting down...", NamedTextColor.RED));
	}

	public static GalaxyWars getInstance() {
		return instance;
	}
}