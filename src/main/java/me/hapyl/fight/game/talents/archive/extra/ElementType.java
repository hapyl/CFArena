package me.hapyl.fight.game.talents.archive.extra;

import me.hapyl.fight.game.talents.InsteadOfNull;
import me.hapyl.spigotutils.module.util.Action;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Predicate;

public enum ElementType {

    HEAVY(
            15.0d,
            30,
            material -> checkName(material, "stone", "cobblestone", "terracotta", "quartz", "sandstone"),
            entity -> addEffect(entity, PotionEffectType.SLOW, 20, 1)
    ),

    STURDY(
            6.0d,
            15,
            material -> checkName(material, "wood", "log", "planks", "dirt", "grass"),
            entity -> addEffect(entity, PotionEffectType.BLINDNESS, 10, 1)
    ),

    SOFT(
            3.0d,
            10,
            material -> checkName(material, "wool", "carpet", "bed", "sand", "gravel", "clay"),
            entity -> addEffect(entity, PotionEffectType.LEVITATION, 10, 1)
    ),

    NULL(-1, 0, material -> false, null);

    private final double damage;
    private final int cd;
    private final Action<LivingEntity> effect;
    private final Predicate<Material> predicate;

    ElementType(double damage, int cd, Predicate<Material> predicate, Action<LivingEntity> effect) {
        this.damage = damage;
        this.cd = cd;
        this.effect = effect;
        this.predicate = predicate;
    }

    public int getCd() {
        return cd;
    }

    public double getDamage() {
        return damage;
    }

    @Nullable
    public Action<LivingEntity> getEffect() {
        return effect;
    }

    private static boolean checkName(Material material, String... names) {
        if (material == null || !material.isBlock() || material.isAir()) {
            return false;
        }

        final String name = material.name().toLowerCase();

        for (String str : names) {
            if (name.contains(str.toLowerCase())) {
                return true;
            }
        }

        return false;
    }

    @Nonnull
    @InsteadOfNull("NULL")
    public static ElementType getElement(Material material) {
        if (!material.isBlock()) {
            throw new IllegalArgumentException("material is not a block!");
        }

        // don't allow wall blocks except player head
        if ((material.name().contains("WALL") && material != Material.PLAYER_HEAD)
                || material.isAir()
                || !material.isSolid()) {
            return NULL;
        }

        // Check for predicate a
        for (ElementType value : values()) {
            if (value.predicate.test(material)) {
                return value;
            }
        }

        final float hardness = material.getHardness();

        if (hardness <= 0.0f) {
            return NULL;
        }

        else if (hardness <= 1.4f) {
            return SOFT;
        }

        else if (hardness <= 2.0f) {
            return STURDY;
        }

        // 100 is water and lava
        else if (hardness < 100.0f) {
            return HEAVY;
        }

        // If not predicated then check blast resistance
        final float magicNumber = material.getBlastResistance();
        if (magicNumber >= 5.0d && magicNumber < 10.0d) {
            return HEAVY;
        }

        if (magicNumber <= 3.0d && magicNumber > 1.0d) {
            return STURDY;
        }

        if (magicNumber > 0.0d && magicNumber <= 1.0d) {
            return SOFT;
        }

        return NULL;
    }

    private static void addEffect(LivingEntity entity, PotionEffectType type, int duration, int multiplier) {
        entity.addPotionEffect(new PotionEffect(type, duration, multiplier));
    }

}
