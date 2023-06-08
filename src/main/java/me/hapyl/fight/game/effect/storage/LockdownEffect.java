package me.hapyl.fight.game.effect.storage;

import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.effect.EffectParticleBlockMarker;
import me.hapyl.fight.game.effect.GameEffect;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
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
    public void onStart(LivingEntity entity) {
        if (entity instanceof Player player) {
            GamePlayer.getPlayer(player).setCanMove(false);
            PlayerLib.playSound(player, Sound.BLOCK_BEACON_ACTIVATE, 0.75f);

            // Force set slot to 7 so abilities cannot be used.
            // Slot 8 is used for extra items like Relics.
            player.getInventory().setHeldItemSlot(7);
        }

        entity.addPotionEffect(PotionEffectType.SLOW.createEffect(Integer.MAX_VALUE, 5));
        entity.addPotionEffect(PotionEffectType.WEAKNESS.createEffect(Integer.MAX_VALUE, 5));
    }

    @Override
    public void onStop(LivingEntity entity) {
        if (entity instanceof Player player) {
            GamePlayer.getPlayer(player).setCanMove(true);
            PlayerLib.playSound(player, Sound.BLOCK_BEACON_ACTIVATE, 2);
        }

        entity.removePotionEffect(PotionEffectType.SLOW);
        entity.removePotionEffect(PotionEffectType.WEAKNESS);
    }

    @Override
    public void onTick(LivingEntity entity, int tick) {
        if (tick == 0) {
            displayParticles(entity.getLocation().add(0.0d, 2.0d, 0.0d), entity);
        }
    }
}
