package me.hapyl.fight.game.talents.storage.moonwalker;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Nulls;
import me.hapyl.fight.util.Utils;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class MoonSliteBomb extends Talent implements Listener {

	private final int bombLimit = 3;
	private final Map<UUID, Set<Item>> bombs = new HashMap<>();

	public MoonSliteBomb() {
		super(
				"Moonslite Bomb",
				"Drop a proximity grenade at your current location that explodes on contact with enemy or after a set period, dealing damage and applying &6&lCorrosion &7for a short time.",
				Type.COMBAT
		);
		this.setItem(Material.END_STONE_BRICK_SLAB);
		this.setCdSec(10);
	}

	@Override
	public void onDeath(Player player) {
		final UUID uniqueId = player.getUniqueId();
		Nulls.runIfNotNull(bombs.get(uniqueId), set -> {
			if (set.isEmpty()) {
				return;
			}
			set.forEach(Entity::remove);
		});
		bombs.remove(uniqueId);
	}

	@Override
	public void onStop() {
		bombs.values().forEach(items -> items.forEach(Item::remove));
		bombs.clear();
	}

	@Override
	public Response execute(Player player) {
		final Set<Item> playerBombs = getBombs(player);
		if (playerBombs.size() >= bombLimit) {
			return Response.error("Limit reached!");
		}

		final Item item = player.getWorld().dropItem(player.getLocation(), new ItemStack(this.getItem().getType()));
		item.setPickupDelay(20);
		item.setTicksLived(5600);
		item.setOwner(player.getUniqueId());
		playerBombs.add(item);

		// Fx
		new GameTask() {
			@Override
			public void run() {
				if (item.isDead()) {
					this.cancel();
					return;
				}

				final Location location = item.getLocation().clone().add(0.0d, 0.15d, 0.0d);
				PlayerLib.spawnParticle(location, Particle.END_ROD, 1, 0.1d, 0.0d, 0.1d, 0.01f);
			}
		}.runTaskTimer(0, 5);

		return Response.OK;
	}

	public int getBombsSize(Player player) {
		return getBombs(player).size();
	}

	private Set<Item> getBombs(Player player) {
		return getBombs(player.getUniqueId());
	}

	private Set<Item> getBombs(UUID uuid) {
		return bombs.computeIfAbsent(uuid, k -> new HashSet<>());
	}

	@EventHandler()
	public void handleEntityPickupItemEvent(EntityPickupItemEvent ev) {
		final Item item = ev.getItem();
		if (isBombItem(item)) {
			ev.setCancelled(true);
			if (!Utils.compare(item.getOwner(), ev.getEntity().getUniqueId())) {
				explode(item);
			}
		}
	}

	@EventHandler()
	public void handleItemDespawnEvent(ItemDespawnEvent ev) {
		final Item item = ev.getEntity();
		if (isBombItem(item)) {
			explode(item);
		}
	}

	private void explode(Item item) {
		final UUID owner = item.getOwner();
		if (owner != null) {
			getBombs(owner).remove(item);
		}

		Utils.createExplosion(item.getLocation(), 2.5, 5.0d, this::applyCorrosion);
		item.remove();
	}
	// yes this is temporary, effects are not yet implemented

	private void applyCorrosion(LivingEntity entity) {
		int duration = 35;
		entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, duration, 4));
		entity.addPotionEffect(new PotionEffect(PotionEffectType.POISON, duration, 4));
		entity.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, duration, 4));
	}

	private boolean isBombItem(Item item) {
		for (final Set<Item> value : bombs.values()) {
			for (final Item item1 : value) {
				if (item1.equals(item)) {
					return true;
				}
			}
		}
		return false;
	}
}
