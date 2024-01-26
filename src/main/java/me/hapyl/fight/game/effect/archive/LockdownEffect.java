package me.hapyl.fight.game.effect.archive;

import me.hapyl.fight.game.effect.Effect;
import me.hapyl.fight.game.effect.EffectParticleBlockMarker;
import me.hapyl.fight.game.effect.EffectType;
import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;

@Deprecated
public class LockdownEffect extends Effect {

    public LockdownEffect() {
        super("Lockdown", EffectType.NEGATIVE);

        setDescription("""
                Drastically reduce player's movement speed, locks their ability to attack and use abilities.
                """);

        setTalentBlocking(true);
        setEffectParticle(new EffectParticleBlockMarker(1, Material.BARRIER));
    }

    @Override
    public void onStart(@Nonnull LivingGameEntity entity, int amplifier) {
        entity.setCanMove(false);

        // Force set slot to 7 so abilities cannot be used.
        // Slot 8 is used for extra items like Relics.
        entity.asPlayer(player -> {
            player.getInventory().setHeldItemSlot(7);
        });

        entity.addPotionEffectIndefinitely(PotionEffectType.SLOW, 5);

        // Fx
        entity.playSound(Sound.BLOCK_BEACON_ACTIVATE, 0.75f);
    }

    @Override
    public void onStop(@Nonnull LivingGameEntity entity, int amplifier) {
        entity.setCanMove(true);
        entity.removePotionEffect(PotionEffectType.SLOW);

        // Fx
        entity.playSound(Sound.BLOCK_BEACON_ACTIVATE, 2);
    }

    @Override
    public void onTick(@Nonnull LivingGameEntity entity, int tick) {
        if (tick % 20 == 0) {
            displayParticles(entity.getLocation().add(0.0d, 2.0d, 0.0d), entity);
        }
    }
}
