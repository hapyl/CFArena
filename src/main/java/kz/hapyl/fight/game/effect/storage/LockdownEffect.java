package kz.hapyl.fight.game.effect.storage;

import kz.hapyl.fight.game.effect.EffectParticle;
import kz.hapyl.fight.game.effect.GameEffect;
import kz.hapyl.fight.game.effect.storage.extra.LockdownData;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

public class LockdownEffect extends GameEffect {

	private final Map<Player, LockdownData> data = new HashMap<>();

	public LockdownEffect() {
		super("Lockdown");
		this.setPositive(false);
		this.setEffectParticle(new EffectParticle(Particle.BLOCK_MARKER, 1));
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

		PlayerLib.addEffect(player, PotionEffectType.SLOW, 165, 100);
		PlayerLib.addEffect(player, PotionEffectType.WEAKNESS, 165, 100);
		PlayerLib.playSound(player, Sound.BLOCK_BEACON_ACTIVATE, 0.75f);

		player.getInventory().setHeldItemSlot(7);

	}

	@Override
	public void onStop(Player player) {
		applyOldData(player);
		data.remove(player);
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
