package me.hapyl.fight.game.talents.storage;

import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.heroes.HeroHandle;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class ShroudedStep extends Talent {
	public ShroudedStep() {
		super("Shrouded Step");
		this.setInfo("Deploy fake footsteps that travel in straight line to fool your opponents.____Can only be used while in Dark Cover!");
		this.setCd(600);
		this.setItem(Material.NETHERITE_BOOTS);
	}

	@Override
	public Response execute(Player player) {
		if (!player.isSneaking()) {
			return Response.error("You must be in &cDark Cover &cto use this!");
		}

		final Location location = player.getLocation();
		final ArmorStand entity = Entities.ARMOR_STAND.spawn(location, me -> {
			me.setVisible(false);
			me.setSilent(true);
			me.setInvulnerable(true);
			me.setSmall(true);
			me.getLocation().setYaw(location.getYaw());
		});

		new GameTask() {
			private int distance = 100;

			@Override
			public void run() {

				final Location entityLocation = entity.getLocation();
				if (distance < 0 || entity.isDead() || GamePlayer.getPlayer(player).isDead()) {
					if (distance < 0) {
						PlayerLib.spawnParticle(entityLocation, Particle.CRIT_MAGIC, 10, 0, 0, 0, 0.5f);
						entity.remove();
					}
					this.cancel();
					return;
				}

				Vector vector = entityLocation.getDirection();
				entity.setVelocity(new Vector(vector.getX(), -1, vector.getZ()).normalize().multiply(0.15f));
				HeroHandle.SHADOW_ASSASSIN.displayFootprints(entityLocation);

				--distance;
			}
		}.runTaskTimer(0, 2);

		return Response.OK;
	}
}
