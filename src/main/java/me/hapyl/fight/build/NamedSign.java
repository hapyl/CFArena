package me.hapyl.fight.build;

import org.bukkit.block.Sign;

import javax.annotation.Nullable;

public enum NamedSign {

    SPAWN("spawn"),
    HEALTH("health"),
    CHARGE("charge"),
    ;

    private final String string;

    NamedSign(String string) {
        this.string = "[" + string + "]";
    }

    public static boolean check(Sign sign) {
        return fromSign(sign) != null;
    }

    @Nullable
    public static NamedSign fromSign(Sign sign) {
        final String line = sign.getLine(0);

        for (NamedSign value : values()) {
            if (line.equalsIgnoreCase(value.string)) {
                return value;
            }
        }

        return null;
    }
}
