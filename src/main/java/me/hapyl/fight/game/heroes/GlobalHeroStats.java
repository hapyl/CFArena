package me.hapyl.fight.game.heroes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.hapyl.eterna.module.util.Compute;
import me.hapyl.fight.database.collection.HeroStatsCollection;
import me.hapyl.fight.game.stats.StatType;

import javax.annotation.Nonnull;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GlobalHeroStats {

    private final Map<StatType, List<StatValue>> values;

    public GlobalHeroStats() {
        this.values = Maps.newLinkedHashMap();

        for (Hero hero : HeroRegistry.playable()) {
            final HeroStatsCollection stats = hero.getStats();

            for (StatType stat : StatType.values()) {
                values.compute(stat, Compute.listAdd(new StatValue(hero, stat, stats.getStat(stat))));
            }
        }

        // Sort
        values.forEach((stat, list) -> {
            list.sort((o1, o2) -> Double.compare(o2.value, o1.value));
        });

        // Assign stat rating
        for (List<StatValue> values : values.values()) {
            int rank = 1;

            for (StatValue value : values) {
                value.setRank(rank++);
            }
        }

        // Assign hero rating
        final Map<Hero, Integer> rating = Maps.newLinkedHashMap();
        for (List<StatValue> values : values.values()) {

            for (StatValue value : values) {
                final int rank = value.getRank();

                rating.compute(value.hero, (h, r) -> {
                    return r != null ? r + value.getRank() : rank;
                });
            }
        }

        int rank = 1;
        for (Hero hero : rating.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new))
                .keySet()) {

            hero.setRank(rank++);
        }

        rating.clear();
    }

    public int getRating(@Nonnull Hero hero, @Nonnull StatType type) {
        for (StatValue value : values.getOrDefault(type, Lists.newArrayList())) {
            if (value.hero == hero) {
                return value.getRank();
            }
        }

        return -1;
    }

    public void clear() {
        values.clear();
    }
}
