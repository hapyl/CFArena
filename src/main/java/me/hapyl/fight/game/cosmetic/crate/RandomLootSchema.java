package me.hapyl.fight.game.cosmetic.crate;

import com.google.common.collect.Maps;
import me.hapyl.fight.game.cosmetic.RandomDrop;
import me.hapyl.fight.game.cosmetic.RareItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Random;

public class RandomLootSchema<T extends RareItem> {

    private final Map<RandomDrop, Float> schema;

    public RandomLootSchema() {
        this.schema = Maps.newHashMap();
    }

    public void set(@Nonnull ItemContents<T> items) {
        if (items.isEmpty()) {
            return;
        }

        items.forEachRarityIfNotEmpty(rarity -> schema.put(rarity, rarity.getDropChance()));

        // compensate missing entries if there are any
        final float sumRates = sumRates();
        if (sumRates < 1.0f) {
            float compensation = (1.0f - sumRates) / schema.size();

            schema.keySet().forEach(rarity -> schema.compute(rarity, (r, v) -> v == null ? 0 : v + compensation));
        }
    }

    public float getDropChance(@Nonnull RandomDrop rarity) {
        return schema.getOrDefault(rarity, 0.0f);
    }

    @Nonnull
    public String getDropChanceString(@Nonnull RandomDrop rarity) {
        return "%.1f%%".formatted(getDropChance(rarity) * 100);
    }

    @Override
    public String toString() {
        return schema.toString() + "~" + sumRates();
    }

    @Nullable
    public RandomDrop random() {
        final Random random = new Random();
        final float randomNumber = random.nextFloat();

        double cumulativeProbability = 0.0;
        for (Map.Entry<RandomDrop, Float> entry : schema.entrySet()) {
            cumulativeProbability += entry.getValue();

            if (randomNumber <= cumulativeProbability) {
                return entry.getKey();
            }
        }

        return null;
    }

    private float sumRates() {
        return schema.values().stream().reduce(0.0f, Float::sum);
    }
}
