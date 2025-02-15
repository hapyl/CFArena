package me.hapyl.fight.game.effect.effects;

import me.hapyl.fight.event.custom.GameDamageEvent;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.effect.Effect;
import me.hapyl.fight.game.effect.EffectType;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;

public class FallDamageResistance extends Effect implements Listener {

    public FallDamageResistance() {
        super("Fall Damage Resistance", EffectType.POSITIVE);

        setDescription("""
                Negates fall damage.
                """);
    }

    @EventHandler()
    public void handleDamageEvent(GameDamageEvent ev) {
        final LivingGameEntity entity = ev.getEntity();
        final DamageCause cause = ev.getCause();

        if (cause != DamageCause.FALL || !entity.hasEffect(Effects.FALL_DAMAGE_RESISTANCE)) {
            return;
        }

        ev.setCancelled(true);
        entity.removeEffect(Effects.FALL_DAMAGE_RESISTANCE);
    }

    @Override
    public void onStart(@Nonnull LivingGameEntity entity, int amplifier, int duration) {
    }

    @Override
    public void onStop(@Nonnull LivingGameEntity entity, int amplifier) {
    }
}
