package me.hapyl.fight.game.talents.alchemist;

import me.hapyl.fight.game.effect.EffectType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.alchemist.ActivePotion;
import me.hapyl.fight.game.heroes.alchemist.AlchemistData;
import me.hapyl.fight.util.CFUtils;
import org.bukkit.Color;

import javax.annotation.Nonnull;

public class AlchemistPotionInvisibility extends AlchemistPotion {
    public AlchemistPotionInvisibility() {
        super("Potion of Invisibility", 15, Color.fromRGB(110, 138, 150));

        setDescription("""
                Makes you completely %sinvisible&7 for &b%s&7.
                &8&o;;Unless you deal damage.
                """.formatted(me.hapyl.fight.game.color.Color.SKY_BLUE, CFUtils.formatTick(getDuration())));
    }

    @Nonnull
    @Override
    public ActivePotion use(@Nonnull AlchemistData data, @Nonnull GamePlayer player) {
        player.addEffect(EffectType.INVISIBILITY, getDuration());

        return new ActivePotion(data, player, this);
    }
}
