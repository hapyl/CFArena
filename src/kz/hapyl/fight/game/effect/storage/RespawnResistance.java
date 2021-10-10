package kz.hapyl.fight.game.effect.storage;

import kz.hapyl.fight.game.effect.EffectParticle;
import kz.hapyl.fight.game.effect.GameEffect;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class RespawnResistance extends GameEffect {
	public RespawnResistance() {
		super("Respawn Resistance");
		this.setEffectParticle(new EffectParticle(Particle.CRIT_MAGIC, 5, 0.25d, 0.5d, 0.25d, 0.1f));
	}

	@Override
	public void onStart(Player player) {
		player.setInvulnerable(true);
	}

	@Override
	public void onStop(Player player) {
		player.setInvulnerable(false);
	}

	@Override
	public void onTick(Player player, int tick) {
		if (tick == 5) {
			displayParticles(player.getLocation(), player);
		}
	}
}
