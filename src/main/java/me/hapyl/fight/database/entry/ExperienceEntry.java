package me.hapyl.fight.database.entry;

import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.registry.KeyedEnum;
import me.hapyl.fight.CF;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.PlayerDatabaseEntry;
import me.hapyl.fight.game.experience.Experience;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class ExperienceEntry extends PlayerDatabaseEntry {
    public ExperienceEntry(@Nonnull PlayerDatabase playerDatabase) {
        super(playerDatabase, "experience");
    }

    public void reset(@Nonnull Type type) {
        set(type, type.getMinValue());
    }

    public long get(@Nonnull Type type) {
        return getValue(type.getKeyAsString(), type.getMinValue());
    }

    public void set(@Nonnull Type type, long value) {
        setValue(type.getKeyAsString(), Math.clamp(value, type.getMinValue(), type.getMaxValue()));

        // Only update if exp changed
        final Experience experience = CF.getPlugin().getExperience();
        final Player player = getOnlinePlayer();

        if (player != null) {
            type.onSet(player, value);

            if (type == Type.EXP && experience.canLevelUp(player)) {
                experience.levelUp(player, false);
            }
        }
    }

    public void remove(@Nonnull Type type, long value) {
        add(type, -value);
    }

    public void add(@Nonnull Type type, long value) {
        set(type, get(type) + value);
    }

    public void update() {
        final Player player = getOnlinePlayer();

        if (player != null) {
            CF.getPlugin().getExperience().triggerUpdate(player);
        }
    }

    public enum Type implements KeyedEnum {

        EXP("Total amount of experience.", 0),
        LEVEL("Current level.", 1, 50) {
            @Override
            public void onSet(@Nonnull Player player, long value) {
                final Experience experience = CF.getPlugin().getExperience();
                final long expRequired = experience.getExpRequired(value);
                final long exp = experience.getExp(player);

                if (exp < expRequired) {
                    CF.getDatabase(player).experienceEntry.set(EXP, expRequired);
                }
            }
        },
        POINT("Points unspent.", 1);

        private final String description;
        private final long minValue;
        private final long maxValue;

        Type(@Nonnull String name, long defaultValue, long maxValue) {
            this.description = name;
            this.minValue = defaultValue;
            this.maxValue = maxValue;
        }

        Type(@Nonnull String name, long defaultValue) {
            this(name, defaultValue, Long.MAX_VALUE);
        }

        public long getMinValue() {
            return minValue;
        }

        @Nonnull
        public String getDescription() {
            return description;
        }

        @Nonnull
        public String getName() {
            return Chat.capitalize(this);
        }

        public long getMaxValue() {
            return maxValue;
        }

        public void onSet(@Nonnull Player player, long value) {
        }

    }


}
