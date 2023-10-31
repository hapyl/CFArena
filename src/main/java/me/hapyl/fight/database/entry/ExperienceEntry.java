package me.hapyl.fight.database.entry;

import me.hapyl.fight.Main;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.PlayerDatabaseEntry;
import me.hapyl.fight.game.experience.Experience;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.math.Numbers;
import org.bson.Document;
import org.bukkit.entity.Player;

public class ExperienceEntry extends PlayerDatabaseEntry {
    public ExperienceEntry(PlayerDatabase playerDatabase) {
        super(playerDatabase);
    }

    public void reset(Type type) {
        this.set(type, type.getMinValue());
    }

    public Document getExperience() {
        return getDocument().get("experience", new Document());
    }

    public long get(Type type) {
        return getExperience().get(type.name(), type.getMinValue());
    }

    public void set(Type type, long value) {
        final Document document = getExperience();
        document.put(type.name(), Numbers.clamp(value, type.getMinValue(), type.getMaxValue()));

        type.onSet(getPlayer(), value);
        getDocument().put("experience", document);

        // Only update if exp changed
        final Experience experience = Main.getPlugin().getExperience();
        final Player player = getPlayer();

        if (type == Type.EXP && experience.canLevelUp(player)) {
            experience.levelUp(getPlayer(), false);
        }
    }

    public void remove(Type type, long value) {
        add(type, -value);
    }

    public void add(Type type, long value) {
        set(type, get(type) + value);
    }

    public void update() {
        Main.getPlugin().getExperience().triggerUpdate(getPlayer());
    }

    public enum Type {

        EXP("Total amount of experience.", 0),
        LEVEL("Current level.", 1, 50) {
            @Override
            public void onSet(Player player, long value) {
                final Experience experience = Main.getPlugin().getExperience();
                final long expRequired = experience.getExpRequired(value);
                final long exp = experience.getExp(player);

                if (exp < expRequired) {
                    PlayerDatabase.getDatabase(player).getExperienceEntry().set(EXP, expRequired);
                }
            }
        },
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

        public void onSet(Player player, long value) {
        }

    }


}
