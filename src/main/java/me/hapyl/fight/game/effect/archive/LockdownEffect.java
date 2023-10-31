package me.hapyl.fight.game.effect.archive;

import me.hapyl.fight.game.effect.EffectParticleBlockMarker;
import me.hapyl.fight.game.effect.GameEffect;
import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.potion.PotionEffectType;

public class LockdownEffect extends GameEffect {

    public LockdownEffect() {
        super("Lockdown");

        setDescription("Drastically reduce player's movement speed, locks their ability to attack and use abilities.");
        setPositive(false);
        setTalentBlocking(true);
        setEffectParticle(new EffectParticleBlockMarker(1, Material.BARRIER));
    }

    @Override
    public void onStart(LivingGameEntity entity) {
        entity.setCanMove(false);

        // Force set slot to 7 so abilities cannot be used.
        // Slot 8 is used for extra items like Relics.
        entity.asPlayer(player -> {
            player.getInventory().setHeldItemSlot(7);
        });

        entity.addPotionEffect(PotionEffectType.SLOW.createEffect(Integer.MAX_VALUE, 5));
        entity.addPotionEffect(PotionEffectType.WEAKNESS.createEffect(Integer.MAX_VALUE, 5));

        // Fx
        entity.playSound(Sound.BLOCK_BEACON_ACTIVATE, 0.75f);
    }

    @Override
    public void onStop(LivingGameEntity entity) {
        entity.setCanMove(true);
        entity.removePotionEffect(PotionEffectType.SLOW);
        entity.removePotionEffect(PotionEffectType.WEAKNESS);

        // Fx
        entity.playSound(Sound.BLOCK_BEACON_ACTIVATE, 2);
    }

    @Override
    public void onTick(LivingGameEntity entity, int tick) {
        if (tick == 0) {
            displayParticles(entity.getLocation().add(0.0d, 2.0d, 0.0d), entity.getEntity());
        }
    }
}
