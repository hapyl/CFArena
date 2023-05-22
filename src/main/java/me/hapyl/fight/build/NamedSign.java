package me.hapyl.fight.build;

import org.bukkit.block.Sign;

public enum NamedSign {

    SPAWN("spawn"),
    HEALTH("health"),
    ENERGY("energy"),
    ;

    private final String string;

    NamedSign(String string) {
        this.string = "[" + string + "]";
    }

    public static boolean check(Sign sign) {
        final String line = sign.getLine(0);

        for (NamedSign value : values()) {
            if (line.equalsIgnoreCase(value.string)) {
                return true;
            }
        }

        return false;
    }
}
