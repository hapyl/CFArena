package me.hapyl.fight.util;

import org.bukkit.event.Event;

import javax.annotation.Nonnull;

/**
 * An interface for <b>single event</b> handling.
 * <p>
 * The implementation is manual for each handler.
 *
 * @see me.hapyl.fight.game.trial.objecitive.TrialObjective
 */
public interface SingleEventHandler {

    <T extends Event> void handle(@Nonnull T ev);

}
