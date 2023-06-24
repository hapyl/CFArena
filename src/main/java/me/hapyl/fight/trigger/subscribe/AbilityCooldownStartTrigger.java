package me.hapyl.fight.trigger.subscribe;

import me.hapyl.fight.trigger.PlayerTrigger;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class AbilityCooldownStartTrigger extends PlayerTrigger {

    private final ItemStack item;
    private final int cooldown;

    public AbilityCooldownStartTrigger(Player player, ItemStack item, int cooldown) {
        super(player);

        this.item = item;
        this.cooldown = cooldown;
    }

    public ItemStack getItem() {
        return item;
    }

    public int getCooldown() {
        return cooldown;
    }
}
