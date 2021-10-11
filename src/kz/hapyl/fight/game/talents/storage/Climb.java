package kz.hapyl.fight.game.talents.storage;

import kz.hapyl.fight.game.Response;
import kz.hapyl.fight.game.talents.Talent;
import kz.hapyl.fight.game.task.GameTask;
import kz.hapyl.fight.util.Supplier;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

public class Climb extends Talent {

	private final Map<Player, GameTask> tasks = new HashMap<>();

	public Climb() {
		super(
				"Climb",
				"Use the wall you're hugging to climb it and perform back-flip, gaining speed boost. Cooldown of this ability stars upon landing or after &b4s&7.____&9Cooldown: &l8s",
				Material.LEATHER_BOOTS
		);
	}

	@Override
	public void onStop() {
		tasks.clear();
	}

	@Override
	protected Response execute(Player player) {
		final Location playerLocation = player.getLocation();
		final Location location = playerLocation.add(playerLocation.getDirection().multiply(1).setY(0.0d));
		if (location.getBlock().getType().isAir()) {
			return Response.error("Not hugging wall.");
		}

		// Flip
		player.teleport(new Supplier<>(player.getLocation()).supply(loc -> loc.setYaw(loc.getYaw() + 180)));

		player.setVelocity(playerLocation.getDirection().multiply(-1.35).setY(0.75d));
		PlayerLib.addEffect(player, PotionEffectType.SPEED, 60, 2);
		PlayerLib.playSound(playerLocation, Sound.BLOCK_REDSTONE_TORCH_BURNOUT, 0.0f);

		taskController(player);
		return Response.OK;
	}

	private void taskController(Player player) {
		final GameTask oldTask = tasks.get(player);
		if (oldTask != null) {
			return;
		}
		tasks.put(player, GameTask.runTaskTimerTimes((self, tick) -> {
			if (tick == 0 || player.isOnGround()) {
				startCooldown(player);
				tasks.remove(player);
				self.cancel();
			}
		}, 1, 80));
	}

	private void startCooldown(Player player) {
		player.setCooldown(this.getItem().getType(), 8 * 20);
	}

}
