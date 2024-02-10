package me.hapyl.fight.game.heroes.archive.swooper;

import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.weapons.PackedParticle;
import me.hapyl.fight.game.weapons.range.RangeWeapon;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SwooperWeapon extends RangeWeapon {

    private final Swooper swooper;
    private final double scopeMultiplier = 2.0d;

    public SwooperWeapon(Swooper swooper) {
        super(Material.WOODEN_HOE, "swooper_weapon");

        setName("Sniper Rifle");

        setDescription("""
                Slow firing sniper rifle.
                &8&o;;Looks like a gift from someone important.
                                
                &eAbility: Scope %s&lSNEAK
                Activates a sniper score, increase the damage and max distance of the rifle.
                """.formatted(Color.BUTTON));

        this.swooper = swooper;

        setDamage(5.0d);
        setMaxAmmo(5);
        setMaxDistance(20);
        setCooldown(45);

        setParticleTick(new PackedParticle(Particle.FIREWORKS_SPARK));
        setSound(Sound.ENTITY_GENERIC_EXPLODE, 1.5f);
    }

    @Override
    public void onShoot(@Nonnull GamePlayer player) {
        final SwooperData data = swooper.getPlayerData(player);

        if (data.ultimateShots <= 0) {
            return;
        }

        data.ultimateShots--;

        if (data.ultimateShots <= 0) {
            swooper.setUsingUltimate(player, false);
        }
    }

    @Nullable
    @Override
    public EnumDamageCause getDamageCause(@Nonnull GamePlayer player) {
        return EnumDamageCause.RIFLE;
    }

    @Override
    public boolean predicateBlock(@Nonnull GamePlayer player, @Nonnull Block block) {
        final Hero hero = player.getHero();

        return hero.isUsingUltimate(player) || super.predicateBlock(player, block);
    }

    @Override
    public double getDamage(@Nonnull GamePlayer player, boolean isHeadShot) {
        final SwooperData data = swooper.getPlayerData(player);

        double damage = getDamage();

        if (player.isSneaking()) {
            damage *= scopeMultiplier;
        }

        if (data.isStealthMode()) {
            damage *= swooper.getPassiveTalent().stealthDamageMultiplier;
        }

        return damage;
    }

    @Override
    public double getMaxDistance(@Nonnull GamePlayer player) {
        final double maxDistance = getMaxDistance();

        return player.isSneaking() ? maxDistance * scopeMultiplier : maxDistance;
    }
}
