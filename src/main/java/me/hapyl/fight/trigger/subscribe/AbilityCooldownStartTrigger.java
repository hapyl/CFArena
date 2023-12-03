package me.hapyl.fight.trigger.subscribe;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.trigger.EntityTrigger;
import org.bukkit.inventory.ItemStack;

public class AbilityCooldownStartTrigger extends EntityTrigger {

    private final ItemStack item;
    private final int cooldown;

    public AbilityCooldownStartTrigger(GamePlayer player, ItemStack item, int cooldown) {
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
