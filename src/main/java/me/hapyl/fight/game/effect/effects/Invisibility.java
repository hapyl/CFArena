package me.hapyl.fight.game.effect.effects;

import me.hapyl.fight.game.effect.Effect;
import me.hapyl.fight.game.effect.Type;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.util.Collect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;

public class Invisibility extends Effect {

    public Invisibility() {
        super("Invisibility", Type.POSITIVE);

        setDescription("""
                Completely hides the player.
                """);
    }

    @Override
    public void onStart(@Nonnull LivingGameEntity entity, int amplifier, int duration) {
        if (entity instanceof GamePlayer player) {
            player.hidePlayer();
        }

        entity.addPotionEffectIndefinitely(PotionEffectType.INVISIBILITY, 1);

        boolean loseAggro = true;

        if (entity instanceof GamePlayer player) {
            loseAggro = !player.getHero().isValidIfInvisible(player);
        }

        if (loseAggro) {
            Collect.nearbyEntities(entity.getLocation(), 10).forEach(target -> {
                final LivingGameEntity targetEntity = target.getTargetEntity();

                if (targetEntity != entity) {
                    return;
                }

                target.setTarget(null);
            });
        }
    }

    @Override
    public void onStop(@Nonnull LivingGameEntity entity, int amplifier) {
        if (entity instanceof GamePlayer player) {
            player.showPlayer();
        }
        
        entity.removePotionEffect(PotionEffectType.INVISIBILITY);
    }
}
