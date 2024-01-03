package me.hapyl.fight.game.effect.archive;

import me.hapyl.fight.game.Debug;
import me.hapyl.fight.game.effect.GameEffect;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.Collect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;

public class Invisibility extends GameEffect {

    public Invisibility() {
        super("Invisibility");
        setDescription("Makes player invisible.");
    }

    @Override
    public void onStart(@Nonnull LivingGameEntity entity) {
        entity.asPlayer(CFUtils::hidePlayer);
        entity.addPotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1);

        // Lose target
        Collect.nearbyEntities(entity.getLocation(), 10).forEach(target -> {
            final LivingGameEntity targetEntity = target.getTargetEntity();

            if (targetEntity != entity) {
                return;
            }

            target.setTarget(null);
        });
    }

    @Override
    public void onStop(@Nonnull LivingGameEntity entity) {
        entity.asPlayer(CFUtils::showPlayer);
        entity.removePotionEffect(PotionEffectType.INVISIBILITY);
    }

    @Override
    public void onTick(@Nonnull LivingGameEntity entity, int tick) {
    }
}
