package me.hapyl.fight.game.effect.storage;

import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.effect.EffectParticleBlockMarker;
import me.hapyl.fight.game.effect.GameEffect;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class LockdownEffect extends GameEffect {

    public LockdownEffect() {
        super("Lockdown");

        setDescription("Drastically reduce player's movement speed, locks their ability to attack and use abilities.");
        setPositive(false);
        setEffectParticle(new EffectParticleBlockMarker(1, Material.BARRIER));
    }

    @Override
    public void onStart(Player player) {
        GamePlayer.getPlayer(player).setCanMove(false);

        PlayerLib.addEffect(player, PotionEffectType.SLOW, Integer.MAX_VALUE, 5);
        PlayerLib.addEffect(player, PotionEffectType.WEAKNESS, Integer.MAX_VALUE, 100);
        PlayerLib.playSound(player, Sound.BLOCK_BEACON_ACTIVATE, 0.75f);

        // Force set slot to 7 so abilities cannot be used.
        // Slot 8 is used for extra items like Relics.
        player.getInventory().setHeldItemSlot(7);
    }

    @Override
    public void onStop(Player player) {
        GamePlayer.getPlayer(player).setCanMove(true);

        PlayerLib.removeEffect(player, PotionEffectType.SLOW);
        PlayerLib.removeEffect(player, PotionEffectType.WEAKNESS);

        PlayerLib.playSound(player, Sound.BLOCK_BEACON_ACTIVATE, 2);
    }

    @Override
    public void onTick(Player player, int tick) {
        if (tick == 0) {
            displayParticles(player.getLocation().add(0.0d, 2.0d, 0.0d), player);
        }
    }
}
