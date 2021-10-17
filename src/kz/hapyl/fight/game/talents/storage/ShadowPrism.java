package kz.hapyl.fight.game.talents.storage;

import kz.hapyl.fight.game.Manager;
import kz.hapyl.fight.game.Response;
import kz.hapyl.fight.game.talents.Talent;
import kz.hapyl.fight.game.task.GameTask;
import kz.hapyl.fight.util.Nulls;
import kz.hapyl.fight.util.Utils;
import kz.hapyl.spigotutils.module.entity.Entities;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class ShadowPrism extends Talent {

	private final int teleportCd = 400;
	private final Map<Player, ArmorStand> playerPrism = new HashMap<>();

	public ShadowPrism() {
		super("Shadow Prism");
		this.setInfo(
				"Deploy a teleportation orb that travels in straight line. Use again to teleport to the orb.__&e&lLOOK AT BLOCK &7to place it at fixed block.");
		this.setItem(
				"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODNlZDRjZTIzOTMzZTY2ZTA0ZGYxNjA3MDY0NGY3NTk5ZWViNTUzMDdmN2VhZmU4ZDkyZjQwZmIzNTIwODYzYyJ9fX0=");
		this.setCd(DYNAMIC);
	}

	@Override
	public void onStop() {
		playerPrism.clear();
	}

	@Override
	public void onDeath(Player player) {
		final ArmorStand armorStand = getPrism(player);
		Nulls.runIfNotNull(armorStand, ArmorStand::remove);
	}

	public ArmorStand getPrism(Player player) {
		return playerPrism.get(player);
	}

	@Override
	public Response execute(Player player) {
		final ArmorStand prism = getPrism(player);

		// Deploy Prism
		if (prism == null) {

			final Block targetBlock = player.getTargetBlockExact(5);
			final boolean isStill = targetBlock != null;
			Location spawnLocation = player.getLocation();
			if (targetBlock != null) {
				spawnLocation = targetBlock.getRelative(BlockFace.UP).getLocation().add(0.5d, 0.0d, 0.5d);
			}

			final ArmorStand entity = Entities.ARMOR_STAND.spawn(spawnLocation, me -> {
				me.setVisible(false);
				me.setSilent(true);
				me.setInvulnerable(true);
				me.setSmall(true);
				me.getLocation().setYaw(player.getLocation().getYaw());
				if (me.getEquipment() != null) {
					me.getEquipment().setHelmet(this.getItem());
				}
			});

			playerPrism.put(player, entity);
			startCd(player, 20); // fix instant use

			Manager.current().getCurrentGame().getAlivePlayers().forEach(gp -> {
				final Player gpPlayer = gp.getPlayer();
				if (gpPlayer == player) {
					return;
				}
				Utils.hideEntity(entity, gpPlayer);
			});

			// Move task
			new GameTask() {
				private int tick = 100;

				@Override
				public void run() {

					if (tick < 0 || entity.isDead()) {
						this.cancel();
						return;
					}

					if (!isStill) {
						final Vector direction = entity.getLocation().getDirection();
						entity.setVelocity(new Vector(direction.getX(), -1, direction.getZ()).normalize().multiply(0.2d));
					}

					--tick;

					// fx
					PlayerLib.spawnParticle(player, entity.getLocation(), Particle.PORTAL, 10, 0.5d, 0.5d, 0.5d, 0.05f);

				}
			}.runTaskTimer(0, 2);

			// fx
			PlayerLib.playSound(player, Sound.ENTITY_SHULKER_AMBIENT, 1.75f);

			return Response.OK;
		}

		// Teleport to Prism
		startCd(player, teleportCd);
		final Location location = prism.getLocation().add(0.5d, 0.0d, 0.d);
		location.setYaw(player.getLocation().getYaw());
		location.setPitch(player.getLocation().getPitch());

		player.teleport(location);

		prism.remove();
		playerPrism.remove(player);

		// fx
		PlayerLib.addEffect(player, PotionEffectType.BLINDNESS, 20, 1);
		PlayerLib.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 0.75f);

		return Response.OK;
	}
}
