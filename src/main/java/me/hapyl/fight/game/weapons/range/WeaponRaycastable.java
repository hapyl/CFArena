package me.hapyl.fight.game.weapons.range;

import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.weapons.PackedParticle;
import org.bukkit.block.Block;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface WeaponRaycastable {

    double getShift();

    double getMaxDistance();

    default double getMaxDistance(@Nonnull GamePlayer player) {
        return getMaxDistance();
    }

    double getDamage(@Nonnull GamePlayer player, boolean isHeadShot);

    @Nullable
    EnumDamageCause getDamageCause(@Nonnull GamePlayer player);

    default boolean predicateBlock(@Nonnull Block block) {
        return !block.getType().isOccluding();
    }

    default boolean predicateEntity(@Nonnull LivingGameEntity entity) {
        return true;
    }

    @Nullable
    PackedParticle getParticleHit();

    @Nullable
    PackedParticle getParticleTick();

}
