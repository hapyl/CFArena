package me.hapyl.fight.game.maps.features;

import me.hapyl.fight.Main;
import me.hapyl.fight.util.BlockLocation;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.spigotmc.event.entity.EntityDismountEvent;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BoosterController implements Listener {

	private final Map<Player, Entity> boosterMap = new ConcurrentHashMap<>();

	public BoosterController() {
		new BukkitRunnable() {
			@Override
			public void run() {
				if (boosterMap.isEmpty()) {
					return;
				}

				boosterMap.forEach((player, entity) -> {
					if (entity.isDead() || entity.isOnGround()) {
						boosterMap.remove(player);
						entity.getPassengers().forEach(Entity::eject);
						entity.remove();
					}
				});
			}
		}.runTaskTimer(Main.getPlugin(), 0, 2);
	}

	@EventHandler()
	public void handleBoosterLaunch(PlayerInteractEvent ev) {
		final Player player = ev.getPlayer();
		final Action action = ev.getAction();
		final Block block = ev.getClickedBlock();

		if (action != Action.PHYSICAL
				|| block == null
				|| block.getType() != Material.HEAVY_WEIGHTED_PRESSURE_PLATE
				|| boosterMap.containsKey(player)) {
			return;
		}

		final Booster booster = Booster.byLocation(new BlockLocation(block.getLocation()));
		if (booster == null) {
			return;
		}

		final Entity entity = booster.launchAndRide(player, false);
		boosterMap.put(player, entity);

	}

	@EventHandler()
	public void handleDismount(EntityDismountEvent ev) {
		final Entity dismounted = ev.getDismounted();
		final Entity entity = ev.getEntity();

		checkMountAndCancel(ev, dismounted, entity);
	}

	@EventHandler()
	public void handleDismount0(VehicleExitEvent ev) {
		final LivingEntity dismounted = ev.getExited();
		final Vehicle entity = ev.getVehicle();

		checkMountAndCancel(ev, entity, dismounted);
	}

	private void checkMountAndCancel(Cancellable event, @Nonnull Entity dismounted, @Nonnull Entity entity) {
		if (!(entity instanceof Player player) || !boosterMap.containsKey(player)) {
			return;
		}

		if (dismounted.isDead()) {
			boosterMap.remove(player);
			return;
		}

		dismounted.addPassenger(entity);
		event.setCancelled(true);

		// FX
		Chat.sendTitle(player, "", "&cCannot dismount booster!", 0, 10, 0);
		PlayerLib.villagerNo(player);
	}

}
