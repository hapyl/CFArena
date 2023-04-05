package me.hapyl.fight.game;

import java.time.LocalDate;
import java.time.Month;

// TODO (hapyl): 005, Apr 5, 2023:
public class ServerEvent {

    public static boolean isAprilFools() {
        final LocalDate date = LocalDate.now();

        final Month month = date.getMonth();
        final int dayOfMonth = date.getDayOfMonth();

        return month == Month.APRIL && dayOfMonth <= 7;
    }

}
