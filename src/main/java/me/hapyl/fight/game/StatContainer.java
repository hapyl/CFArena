package me.hapyl.fight.game;

import me.hapyl.fight.util.NonNullableElementHolder;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Keep in mind this only tracks the numbers, does not actually add them.
 */
public class StatContainer extends NonNullableElementHolder<Player> {

    private final Map<Type, Long> valueMap;

    public StatContainer(Player player) {
        super(player);
        valueMap = new HashMap<>();
    }

    public long getValue(Type type) {
        return valueMap.getOrDefault(type, 0L);
    }

    public void addValue(Type type, long l) {
        valueMap.compute(type, (a, b) -> b == null ? l : b + l);
    }

    public void setValue(Type type, long newValue) {
        valueMap.put(type, newValue);
    }

    public String getString(Type type) {
        final long value = getValue(type);
        return " " + (value > 0 ? type.getTextHas().formatted(value) : type.getTextHasnt());
    }

    public Player getPlayer() {
        return this.getElement();
    }

    public enum Type {
        COINS("&7You've earned &e&l%s &7coins this game!", "&7You haven't earned any coins this game."),
        KILLS("&7You've killed &e&l%s &7opponents this game!", "&7You haven't killed anyone this game."),
        EXP("&7You've earned &b&l%s &7exp this game!", "&7You haven't earned any exp this game."),
        DEATHS("&7You've died &e&l%s &7times this game!", "&7You haven't died this game. Wow.");

        private final String textHas;
        private final String textHasnt;

        Type(String textHas, String textHasnt) {
            this.textHas = textHas;
            this.textHasnt = textHasnt;
        }

        public String getTextHas() {
            return textHas;
        }

        public String getTextHasnt() {
            return textHasnt;
        }
    }

}
