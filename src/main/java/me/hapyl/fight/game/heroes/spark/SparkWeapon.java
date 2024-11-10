package me.hapyl.fight.game.heroes.spark;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.weapons.PackedParticle;
import me.hapyl.fight.game.weapons.range.RangeWeapon;
import me.hapyl.fight.game.weapons.range.WeaponRayCast;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public class SparkWeapon extends RangeWeapon {

    public SparkWeapon() {
        super(Material.STICK, Key.ofString("fire_weapon"));

        setName("Fire Sprayer");
        setDescription("A long range weapon that can shoot fire lasers in front of you! How cool is that...");

        setDamage(8.0d);
        setCooldown(30);

        setParticleHit(new PackedParticle(Particle.LAVA).setAmount(3).setSpeed(0.2f));
        setParticleTick(new PackedParticle(Particle.FLAME).setSpeed(0.001f));

        setSound(Sound.ENTITY_BLAZE_SHOOT, 1.75f);
    }

    @Nonnull
    @Override
    public WeaponRayCast newRayCastInstance(@Nonnull GamePlayer player) {
        return new WeaponRayCast(this, player) {
            @Override
            public void onHit(@Nonnull LivingGameEntity entity, boolean isHeadShot) {
                super.onHit(entity, isHeadShot);

                entity.setFireTicks(10);
            }

            @Nonnull
            @Override
            public EnumDamageCause getDamageCause() {
                return EnumDamageCause.FIRE_SPRAY;
            }
        };
    }

}
