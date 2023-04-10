package me.hapyl.fight.game.maps.features;

import me.hapyl.fight.Main;
import me.hapyl.fight.game.Debugger;
import me.hapyl.fight.util.BlockLocation;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BoosterController implements Listener {

	private final Map<Player, Entity> boosterMap = new ConcurrentHashMap<>();

	public BoosterController(Main main) {
		main.getServer().getPluginManager().registerEvents(this, main);

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
		if (booster == null || isOnBooster(player)) {
			return;
		}

		ev.setUseInteractedBlock(Event.Result.DENY);

		final Entity entity = booster.launchAndRide(player, false);
		boosterMap.put(player, entity);

		Debugger.log("init = " + player.getUniqueId());
	}

	public boolean isOnBooster(Player player) {
		return boosterMap.containsKey(player);
	}

	//@EventHandler()
	//public void handleDismount(EntityDismountEvent ev) {
	//	final Entity dismounted = ev.getDismounted();
	//	final Entity entity = ev.getEntity();
	//
	//	checkMountAndCancel(ev, dismounted, entity);
	//}
	//
	//@EventHandler()
	//public void handleDismount0(VehicleExitEvent ev) {
	//	final LivingEntity dismounted = ev.getExited();
	//	final Vehicle entity = ev.getVehicle();
	//
	//	checkMountAndCancel(ev, entity, dismounted);
	//}
	//
	//private void checkMountAndCancel(Cancellable event, @Nonnull Entity dismounted, @Nonnull Entity entity) {
	//	if (!(entity instanceof Player player) || !boosterMap.containsKey(player)) {
	//		return;
	//	}
	//
	//	if (dismounted.isDead()) {
	//		boosterMap.remove(player);
	//		return;
	//	}
	//
	//	dismounted.addPassenger(entity);
	//	event.setCancelled(true);
	//
	//	// FX
	//	Chat.sendTitle(player, "", "&cCannot dismount booster!", 0, 10, 0);
	//	PlayerLib.villagerNo(player);
	//}

}
