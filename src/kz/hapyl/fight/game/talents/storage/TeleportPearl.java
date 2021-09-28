package kz.hapyl.fight.game.talents.storage;

import kz.hapyl.fight.game.GamePlayer;
import kz.hapyl.fight.game.Response;
import kz.hapyl.fight.game.talents.Talent;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.HashSet;
import java.util.Set;

public class TeleportPearl extends Talent implements Listener {

	private final Set<EnderPearl> enderPearls = new HashSet<>();

	public TeleportPearl() {
		// Throw an ender pearl forward, you will teleport to it once it landed. &e&lSNEAK &7while throwing to mount and ride it all the way!
		// Throw an ender pearl and mount to ride it all the way! &e&lSNEAK &7to throw normally.
		super("Rideable Pearl", "Throw an ender pearl and mount to ride it all the way! &e&lSNEAK &7to throw normally.____&7Heals &c3 ❤ &7upon teleport.", Type.COMBAT);
		this.setCd(160);
		this.setItem(Material.ENDER_PEARL);
	}

	@Override
	public void onStop() {
		enderPearls.clear();
	}

	@Override
	public Response execute(Player player) {

		final EnderPearl pearl = player.launchProjectile(EnderPearl.class);
		enderPearls.add(pearl);
		pearl.setShooter(player);

		if (!player.isSneaking()) {
			PlayerLib.playSound(player, Sound.ENTITY_HORSE_SADDLE, 1.5f);
			pearl.addPassenger(player);
		}

		return Response.OK;
	}

	@EventHandler()
	public void handleProjectileHitEvent(ProjectileHitEvent ev) {
		if (ev.getEntity() instanceof EnderPearl pearl && enderPearls.contains(pearl)) {
			if (pearl.getShooter() instanceof Player player) {
				GamePlayer.getPlayer(player).heal(3);
				PlayerLib.spawnParticle(player.getEyeLocation().add(0.0d, 0.5d, 0.0d), Particle.HEART, 1, 0, 0, 0, 0);
			}
			enderPearls.remove(pearl);
		}
	}
}
