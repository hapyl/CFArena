package me.hapyl.fight.game.database.entry;

import me.hapyl.fight.Main;
import me.hapyl.fight.game.database.Database;
import me.hapyl.fight.game.database.DatabaseEntry;
import me.hapyl.fight.game.experience.Experience;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.math.Numbers;

public class ExperienceEntry extends DatabaseEntry {
    public ExperienceEntry(Database database) {
        super(database);
    }

    public enum Type {

        EXP("Total amount of experience.", 0),
        LEVEL("Current level.", 1, 20),
        POINT("Points unspent.", 1);

        private final String description;
        private final long minValue;
        private final long maxValue;

        Type(String name, long defaultValue, long maxValue) {
            this.description = name;
            this.minValue = defaultValue;
            this.maxValue = maxValue;
        }

        Type(String name, long defaultValue) {
            this(name, defaultValue, Long.MAX_VALUE);
        }

        public long getMinValue() {
            return minValue;
        }

        public String getDescription() {
            return description;
        }

        public String getName() {
            return Chat.capitalize(this);
        }

        public long getMaxValue() {
            return maxValue;
        }

        public String path() {
            return "exp." + this.name().toLowerCase();
        }

    }

    public void reset(Type type) {
        this.set(type, type.getMinValue());
    }

    public long get(Type type) {
        return this.getConfig().getLong(type.path(), type.minValue);
    }

    // Super
    public void set(Type type, long value) {
        this.getConfig().set(type.path(), Numbers.clamp(value, type.getMinValue(), type.getMaxValue()));

        // Update experience
        final Experience experience = Main.getPlugin().getExperience();

        experience.levelUp(getPlayer(), false);
        experience.triggerUpdate(getPlayer());
    }

    public void remove(Type type, long value) {
        add(type, -value);
    }

    public void add(Type type, long value) {
        set(type, get(type) + value);
    }


}
