package me.hapyl.fight.game.talents.alchemist;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talent;

import javax.annotation.Nonnull;

public class PotionBundle extends Talent {
    public PotionBundle(@Nonnull Key key) {
        super(key, "Bundle o' Potions");

        setDescription("""
                Dash forward and throw a deadly potion.
                """);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        return null;
    }
}
