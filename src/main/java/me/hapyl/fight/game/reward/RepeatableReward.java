package me.hapyl.fight.game.reward;

import me.hapyl.eterna.module.registry.Key;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class RepeatableReward extends Reward {

    public RepeatableReward(@Nonnull String name) {
        super(Key.empty(), name);
    }

    @Override
    public boolean hasClaimed(@Nonnull Player player) {
        return false;
    }

    @Override
    public void setClaimed(@Nonnull Player player, boolean obtained) {
    }
}
