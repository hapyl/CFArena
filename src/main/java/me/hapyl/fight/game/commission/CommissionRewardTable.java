package me.hapyl.fight.game.commission;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.hapyl.eterna.module.util.WeightedCollection;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

public class CommissionRewardTable extends WeightedCollection<CommissionReward> {

    private final List<CommissionReward> guaranteedPool;

    public CommissionRewardTable() {
        this.guaranteedPool = Lists.newArrayList();
    }

    public void addGuaranteedReward(@Nonnull CommissionReward reward) {
        this.guaranteedPool.add(reward);
    }

    @Nonnull
    public LinkedHashMap<CommissionReward, Double> getRewardsSorted(@Nonnull RewardSort sort) {
        final List<WeightedCollection<CommissionReward>.WeightedElement> elements = getWeightedElements();

        if (elements.isEmpty()) {
            return Maps.newLinkedHashMap();
        }

        // Add guaranteed items
        this.guaranteedPool.forEach(reward -> {
            elements.add(new WeightedElement(reward, Integer.MAX_VALUE) {
                @Override
                public double getDropChance() {
                    return 1.0d;
                }
            });
        });

        elements.sort(sort.comparator);
        return elements.stream().collect(Collectors.toMap(WeightedElement::t, WeightedElement::getDropChance, (a, b) -> b, LinkedHashMap::new));
    }

    public enum RewardSort {
        RARE_TO_COMMON(Comparator.comparingDouble(WeightedElement::weight)),
        COMMON_TO_RATE(RARE_TO_COMMON.comparator.reversed());

        private final Comparator<WeightedElement> comparator;

        RewardSort(Comparator<WeightedCollection<CommissionReward>.WeightedElement> comparator) {
            this.comparator = comparator;
        }
    }

}
