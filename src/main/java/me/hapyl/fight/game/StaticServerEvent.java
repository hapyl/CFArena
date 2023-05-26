package me.hapyl.fight.game;

import java.time.LocalDate;
import java.time.Month;

// Represents a utility for server events, such as April Fools and Anniversary etc
public class StaticServerEvent {

    public static boolean isAprilFools() {
        final LocalDate date = LocalDate.now();

        final Month month = date.getMonth();
        final int dayOfMonth = date.getDayOfMonth();

        return month == Month.APRIL && dayOfMonth <= 7;
    }

}
