package me.hapyl.fight.util;

public final class Matchers {

    private Matchers() {
    }

    @SafeVarargs
    public static <T> boolean matchesAny(T[] array, T... match) {
        return match(array, match) > 0;
    }

    @SafeVarargs
    public static <T> boolean matchesNone(T[] array, T... match) {
        return match(array, match) == 0;
    }

    public static <T> boolean matchesAtLeast(T[] array, T match, int amount) {
        return match(array, match) >= amount;
    }

    public static <T> boolean matchesAtMost(T[] array, T match, int amount) {
        return match(array, match) < amount;
    }

    public static <T> boolean matchesWithin(T[] array, T match, int min, int max) {
        final int i = match(array, match);
        return i >= min && i < max;
    }

    public static <T> boolean matchesAt(T[] array, T match, int index) {
        if (array == null || match == null) {
            return false;
        }

        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(match) && i == index) {
                return true;
            }
        }

        return false;
    }

    @SafeVarargs
    public static <T> int match(T[] array, T... match) {
        if (array == null || match == null) {
            return 0;
        }

        int matches = 0;

        for (T t : array) {
            for (T m : match) {
                if (t.equals(m)) {
                    matches++;
                }
            }
        }

        return matches;
    }


}
