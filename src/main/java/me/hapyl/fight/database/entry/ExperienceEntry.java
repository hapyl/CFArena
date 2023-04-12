package me.hapyl.fight.database.entry;

import me.hapyl.fight.Main;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.PlayerDatabaseEntry;
import me.hapyl.fight.game.experience.Experience;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.math.Numbers;
import org.bson.Document;

public class ExperienceEntry extends PlayerDatabaseEntry {
    public ExperienceEntry(PlayerDatabase playerDatabase) {
        super(playerDatabase);
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

        public String pathLegacy() {
            return "exp." + name().toLowerCase();
        }

    }

    public void reset(Type type) {
        this.set(type, type.getMinValue());
    }

    public Document getExperience() {
        return getConfig().get("experience", new Document());
    }

    public long get(Type type) {
        return getExperience().get(type.name(), type.getMinValue());
    }

    // Super
    public void set(Type type, long value) {
        final Document experience = getExperience();
        experience.put(type.name(), Numbers.clamp(value, type.getMinValue(), type.getMaxValue()));

        getConfig().put("experience", experience);

        // Update experience
        updateExperience();
    }

    public void updateExperience() {
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
