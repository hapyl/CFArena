package me.hapyl.fight.game.talents.storage.extra;

import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Nulls;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.reflect.glow.Glowing;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nullable;

public class CyberCage {

	private final Player player;
	private LivingEntity entity;

	public CyberCage(Player player) {
		this.player = player;
		final Location location = player.getEyeLocation();

		final Item item = player.getWorld().dropItem(location.add(location.getDirection()), new ItemStack(Material.IRON_TRAPDOOR));
		item.setPickupDelay(99999);
		item.setTicksLived(5400); // 30s max life time
		item.setThrower(player.getUniqueId());
		item.setVelocity(player.getEyeLocation().getDirection().multiply(0.5d));

		new GameTask() {
			@Override
			public void run() {
				if (item.isOnGround() || item.isDead()) {
					createEntity(item.getLocation());
					item.remove();
					this.cancel();
				}
			}
		}.addCancelEvent(item::remove).runTaskTimer(0, 1);

	}

	public void activate(Player victim) {
		final LivingEntity marker = getEntity();
		final Player player = getPlayer();
		if (marker == null) {
			return;
		}

		if (player.isOnline()) {
			Chat.sendTitle(player, "&aCage Triggered!", "&7You caught %s".formatted(victim.getName()), 10, 20, 10);
		}

		GamePlayer.damageEntity(victim, 5.0d, player);
		GamePlayer.getPlayer(victim).addEffect(GameEffectType.VULNERABLE, 80);
		victim.setVelocity(marker.getLocation().toVector().subtract(victim.getLocation().toVector()).normalize());

		// Glowing
		new Glowing(victim, ChatColor.AQUA, 80).addViewer(player);

		// Zoom Fx
		PlayerLib.addEffect(victim, PotionEffectType.SLOW, 20, 300);
		PlayerLib.addEffect(victim, PotionEffectType.SLOW, 80, 1);
		PlayerLib.addEffect(victim, PotionEffectType.BLINDNESS, 30, 1);

		// Fx
		PlayerLib.playSound(victim, Sound.ENTITY_ENDERMAN_HURT, 0.8f);
	}

	private void createEntity(Location location) {
		final ItemStack maskedItem = getMaskedItem(location);
		entity = Entities.ARMOR_STAND.spawn(location.subtract(0.0d, 0.95d, 0.0d), me -> {
			me.setSmall(true);
			me.setMarker(true);
			me.setInvisible(true);
			Nulls.runIfNotNull(me.getEquipment(), eq -> eq.setHelmet(maskedItem));
		});

		// Fx
		PlayerLib.playSound(player, Sound.BLOCK_IRON_TRAPDOOR_OPEN, 1.2f);
	}

	private ItemStack getMaskedItem(Location location) {
		final Material type = location.getBlock().getRelative(BlockFace.DOWN).getType();
		return new ItemStack(type.isAir() ? Material.IRON_TRAPDOOR : type);
	}

	public void remove() {
		if (entity == null) {
			return;
		}

		entity.remove();
	}

	public Player getPlayer() {
		return player;
	}

	public boolean isOwner(Player player) {
		return player != null && getPlayer() == player;
	}

	public void drawParticle() {
		if (entity == null) {
			return;
		}

		PlayerLib.spawnParticle(entity.getLocation().add(0.0d, 1.0d, 0.0d), Particle.SNOWBALL, 1, 0, 0, 0, 0.01f);
	}

	@Nullable
	public LivingEntity getEntity() {
		return entity;
	}

	public boolean compareDistance(Location location, double distance) {
		return entity != null && entity.getLocation().distance(location) <= distance;
	}
}
