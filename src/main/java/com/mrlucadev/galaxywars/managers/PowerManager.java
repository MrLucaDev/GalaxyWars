package com.mrlucadev.galaxywars.managers;

import com.mrlucadev.galaxywars.powers.PowerAbility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.*;

public class PowerManager {

    private static final Map<String, PowerAbility> REGISTRY = new HashMap<>();
    
    // Player Data (In a real plugin, save this to database/file)
    private static final Map<UUID, List<String>> UNLOCKED = new HashMap<>();
    private static final Map<UUID, Integer> SELECTED_SLOT = new HashMap<>();
    private static final Map<UUID, Map<String, Long>> COOLDOWNS = new HashMap<>();

    // 1. Register a new Power into the game
    public static void register(PowerAbility ability) {
        REGISTRY.put(ability.getId(), ability);
    }

    // 2. Unlock a power for a player (Add to their belt)
    public static void unlockPower(Player player, String powerId) {
        UNLOCKED.computeIfAbsent(player.getUniqueId(), k -> new ArrayList<>()).add(powerId);
    }

    // 3. Cycle to the next power (The 'F' Key Logic)
    public static void cyclePower(Player player) {
        List<String> belt = UNLOCKED.getOrDefault(player.getUniqueId(), new ArrayList<>());
        
        if (belt.isEmpty()) {
            player.sendActionBar(Component.text("No powers unlocked.", NamedTextColor.GRAY));
            return;
        }

        int currentSlot = SELECTED_SLOT.getOrDefault(player.getUniqueId(), 0);
        currentSlot = (currentSlot + 1) % belt.size(); // Loop back to 0 if at end
        SELECTED_SLOT.put(player.getUniqueId(), currentSlot);

        // Feedback
        String powerId = belt.get(currentSlot);
        PowerAbility ability = REGISTRY.get(powerId);
        player.sendActionBar(Component.text("Selected: ", NamedTextColor.GRAY)
                .append(Component.text(ability.getDisplayName(), NamedTextColor.AQUA)));
    }

    // 4. Cast the currently selected power
    public static void castCurrent(Player player) {
        List<String> belt = UNLOCKED.getOrDefault(player.getUniqueId(), new ArrayList<>());
        if (belt.isEmpty()) return;

        int slot = SELECTED_SLOT.getOrDefault(player.getUniqueId(), 0);
        // Safety check if belt size changed
        if (slot >= belt.size()) {
            slot = 0;
            SELECTED_SLOT.put(player.getUniqueId(), 0);
        }

        String powerId = belt.get(slot);
        PowerAbility ability = REGISTRY.get(powerId);

        if (ability != null) {
            // 1. Check Cooldown
            if (isOnCooldown(player, ability)) return;

            // 2. CHECK if they have enough energy (Don't take it yet!)
            if (!EnergyManager.hasEnough(player, ability.getCost())) {
                player.sendActionBar(Component.text("Not enough energy!", NamedTextColor.RED));
                return;
            }

            // 3. ATTEMPT the cast
            // If the ability returns TRUE (Success), then we pay the price.
            if (ability.cast(player)) {
                
                // 4. Success! Deduct Energy & Set Cooldown
                EnergyManager.take(player, ability.getCost());
                setCooldown(player, ability);
            }
            // If ability.cast() returned FALSE (e.g. failed air check), 
            // we do nothing. No energy lost, no cooldown set.
        }
    }

    // --- Cooldown Helpers ---
    
    private static boolean isOnCooldown(Player player, PowerAbility ability) {
        Map<String, Long> playerCooldowns = COOLDOWNS.getOrDefault(player.getUniqueId(), new HashMap<>());
        if (!playerCooldowns.containsKey(ability.getId())) return false;

        long endTime = playerCooldowns.get(ability.getId());
        if (System.currentTimeMillis() < endTime) {
            long left = (endTime - System.currentTimeMillis()) / 1000;
            player.sendActionBar(Component.text(ability.getDisplayName() + " is ready in " + left + "s", NamedTextColor.RED));
            return true;
        }
        return false;
    }

    private static void setCooldown(Player player, PowerAbility ability) {
        long endTime = System.currentTimeMillis() + (ability.getCooldown() * 1000L);
        COOLDOWNS.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>()).put(ability.getId(), endTime);
    }
}