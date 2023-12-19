package me.hapyl.fight.game.effect.archive;

import me.hapyl.fight.event.custom.GameDamageEvent;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.effect.GameEffect;
import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;

public class FallDamageResistance extends GameEffect implements Listener {

    public FallDamageResistance() {
        super("Fall Damage Resistance");
        this.setDescription("Negates all fall damage until it's taken.");
    }

    @EventHandler()
    public void handleDamageEvent(GameDamageEvent ev) {
        final LivingGameEntity entity = ev.getEntity();
        final EnumDamageCause cause = ev.getCause();

        if (cause != EnumDamageCause.FALL) {
            return;
        }

        ev.setCancelled(true);
        entity.removeEffect(GameEffectType.FALL_DAMAGE_RESISTANCE);
    }

    @Override
    public void onTick(@Nonnull LivingGameEntity entity, int tick) {

    }

    @Override
    public void onStart(@Nonnull LivingGameEntity entity) {

    }

    @Override
    public void onStop(@Nonnull LivingGameEntity entity) {

    }
}
