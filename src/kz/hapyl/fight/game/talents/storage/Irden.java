package kz.hapyl.fight.game.talents.storage;

import kz.hapyl.fight.game.GamePlayer;
import kz.hapyl.fight.game.Response;
import kz.hapyl.fight.game.effect.GameEffectType;
import kz.hapyl.fight.game.talents.Talent;
import kz.hapyl.fight.game.task.GameTask;
import kz.hapyl.fight.util.Utils;
import kz.hapyl.spigotutils.module.math.Geometry;
import kz.hapyl.spigotutils.module.math.gometry.Quality;
import kz.hapyl.spigotutils.module.math.gometry.WorldParticle;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class Irden extends Talent {

	private final int lifeTime = 200;
	private final double radius = 3.5d;

	public Irden() {
		super(
				"Yrden",
				"Creates Yrden aura at your current location. Opponents inside the aura are &bslowed&7, &binvulnerable&7, &bweakened &7and aren't affected by knockback."
		);
		this.setCdSec(25);
		this.setItem(Material.POPPED_CHORUS_FRUIT);
	}

	@Override
	public Response execute(Player player) {
		final Location location = player.getLocation();

		new GameTask() {
			private int tick = lifeTime;

			@Override
			public void run() {

				if (tick-- <= 0) {
					this.cancel();
					return;
				}

				affect(player, location, tick);

			}
		}.runTaskTimer(0, 1);
		return Response.OK;
	}

	public void affect(Player player, Location location, int tick) {
		if (tick % 20 == 0 || tick == (lifeTime - 1)) {
			Geometry.drawCircle(location, radius, Quality.HIGH, new WorldParticle(Particle.SPELL_WITCH));
		}

		Utils.getPlayersInRange(location, radius).forEach(target -> {
			if (target == player) {
				return;
			}

			PlayerLib.addEffect(target, PotionEffectType.SLOW, 5, 3);
			PlayerLib.addEffect(target, PotionEffectType.WEAKNESS, 5, 3);
			GamePlayer.getPlayer(player).addEffect(GameEffectType.VULNERABLE, 5, true);
			GamePlayer.getPlayer(player).addEffect(GameEffectType.IMMOVABLE, 5, true);
		});
	}

}
