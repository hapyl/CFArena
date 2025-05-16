package me.hapyl.fight.event.custom;

import me.hapyl.fight.event.CancellableWithReason;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.heroes.ultimate.UltimateTalent;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.game.weapons.ability.Ability;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

/**
 * Called before a {@link Talent}, {@link UltimateTalent} or a {@link Weapon} {@link Ability} is about to be executed.
 */
public class TalentPreconditionEvent extends GamePlayerEvent implements CancellableWithReason {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private String reason;
    private boolean cancel;

    public TalentPreconditionEvent(@Nonnull GamePlayer gamePlayer) {
        super(gamePlayer);

        this.reason = "Not allowed.";
    }

    public static boolean call(@Nonnull GamePlayer player) {
        return new TalentPreconditionEvent(player).callEvent();
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
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    @Nonnull
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

}
