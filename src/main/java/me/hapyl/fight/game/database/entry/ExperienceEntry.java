package me.hapyl.fight.game.database.entry;

import me.hapyl.fight.game.database.Database;
import me.hapyl.fight.game.database.DatabaseEntry;
import me.hapyl.spigotutils.module.math.Numbers;

public class ExperienceEntry extends DatabaseEntry {
    public ExperienceEntry(Database database) {
        super(database);
    }

    public enum Type {

        EXP("Total amount of experience.", 0),
        LEVEL("Current level.", 1, 20),
        POINT("Points unspent.", 1);

        private final String name;
        private final long minValue;
        private final long maxValue;

        Type(String name, long defaultValue, long maxValue) {
            this.name = name;
            this.minValue = defaultValue;
            this.maxValue = maxValue;
        }

        Type(String name, long defaultValue) {
            this(name, defaultValue, Long.MAX_VALUE);
        }

        public long getMinValue() {
            return minValue;
        }

        public String getName() {
            return name;
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

    public void set(Type type, long value) {
        this.getConfig().set(type.path(), Numbers.clamp(value, type.getMinValue(), type.getMaxValue()));
    }

    public void add(Type type, long value) {
        set(type, get(type) + value);
    }


}
