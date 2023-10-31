package me.hapyl.fight.game.entity.shield;

import me.hapyl.fight.game.entity.GamePlayer;

import javax.annotation.Nonnull;

public class HitShield extends Shield {
    public HitShield(@Nonnull GamePlayer player, double maxCapacity) {
        super(player, maxCapacity, 0.0f);
    }

    @Override
    public void takeDamage(double damage) {
        capacity--;
    }
}
