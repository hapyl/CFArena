package kz.hapyl.fight.game.talents.storage.extra;

import kz.hapyl.fight.game.task.GameTask;
import kz.hapyl.fight.game.weapons.Weapon;
import kz.hapyl.fight.util.Utils;
import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

public class PhysGun extends Weapon {

	private final Map<Player, LivingEntity> capturedEntity = new HashMap<>();

	public PhysGun() {
		super(Material.GOLDEN_HORSE_ARMOR);
		this.setId("dr_ed_gun_2");
		this.setName("Upgraded Dr. Ed's Gravity Energy Capacitor Mk. 4");
		GameTask.scheduleCancelTask(capturedEntity::clear);
	}

	@Override
	public void onRightClick(Player player, ItemStack item) {

		// Throw
		if (capturedEntity.containsKey(player)) {
			final LivingEntity entity = capturedEntity.get(player);
			final Location location = player.getLocation().add(player.getLocation().getDirection().multiply(2.0d));

			capturedEntity.remove(player);

			entity.setVelocity(player.getLocation().getDirection().multiply(2.5d));
			PlayerLib.spawnParticle(location, Particle.EXPLOSION_NORMAL, 10, 0.2, 0.05, 0.2, 0.02f);
			PlayerLib.playSound(location, Sound.ITEM_CROSSBOW_SHOOT, 0.5f);
			return;
		}

		// Get the target entity
		final LivingEntity target = Utils.getTargetEntity(player, 3.0d, e -> e != player);

		if (target == null) {
			Chat.sendMessage(player, "&cNo valid target!");
			return;
		}

		capturedEntity.put(player, target);

		new GameTask() {
			@Override
			public void run() {

				if (player.getInventory().getHeldItemSlot() != 4 || (capturedEntity.get(player) == null) || (capturedEntity.get(player) != target)) {
					dismountEntity(player, target);
					this.cancel();
					return;
				}

				final Location playerLocation = player.getLocation();
				final Location location = target.getLocation();
				Location finalLocation = playerLocation.add(0.0d, 1.0d, 0.0d).add(playerLocation.getDirection().multiply(2.0d));

				finalLocation.setYaw(location.getYaw());
				finalLocation.setPitch(location.getPitch());

				if (!finalLocation.getBlock().getType().isAir() || !finalLocation.getBlock().getRelative(BlockFace.UP).getType().isAir()) {
					finalLocation = playerLocation;
				}

				target.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20, 1, true));
				if (target instanceof Player targetPlayer) {
					Chat.sendActionbar(targetPlayer, "&f&lCaptured by &a%s&f&l!", player.getName());
				}

				target.teleport(finalLocation);
				Chat.sendTitle(player, "", "&f&lCarrying &a%s".formatted(target.getName()), 0, 10, 0);

			}
		}.runTaskTimer(0, 1);
	}

	private void dismountEntity(Player player, LivingEntity entity) {
		final Location location = entity.getLocation();
		final Block block = location.getBlock();

		if (!block.getType().isAir() || !block.getRelative(BlockFace.UP).getType().isAir()) {
			Chat.sendMessage(player, "&a%s was teleported to your since they would suffocate.", entity.getName());
			entity.teleport(player);
		}

		boolean solid = false;
		// check for solid ground
		for (double y = 0; y <= location.getY(); ++y) {
			if (!location.clone().subtract(0.0d, y, 0.0d).getBlock().getType().isAir()) {
				solid = true;
				break;
			}
		}

		if (!solid) {
			Chat.sendMessage(player, "&a%s was teleported to your since they would fall into void.", entity.getName());
			entity.teleport(player);
		}

		capturedEntity.remove(player);
	}

}
