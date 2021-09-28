package kz.hapyl.fight.game.effect.storage;

import kz.hapyl.fight.game.effect.GameEffect;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import kz.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class Vulnerable extends GameEffect {

	private final int damagePercent = 50;

	public Vulnerable() {
		super("Vulnerable");
		this.setAbout("Players who affected by vulnerability take %s%% more damage.", BukkitUtils.roundTick(damagePercent));
		this.setPositive(false);
	}

	@Override
	public void onTick(Player player, int tick) {
		if (tick == 5) {
			PlayerLib.spawnParticle(player.getLocation().add(0.0d, 1.0d, 0.0d), Particle.VILLAGER_ANGRY, 1, 0.1d, 0.0d, 0.1d, 0.0f);
		}
	}

	@Override
	public void onStart(Player player) {

	}

	@Override
	public void onStop(Player player) {

	}
}
