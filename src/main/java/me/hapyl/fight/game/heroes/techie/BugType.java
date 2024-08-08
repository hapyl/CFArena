package me.hapyl.fight.game.heroes.techie;

import com.google.common.collect.Sets;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.attribute.temper.TemperInstance;
import me.hapyl.fight.util.Described;
import me.hapyl.eterna.module.util.CollectionUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashSet;

public enum BugType implements Described {

    TYPE_A("&4ᚨ", "Decreases %s.".formatted(AttributeType.ATTACK), AttributeType.ATTACK, 0.3d),
    TYPE_D("&2ᛞ", "Decreases %s.".formatted(AttributeType.DEFENSE), AttributeType.DEFENSE, 0.3d),
    TYPE_S("&3ᛊ", "Decreases %s.".formatted(AttributeType.ENERGY_RECHARGE), AttributeType.ENERGY_RECHARGE, 0.5d),


    ;

    private final String name;
    private final String description;
    private final TemperInstance temper;

    BugType(String name, String description, AttributeType type, double value) {
        this.name = name;
        this.description = description;
        this.temper = Temper.SABOTEUR.newInstance().decrease(type, value);
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    @Nonnull
    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return name;
    }

    @Nonnull
    public TemperInstance getTemper() {
        return temper;
    }

    @Nullable
    public static BugType random(@Nonnull Collection<BugType> existingBugs) {
        final HashSet<BugType> hashSet = Sets.newHashSet(values());
        hashSet.removeAll(existingBugs);

        if (hashSet.isEmpty()) {
            return null;
        }

        return CollectionUtils.randomElement(hashSet);
    }
}
