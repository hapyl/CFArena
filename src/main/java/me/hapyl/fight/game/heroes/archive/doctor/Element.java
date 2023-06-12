package me.hapyl.fight.game.heroes.archive.doctor;

import com.google.common.collect.Sets;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Set;

public class Element {

    private double damage;
    private int cd;
    private final Set<Material> materials;

    public Element() {
        this.damage = 0;
        this.cd = 0;
        this.materials = Sets.newHashSet();
    }

    public Element setMaterials(@Nonnull Material... materials) {
        this.materials.addAll(Arrays.asList(materials));
        return this;
    }

    public Element setDamage(double damage) {
        this.damage = damage;
        return this;
    }

    public Element setCd(int cd) {
        this.cd = cd;
        return this;
    }

    public double getDamage() {
        return damage;
    }

    public int getCd() {
        return cd;
    }

    public boolean isApplicable(@Nonnull Material material) {
        return materials.contains(material);
    }

    public void onHit(@Nonnull LivingEntity entity, @Nonnull Material material) {
    }
}
