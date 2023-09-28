package me.hapyl.fight.infraction;

import javax.annotation.Nonnull;

public interface Infraction {

    @Nonnull
    HexID getID();

    long getTimestamp();

    long getDuration();

}
