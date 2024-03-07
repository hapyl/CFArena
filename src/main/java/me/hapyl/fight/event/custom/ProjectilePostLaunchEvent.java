package me.hapyl.fight.event.custom;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A better version of {@link org.bukkit.event.entity.ProjectileLaunchEvent}.
 */
public class ProjectilePostLaunchEvent extends GamePlayerEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Projectile projectile;

    public ProjectilePostLaunchEvent(@Nonnull GamePlayer player, @Nonnull Projectile projectile) {
        super(player);
        this.projectile = projectile;
    }

    @Nonnull
    public Projectile getProjectile() {
        return projectile;
    }

    @Nonnull
    public GamePlayer getShooter() {
        return player;
    }

    @Nonnull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    @Nonnull
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

}
