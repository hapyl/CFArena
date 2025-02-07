package me.hapyl.fight.event.custom;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.loadout.HotBarSlot;
import me.hapyl.fight.game.talents.Talent;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

/**
 * Called <b>after</b> a {@link GamePlayer} <b>successfully</b> used a {@link Talent}.
 */
public class TalentUseEvent extends GamePlayerEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Talent talent;

    public TalentUseEvent(@Nonnull GamePlayer player, @Nonnull Talent talent) {
        super(player);

        this.talent = talent;
    }

    /**
     * Gets the talent that was used.
     *
     * @return the talent used.
     */
    @Nonnull
    public Talent getTalent() {
        return talent;
    }

    /**
     * Gets the {@link HotBarSlot} of this talent from the hero.
     *
     * @return the slot of this talent.
     * @throws IllegalArgumentException if player's hero does not have this talent.
     */
    @Nonnull
    public HotBarSlot getSlot() throws IllegalArgumentException {
        return getHero().getTalentSlotByHandle(talent);
    }

    /**
     * Gets the {@link Hero} of the {@link GamePlayer}.
     * <p>
     * This should always return the hero that this talent belongs to, unless talent used illegally.
     *
     * @return the current hero of the player.
     */
    @Nonnull
    public Hero getHero() {
        return player.getHero();
    }

    @Nonnull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    @Nonnull
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
