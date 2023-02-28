package me.hapyl.fight.game.talents.storage.witcher;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.util.Utils;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Aard extends Talent {
	public Aard() {
		super("Aard", "Creates a small explosion in front of you that pushes enemies away.");
		this.setItem(Material.HEART_OF_THE_SEA);
		this.setCdSec(5);
	}

	@Override
	public Response execute(Player player) {
		final Vector vector = player.getLocation().getDirection().setY(0.125d).multiply(2.0d);
		final Location inFront = player.getLocation().add(vector);
		final World world = inFront.getWorld();

		if (world == null) {
			return Response.error("world is null");
		}

		Utils.getEntitiesInRange(inFront, 4.0d).forEach(entity -> {
			if (entity == player) {
				return;
			}
			entity.setVelocity(vector);
		});
		// fx
		PlayerLib.playSound(inFront, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 0.25f);
		PlayerLib.spawnParticle(inFront, Particle.EXPLOSION_LARGE, 1, 0, 0, 0, 0);

		return Response.OK;
	}
}
