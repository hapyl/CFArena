package me.hapyl.fight.game.weapons.range;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import javax.annotation.OverridingMethodsMustInvokeSuper;

public class WeaponRaycastInstance {

    protected final GamePlayer player;
    protected final WeaponRaycastable raycastable;

    public WeaponRaycastInstance(GamePlayer player, WeaponRaycastable raycastable) {
        this.player = player;
        this.raycastable = raycastable;
    }

    public void onStart() {
    }

    public void onStop() {
    }

    @OverridingMethodsMustInvokeSuper
    public void onMove(@Nonnull Location location) {
    }

    @OverridingMethodsMustInvokeSuper
    public void onHit(@Nonnull LivingGameEntity entity, boolean isHeadShot) {
        entity.damage(raycastable.getDamage(player, isHeadShot), player, raycastable.getDamageCause(player));
    }
}
