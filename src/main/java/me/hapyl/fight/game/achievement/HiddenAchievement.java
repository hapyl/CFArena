package me.hapyl.fight.game.achievement;

import me.hapyl.fight.game.color.Color;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

/**
 * Represents a hidden achievement.
 * For the most part, a normal achievement but not shown in GUI until unlocked.
 */
public class HiddenAchievement extends Achievement {
    public HiddenAchievement(@Nonnull String name, @Nonnull String description) {
        super(name, description);

        setPointReward(10); // default x2 for hidden because why not?
    }

    @Override
    public void format(Player player, ItemBuilder builder) {
        final boolean isComplete = isComplete(player);

        if (isComplete) {
            super.format(player, builder);
            return;
        }

        builder.setName(Color.ERROR + "???");
        builder.addLore();
        builder.addLore("???");
    }

    @Nonnull
    @Override
    public String getType() {
        return "Secret Achievement";
    }
}
