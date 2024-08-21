package me.hapyl.fight.game.effect.archive;

import me.hapyl.fight.CF;
import me.hapyl.fight.game.effect.Effect;
import me.hapyl.fight.game.effect.EffectType;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.entity.cooldown.Cooldown;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.Random;

public class Amnesia extends Effect implements Listener {

    public Amnesia() {
        super("Amnesia", EffectType.NEGATIVE);

        setDescription("""
                Players will move randomly and their vision is disturbed.
                """);
    }

    @EventHandler(ignoreCancelled = true)
    public void handlePlayerMove(PlayerMoveEvent ev) {
        final GamePlayer player = CF.getPlayer(ev.getPlayer());

        if (player == null) {
            return;
        }

        if (player.hasEffect(Effects.AMNESIA) && !player.hasCooldown(Cooldown.AMNESIA)) {
            final double pushSpeed = player.isSneaking() ? 0.05d : 0.1d;

            player.setVelocity(new Vector(
                    new Random().nextBoolean() ? pushSpeed : -pushSpeed,
                    -0.2723,
                    new Random().nextBoolean() ? pushSpeed : -pushSpeed
            ));

            player.startCooldown(Cooldown.AMNESIA, 50);
        }
    }

    @Override
    public void onStart(@Nonnull LivingGameEntity entity, int amplifier, int duration) {
        entity.addPotionEffectIndefinitely(PotionEffectType.NAUSEA, 1);
    }

    @Override
    public void onStop(@Nonnull LivingGameEntity entity, int amplifier) {
        entity.removePotionEffect(PotionEffectType.NAUSEA);
    }
}
