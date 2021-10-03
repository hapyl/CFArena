package kz.hapyl.fight.game.talents.storage.extra;

import kz.hapyl.spigotutils.module.annotate.NULLABLE;
import kz.hapyl.spigotutils.module.util.Action;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public enum ElementType {

	// nothing or idk slow
	STONE(15.0d, 30, entity -> addEffect(entity, PotionEffectType.SLOW, 20, 1)),

	// idk yet
	WOOD(6.0d, 15, entity -> addEffect(entity, PotionEffectType.BLINDNESS, 10, 1)),

	// flights up
	WOOL(3.0d, 10, entity -> addEffect(entity, PotionEffectType.LEVITATION, 10, 1)),

	NULL(-1, 0, null);

	private final double damage;
	private final int cd;
	private final Action<LivingEntity> effect;

	ElementType(double damage, int cd, Action<LivingEntity> effect) {
		this.damage = damage;
		this.cd = cd;
		this.effect = effect;
	}

	public int getCd() {
		return cd;
	}

	public double getDamage() {
		return damage;
	}

	@NULLABLE
	public Action<LivingEntity> getEffect() {
		return effect;
	}

	public static ElementType getElementOf(Material material) {
		if (!material.isBlock()) {
			throw new IllegalArgumentException("material is not a block!");
		}

		// don't allow wall blocks except player head
		if (material.name().contains("WALL") && material != Material.PLAYER_HEAD) {
			return NULL;
		}

		final float magicNumber = material.getBlastResistance();
		if (magicNumber >= 5.0d && magicNumber < 10.0d) {
			return STONE;
		}

		if (magicNumber <= 3.0d && magicNumber > 1.0d) {
			return WOOD;
		}

		if (magicNumber > 0.0d && magicNumber <= 1.0d) {
			return WOOL;
		}

		return NULL;
	}

	private static void addEffect(LivingEntity entity, PotionEffectType type, int duration, int multiplier) {
		entity.addPotionEffect(new PotionEffect(type, duration, multiplier));
	}

}
