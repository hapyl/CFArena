package me.hapyl.fight.game.heroes.archive.shadow_assassin;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.Outline;
import org.bukkit.Color;
import org.bukkit.Particle;

import javax.annotation.Nonnull;

public enum AssassinMode {

    STEALTH(new Particle.DustTransition(Color.fromRGB(9, 65, 133), Color.fromRGB(75, 130, 196), 2)) {
        @Override
        protected void switchTo(@Nonnull GamePlayer player, @Nonnull ShadowAssassin assassin) {
            assassin.getEquipment().equip(player);
            player.setOutline(Outline.CLEAR);
        }
    },
    FURY(new Particle.DustTransition(Color.fromRGB(99, 6, 6), Color.fromRGB(230, 48, 48), 2)) {
        @Override
        protected void switchTo(@Nonnull GamePlayer player, @Nonnull ShadowAssassin assassin) {
            assassin.furyEquipment.equip(player);
            player.setOutline(Outline.RED);
        }
    };

    private final Particle.DustTransition transition;

    AssassinMode(Particle.DustTransition transition) {
        this.transition = transition;
    }

    @Nonnull
    public Particle.DustTransition getTransition() {
        return transition;
    }

    protected void switchTo(@Nonnull GamePlayer player, @Nonnull ShadowAssassin assassin) {
    }

}
