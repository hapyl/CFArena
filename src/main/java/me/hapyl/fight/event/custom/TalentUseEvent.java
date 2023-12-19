package me.hapyl.fight.event.custom;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.archive.techie.Talent;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public class TalentUseEvent extends GamePlayerEvent{

    private static final HandlerList HANDLERS = new HandlerList();

    public final Talent talent;

    public TalentUseEvent(@Nonnull GamePlayer gamePlayer, @Nonnull Talent talent) {
        super(gamePlayer);

        this.talent = talent;
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
