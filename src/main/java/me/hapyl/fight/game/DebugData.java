package me.hapyl.fight.game;

import com.google.common.collect.Sets;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class DebugData {

    public static final DebugData EMPTY = new DebugData() {
        @Override
        public boolean is(@Nonnull Flag flag) {
            return false;
        }
    };

    public static final DebugData FORCE = new DebugData() {
        @Override
        public boolean is(@Nonnull Flag flag) {
            return flag == Flag.FORCE;
        }
    };

    private final Set<Flag> flags;

    public DebugData() {
        this.flags = Sets.newHashSet();
    }

    public boolean none() {
        return flags.isEmpty();
    }

    public boolean any() {
        return !none();
    }

    public boolean or(@Nonnull Flag... flags) {
        for (Flag flag : flags) {
            if (is(flag)) {
                return true;
            }
        }

        return false;
    }

    public boolean is(@Nonnull Flag flag) {
        return this.flags.contains(flag);
    }

    public boolean nor(@Nonnull Flag... flags) {
        for (Flag flag : flags) {
            if (is(flag)) {
                return false;
            }
        }

        return true;
    }

    public boolean not(@Nonnull Flag flag) {
        return !is(flag);
    }

    @Nonnull
    public String list() {
        if (none()) {
            return "";
        }

        final StringBuilder builder = new StringBuilder();

        for (Flag value : Flag.values()) {
            if (is(value)) {
                if (!builder.isEmpty()) {
                    builder.append(", ");
                }

                builder.append(value);
            }
        }

        return builder.toString();
    }

    @Nonnull
    public static DebugData parse(@Nullable String[] strings) {
        final DebugData debugData = new DebugData();

        if (strings == null) {
            return debugData;
        }

        for (String s : strings) {
            final Flag flag = Flag.byCharacter(s);
            if (flag != null) {
                debugData.flags.add(flag);
            }
        }

        return debugData;
    }

    public enum Flag {
        DEBUG("d"),
        FORCE("f"),
        IGNORE_COOLDOWN("c") {
            @Override
            public String toString() {
                return "IGNORE COOLDOWN";
            }
        };

        private final String flag;

        Flag(String flag) {
            this.flag = flag;
        }

        @Nullable
        public static Flag byCharacter(@Nonnull String character) {
            character = character.replace("-", "").trim();

            for (Flag value : values()) {
                if (value.flag.equals(character)) {
                    return value;
                }
            }

            return null;
        }
    }

}
