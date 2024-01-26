package me.hapyl.fight.game.heroes.archive.spark;

import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.weapons.PackedParticle;
import me.hapyl.fight.game.weapons.range.RangeWeapon;
import me.hapyl.fight.game.weapons.range.WeaponRaycast;
import me.hapyl.fight.game.weapons.range.WeaponRaycastInstance;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public class SparkWeapon extends RangeWeapon {
    public SparkWeapon() {
        super(Material.STICK, "fire_weapon");

        setName("Fire Sprayer");
        setDescription("A long range weapon that can shoot fire lasers in front of you! How cool is that...");

        setDamage(8.0d);
        setCooldown(30);

        setSound(Sound.ENTITY_BLAZE_SHOOT, 1.75f);
        setParticleHit(new PackedParticle(Particle.LAVA).setAmount(3).setSpeed(0.2f));
        setParticleTick(new PackedParticle(Particle.FLAME).setSpeed(0.001f));

        raycast = new WeaponRaycast(this) {
            @Nonnull
            @Override
            public WeaponRaycastInstance newInstance(@Nonnull GamePlayer player) {
                return new WeaponRaycastInstance(player, SparkWeapon.this) {
                    @Override
                    public void onHit(@Nonnull LivingGameEntity entity, boolean isHeadShot) {
                        super.onHit(entity, isHeadShot);

                        entity.setFireTicks(10);
                    }
                };
            }
        };
    }

    @Nonnull
    @Override
    public EnumDamageCause getDamageCause(@Nonnull GamePlayer player) {
        return EnumDamageCause.FIRE_SPRAY;
    }

}
