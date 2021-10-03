package kz.hapyl.fight.game.weapons;

import kz.hapyl.fight.game.GamePlayer;
import kz.hapyl.fight.util.Nulls;
import kz.hapyl.fight.util.Utils;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public abstract class RangeWeapon extends Weapon {

	private int cooldown = 10;
	private double shift = 0.5d;
	private double maxDistance = 40d;

	private PackedParticle particleTick;
	private PackedParticle particleHit;

	private Sound sound;
	private float pitch;

	public RangeWeapon(Material material, String id) {
		super(material);
		this.setId(id);
	}

	public RangeWeapon setMaxDistance(double d) {
		this.maxDistance = d;
		return this;
	}

	public RangeWeapon setShift(double d) {
		this.shift = d;
		return this;
	}

	public RangeWeapon setParticleHit(PackedParticle particleHit) {
		this.particleHit = particleHit;
		return this;
	}

	public RangeWeapon setParticleTick(PackedParticle particleTick) {
		this.particleTick = particleTick;
		return this;
	}

	public RangeWeapon setCooldown(int cd) {
		this.cooldown = cd;
		return this;
	}

	public RangeWeapon setCooldownSec(int cd) {
		return setCooldown(cd * 20);
	}

	public int getCooldown() {
		return cooldown;
	}

	public abstract void onHit(LivingEntity entity);

	public abstract void onMove(Location location);

	public boolean predicateBlock(Block block) {
		return !block.getType().isOccluding();
	}

	public boolean predicateEntity(LivingEntity entity) {
		return true;
	}

	public int getCooldown(Player player) {
		if (this.getItem() == null) {
			return 0;
		}
		return player.getCooldown(this.getItem().getType());
	}

	public void startCooldown(Player player) {
		if (this.getItem() == null) {
			return;
		}
		player.setCooldown(this.getItem().getType(), cooldown);
	}

	public boolean hasCooldown(Player player) {
		return getCooldown(player) > 0;
	}

	public RangeWeapon setSound(Sound sound, float pitch) {
		this.sound = sound;
		this.pitch = pitch;
		return this;
	}

	@Override
	public final void onRightClick(Player player, ItemStack item) {
		if (hasCooldown(player)) {
			return;
		}

		Nulls.runIfNotNull(sound, s -> {
			PlayerLib.playSound(player.getLocation(), s, pitch);
		});

		final Location location = player.getLocation().add(0, 1.5, 0);
		final Vector vector = location.getDirection().normalize();

		startCooldown(player);

		for (double i = 0; i < maxDistance; i += shift) {
			final double x = vector.getX() * i;
			final double y = vector.getY() * i;
			final double z = vector.getZ() * i;
			location.add(x, y, z);

			// check for block predicate
			if (!predicateBlock(location.getBlock())) {
				Nulls.runIfNotNull(particleHit, p -> {
					p.display(location);
				});
				break;
			}

			for (final LivingEntity target : Utils.getEntitiesInRange(location, 0.5d)) {
				if (target == player || !predicateEntity(target)) {
					continue;
				}
				onHit(target);
				GamePlayer.damageEntity(target, this.getDamage(), player);
				Nulls.runIfNotNull(particleHit, p -> {
					p.display(location);
				});
				return;
			}

			if (i > 1.0) {
				Nulls.runIfNotNull(particleTick, p -> {
					p.display(location);
				});
				onMove(location);
			}
			location.subtract(x, y, z);
		}
	}

}
