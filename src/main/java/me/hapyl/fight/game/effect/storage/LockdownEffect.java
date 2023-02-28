package me.hapyl.fight.game.effect.storage;

import me.hapyl.fight.game.effect.EffectParticleBlockMarker;
import me.hapyl.fight.game.effect.GameEffect;
import me.hapyl.fight.game.effect.storage.extra.LockdownData;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

public class LockdownEffect extends GameEffect {

    private final Map<Player, LockdownData> data = new HashMap<>();

    public LockdownEffect() {
        super("Lockdown");
        this.setDescription("Removes player's ability to move, attack or use abilities.");
        this.setPositive(false);
        this.setEffectParticle(new EffectParticleBlockMarker(1, Material.BARRIER));
    }

    @Override
    public void onStart(Player player) {
        if (data.containsKey(player)) {
            data.get(player).applyData(player);
        }

        data.put(player, new LockdownData(player));

        player.setAllowFlight(true);
        player.setFlying(true);
        player.setFlySpeed(0.0f);

        PlayerLib.addEffect(player, PotionEffectType.SLOW, Integer.MAX_VALUE, 100);
        PlayerLib.addEffect(player, PotionEffectType.WEAKNESS, Integer.MAX_VALUE, 100);
        PlayerLib.playSound(player, Sound.BLOCK_BEACON_ACTIVATE, 0.75f);

        // Force set slot to 7 so abilities cannot be used.
        // Slot 8 is used for extra items like Relics.
        player.getInventory().setHeldItemSlot(7);
    }

    @Override
    public void onStop(Player player) {
        applyOldData(player);
        data.remove(player);

        PlayerLib.removeEffect(player, PotionEffectType.SLOW);
        PlayerLib.removeEffect(player, PotionEffectType.WEAKNESS);

        PlayerLib.playSound(player, Sound.BLOCK_BEACON_ACTIVATE, 2);
    }

    private void applyOldData(Player player) {
        final LockdownData lockdownData = data.get(player);
        if (lockdownData != null) {
            lockdownData.applyData(player);
        }
    }

    @Override
    public void onTick(Player player, int tick) {
        if (tick == 0) {
            displayParticles(player.getLocation().add(0.0d, 2.0d, 0.0d), player);
        }
    }
}
