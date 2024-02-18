package me.hapyl.fight.event.custom;

import me.hapyl.fight.event.CancellableWithReason;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.archive.techie.Talent;
import me.hapyl.fight.game.weapons.Weapon;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

/**
 * Called before a {@link Talent} or a {@link Weapon} ability is executed.
 */
public class PlayerPreconditionEvent extends GamePlayerEvent implements CancellableWithReason {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private String reason;
    private boolean cancel;

    public PlayerPreconditionEvent(@Nonnull GamePlayer gamePlayer) {
        super(gamePlayer);

        this.reason = "Not allowed.";
    }

    @Nonnull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    @Nonnull
    @Override
    public String getReason() {
        return reason;
    }

    @Override
    public void setReason(@Nonnull String reason) {
        this.reason = reason;
    }

    @Nonnull
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

}
