package me.hapyl.fight.game.heroes.swooper;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.Vector3;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.weapons.PackedParticle;
import me.hapyl.fight.game.weapons.ability.Ability;
import me.hapyl.fight.game.weapons.ability.AbilityType;
import me.hapyl.fight.game.weapons.range.RangeWeapon;
import me.hapyl.fight.game.weapons.range.WeaponRayCast;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SwooperWeapon extends RangeWeapon {

    private final Swooper swooper;
    private final double scopeMultiplier = 2.0d;

    private final PackedParticle[] particles = {
            new PackedParticle(Particle.FIREWORK),
            new PackedParticle(Particle.FLAME),
            new PackedParticle(Particle.INSTANT_EFFECT).setAmount(0).setOffsetX(1).setOffsetY(1).setOffsetZ(1).setSpeed(1)
    };

    public SwooperWeapon(Swooper swooper) {
        super(Material.WOODEN_HOE, Key.ofString("swooper_weapon"));

        setName("Sniper Rifle");

        setDescription("""
                Slow firing sniper rifle.
                &8&o;;Looks like a gift from someone important.
                
                &eAbility: Scope %s&lSNEAK
                Activates a sniper score, increasing the max bullet distance.
                """.formatted(Color.BUTTON));

        this.swooper = swooper;

        setDamage(8.0d);
        setMaxAmmo(5);
        setMaxDistance(75);
        setCooldown(40);

        setSound(Sound.ENTITY_GENERIC_EXPLODE, 1.5f);

        removeAbility(AbilityType.LEFT_CLICK);
        setAbility(AbilityType.ATTACK, new StunAbility());
    }

    @Nonnull
    @Override
    public WeaponRayCast newRayCastInstance(@Nonnull GamePlayer player) {
        final SwooperData data = swooper.getPlayerData(player);
        final boolean strongShot;

        if (data.ultimateShots > 0) {
            strongShot = true;
            data.ultimateShots--;

            if (data.ultimateShots <= 0) {
                player.setUsingUltimate(false);
                data.remove(); // remove highlighting
            }
        }
        else {
            strongShot = false;
        }

        return new WeaponRayCast(this, player) {

            @Nonnull
            @Override
            public PackedParticle getParticleTick() {
                if (strongShot) {
                    return particles[1];
                }

                return particles[data.isStealthMode() ? 2 : 0];
            }

            @Nonnull
            @Override
            public EnumDamageCause getDamageCause() {
                return EnumDamageCause.RIFLE;
            }

            @Override
            public boolean canPassThrough(@Nonnull Block block, @Nonnull Vector3 vector) {
                return strongShot || super.canPassThrough(block, vector);
            }

            @Override
            public double getDamage(boolean isHeadShot) {
                final SwooperData data = swooper.getPlayerData(player);

                double damage = weapon.getDamage();

                if (data.isStealthMode()) {
                    damage *= swooper.getPassiveTalent().stealthDamageMultiplier;
                }

                if (strongShot) {
                    damage *= swooper.getUltimate().ultimateDamageMultiplier;
                }

                return damage;
            }

            @Override
            public double getMaxDistance() {
                final double maxDistance = weapon.getMaxDistance();

                return player.isSneaking() ? maxDistance * scopeMultiplier : maxDistance;
            }
        };
    }

    private class StunAbility extends Ability {
        public StunAbility() {
            super("Stun", """
                    Hitting an &cenemy&7 with your rifle has a small chance to &estun&7 them.
                    """);
        }

        @Nullable
        @Override
        public Response execute(@Nonnull GamePlayer player, @Nonnull ItemStack item) {
            return Response.OK;
        }
    }
}
