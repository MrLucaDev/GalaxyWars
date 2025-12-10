package com.mrlucadev.galaxywars.utils;

import com.mrlucadev.galaxywars.GalaxyWars;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Lang {

    private static FileConfiguration messages;
    private static final MiniMessage mm = MiniMessage.miniMessage();

    public static void load() {
        GalaxyWars plugin = GalaxyWars.getInstance();
        File file = new File(plugin.getDataFolder(), "messages.yml");
        if (!file.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messages = YamlConfiguration.loadConfiguration(file);
    }

    public static Component getMessage(String key) {
        String prefix = messages.getString("prefix", "");
        String msg = messages.getString(key, "<red>Missing: " + key);
        return mm.deserialize(prefix + msg);
    }
    
    public static Component getItemName(String key) {
        String msg = messages.getString(key, "<red>Missing: " + key);
        return mm.deserialize(msg);
    }

    public static List<Component> getList(String key) {
        List<Component> componentList = new ArrayList<>();
        List<String> rawList = messages.getStringList(key);

        if (rawList.isEmpty()) {
            componentList.add(mm.deserialize("<red>Missing List: " + key));
            return componentList;
        }

        for (String line : rawList) {
            componentList.add(mm.deserialize(line));
        }
        return componentList;
    }
}