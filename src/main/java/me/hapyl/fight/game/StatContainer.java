package me.hapyl.fight.game;

import com.google.common.collect.Maps;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.util.NonNullableElementHolder;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * Keep in mind this only tracks the numbers, does not actually add them.
 */
public class StatContainer extends NonNullableElementHolder<Player> {

    private final Map<Talents, Long> abilityUsage;
    private final Map<Type, Double> valueMap;

    private boolean winner;

    public StatContainer(Player player) {
        super(player);

        abilityUsage = Maps.newHashMap();
        valueMap = Maps.newHashMap();
        winner = false;
    }

    public void addAbilityUsage(Talents talent) {
        abilityUsage.compute(talent, (a, b) -> b == null ? 1 : b + 1);
    }

    public double getValue(Type type) {
        return valueMap.getOrDefault(type, 0d);
    }

    public void addValue(Type type, double l) {
        valueMap.compute(type, (a, b) -> b == null ? l : b + l);
    }

    public void setValue(Type type, double newValue) {
        valueMap.put(type, newValue);
    }

    public String getString(Type type) {
        final double value = getValue(type);
        return " " + (value > 0 ? type.getTextHas().formatted(value) : type.getTextHasnt());
    }

    public Player getPlayer() {
        return this.getElement();
    }

    public Map<Talents, Long> getUsedAbilities() {
        return abilityUsage;
    }

    public void markAsWinner() {
        this.winner = true;
    }

    public boolean isWinner() {
        return winner;
    }

    public enum Type {
        COINS("&7You've earned &e&l%s &7coins this game!", "&7You haven't earned any coins this game."),
        KILLS("&7You've killed &e&l%s &7opponents this game!", "&7You haven't killed anyone this game."),
        EXP("&7You've earned &b&l%s &7exp this game!", "&7You haven't earned any exp this game."),
        DEATHS("&7You've died &e&l%s &7times this game!", "&7You haven't died this game. Wow."),
        DAMAGE_DEALT("&7You've dealt &c&l%s &7damage this game!", "&7You haven't dealt any damage this game."),
        DAMAGE_TAKEN("&7You've taken &c&l%s &7damage this game!", "&7You haven't taken any damage this game."),
        ULTIMATE_USED("&7You've used your ultimate &e&l%s &7times this game!", "&7You haven't used your ultimate this game."),

        ;

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
