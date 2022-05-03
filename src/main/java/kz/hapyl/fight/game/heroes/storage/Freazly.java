package kz.hapyl.fight.game.heroes.storage;

import kz.hapyl.fight.game.heroes.ClassEquipment;
import kz.hapyl.fight.game.heroes.Hero;
import kz.hapyl.fight.game.heroes.storage.extra.BarrierWall;
import kz.hapyl.fight.game.talents.Talent;
import kz.hapyl.fight.game.talents.Talents;
import kz.hapyl.fight.game.talents.UltimateTalent;
import kz.hapyl.fight.game.weapons.PackedParticle;
import kz.hapyl.fight.game.weapons.RangeWeapon;
import kz.hapyl.fight.util.Direction;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

public class Freazly extends Hero {

	private final int wallBuildDelay = 4;
	private final int wallDecayTime = 15;

	private final Map<Player, BarrierWall> barrierWallMap = new HashMap<>();

	public Freazly() {
		super("Freazly");
		this.setInfo("A great warrior from the Frozen Castle is here with deadly freezing attacks!");
		this.setItem(Material.ICE);

		final ClassEquipment equipment = this.getEquipment();
		equipment.setHelmet(
				"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2FkNzQ4NmI1ZDIwODIzZDVjMjRjYmExODUwYTYwMGE3NzQ0MjA5ODk5ODI4YjE5Y2NmOTNmNjlmMjE4NzA1OCJ9fX0="
		);
		equipment.setChestplate(Color.AQUA);
		equipment.setLeggings(Color.OLIVE);
		equipment.setBoots(Color.AQUA);

		this.setWeapon(new RangeWeapon(Material.IRON_SHOVEL, "snow_weapon") {
			@Override
			public void onHit(LivingEntity entity) {
				entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10, 1));
			}

			@Override
			public void onMove(Location location) {

			}

			@Override
			public boolean predicateBlock(Block block) {
				final Material type = block.getType();
				if (type == Material.ICE || type == Material.FROSTED_ICE) {
					return true;
				}
				return super.predicateBlock(block);
			}

		}.setCooldown(25)
				.setSound(Sound.BLOCK_GLASS_BREAK, 1.5f)
				.setParticleTick(new PackedParticle(Particle.SNOWBALL, 1, 0.0d, 0.0d, 0.0d, 0.0f))
				.setParticleHit(new PackedParticle(Particle.SNOWFLAKE, 5, 0.0d, 0.0d, 0.0d, 0.05f))
				.setName("Snowball Cannon")
				.setInfo("Shoots a beam of freezing energy, damage and slowing enemies upon hit. Pierce through ice blocks.")
				.setDamage(7.5d));


		// Get a barrier builder block. Place to build a 5x3 Ice Wall that decay after &b" + WALL_DECAY_TIME + "s&7. The wall blocks vision and grants positive effect if near it. Also, &eSnowball Cannon &7can shoot thought this wall!
		this.setUltimate(new UltimateTalent(
				"Ice Barrier",
				"Summons an &bIce Barrier &7at your target block that decay over {duration}. The barrier blocks enemies line of sight and grants positive effects to you when nearby.",
				60
		).setItem(Material.PACKED_ICE).setDuration(getUltimateDuration()));

	}


	@Override
	public boolean predicateUltimate(Player player) {
		return getBuildLocation(player) != null;
	}

	@Override
	public String predicateMessage() {
		return "No valid block in sight!";
	}

	private Location getBuildLocation(Player player) {
		final Block target = player.getTargetBlockExact(5);
		if (target == null) {
			return null;
		}
		final Block up = target.getRelative(BlockFace.UP);
		if (!up.getType().isAir()) {
			return null;
		}
		return up.getLocation();
	}

	@Override
	public void useUltimate(Player player) {
		final Location location = getBuildLocation(player);

		// predicate
		if (location == null) {
			return;
		}

		// check the direction player is looking
		final Direction direction = Direction.getDirection(player);

		// remove previous wall
		final BarrierWall oldWall = barrierWallMap.get(player);
		if (oldWall != null) {
			oldWall.destroy();
			barrierWallMap.remove(player);
		}

		final boolean bool = direction.isEastWest();
		Location startLocation = location.subtract((bool ? 0 : 2), 0, (bool ? 2 : 0));

		final BarrierWall wall = new BarrierWall(player, location.clone());
		barrierWallMap.put(player, wall);

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 5; j++) {
				final Block blockToChange = startLocation.add(bool ? 0 : j, i, bool ? j : 0).getBlock();
				if (blockToChange.getType().isAir()) {
					wall.add(blockToChange);
				}
				startLocation.subtract(bool ? 0 : j, i, bool ? j : 0);
			}
		}

		wall.buildSmooth(wallBuildDelay, wallDecayTime);
	}

	public Map<Player, BarrierWall> getBarrierWallMap() {
		return barrierWallMap;
	}

	@Override
	public Talent getFirstTalent() {
		return Talents.ICE_CONE.getTalent();
	}

	@Override
	public Talent getSecondTalent() {
		return null;
	}

	@Override
	public Talent getPassiveTalent() {
		return null;
	}
}
