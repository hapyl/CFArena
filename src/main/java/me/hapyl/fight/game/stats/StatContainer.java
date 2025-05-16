package me.hapyl.fight.game.stats;

import com.google.common.collect.Maps;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.util.NonNullableElementHolder;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * Keep in mind this only tracks the numbers, does not actually add them.
 */
public class StatContainer extends NonNullableElementHolder<GamePlayer> {

    private final Map<Talent, Long> abilityUsage;
    private final Map<StatType, Double> valueMap;

    private boolean winner;

    public StatContainer(GamePlayer player) {
        super(player);

        abilityUsage = Maps.newHashMap();
        valueMap = Maps.newHashMap();
        winner = false;
    }

    public void addAbilityUsage(Talent talent) {
        abilityUsage.compute(talent, (a, b) -> b == null ? 1 : b + 1);
    }

    public double getValue(StatType type) {
        return valueMap.getOrDefault(type, 0d);
    }

    public void addValue(StatType type, double newValue) {
        valueMap.compute(type, (a, b) -> b == null ? newValue : b + newValue);
    }

    public void setValue(StatType type, double newValue) {
        valueMap.put(type, newValue);
    }

    public Player getPlayer() {
        return this.getElement().getEntity();
    }

    public Map<Talent, Long> getUsedAbilities() {
        return abilityUsage;
    }

    public void markAsWinner() {
        this.winner = true;
    }

    public boolean isWinner() {
        return winner;
    }

    public Map<StatType, Double> nonNegativeValuesMapped() {
        final Map<StatType, Double> map = Maps.newHashMap();

        valueMap.forEach((t, v) -> {
            if (v > 0) {
                map.put(t, v);
            }
        });

        return map;
    }


    public boolean greaterThanZero(@Nonnull StatType statType) {
        return getValue(statType) > 0;
    }
}
