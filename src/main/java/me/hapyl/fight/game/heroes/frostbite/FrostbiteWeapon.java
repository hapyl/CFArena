package me.hapyl.fight.game.heroes.frostbite;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.weapons.range.RangeWeapon;
import me.hapyl.fight.game.weapons.range.WeaponRayCast;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;

import javax.annotation.Nonnull;

public class FrostbiteWeapon extends RangeWeapon {

    @DisplayField private final int slowingAuraDuration = 60;

    public FrostbiteWeapon() {
        super(Material.IRON_SHOVEL, Key.ofString("frostbite_weapon"));

        setName("Snow Shovel");

        setDescription("""
                An ordinary shovel used for shoveling the snow.
                """);

        setDamage(5.0d);
        setCooldown(20);
        setReloadTimeSec(3);
        setMaxAmmo(4);
    }

    @Nonnull
    @Override
    public WeaponRayCast newRayCastInstance(@Nonnull GamePlayer player) {
        final FrostbiteBullet bullet = new FrostbiteBullet(player);

        return new ProjectileRayCast(this, player, 0.7d, 2) {

            @Nonnull
            @Override
            public EnumDamageCause getDamageCause() {
                return EnumDamageCause.FROSTBITE;
            }

            @Override
            public void onMove(@Nonnull Location location) {
                bullet.teleport(location);

                player.spawnWorldParticle(location, Particle.SNOWFLAKE, 1);
                player.spawnWorldParticle(location, Particle.ITEM_SNOWBALL, 1, 0.05d, 0.05d, 0.05d, 0.025f);
            }

            @Override
            public void onHit(@Nonnull LivingGameEntity entity, boolean isHeadShot) {
                super.onHit(entity, isHeadShot);

                entity.addEffect(Effects.SLOWING_AURA, slowingAuraDuration, true);
            }

            @Override
            public void onStop() {
                bullet.remove();
            }

        };
    }
}
