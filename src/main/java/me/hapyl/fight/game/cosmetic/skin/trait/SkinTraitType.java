package me.hapyl.fight.game.cosmetic.skin.trait;

import com.google.common.collect.Lists;
import me.hapyl.fight.util.Caster;
import me.hapyl.fight.util.Named;

import javax.annotation.Nonnull;
import java.util.List;

public final class SkinTraitType<T extends SkinTrait> implements Caster<SkinTrait>, Named {

    public static final SkinTraitType<SkinTraitOnTick> TICK;
    public static final SkinTraitType<SkinTraitOnKill> KILL;
    public static final SkinTraitType<SkinTraitOnDeath> DEATH;
    public static final SkinTraitType<SkinTraitOnMove> MOVE;
    public static final SkinTraitType<SkinTraitOnStill> STILL;
    public static final SkinTraitType<SkinTraitOnWin> WIN;

    private static final List<SkinTraitType<?>> values;

    static {
        values = Lists.newArrayList();

        TICK = of("Every Tick", SkinTraitOnTick.class);
        KILL = of("On Kill", SkinTraitOnKill.class);
        DEATH = of("On Death", SkinTraitOnDeath.class);
        MOVE = of("When Moving", SkinTraitOnMove.class);
        STILL = of("When Standing Still", SkinTraitOnStill.class);
        WIN = of("Winning a Game", SkinTraitOnWin.class);
    }

    private final String name;
    private final Class<T> clazz;

    private SkinTraitType(String name, Class<T> clazz) {
        this.name = name;
        this.clazz = clazz;
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    @Nonnull
    @Override
    public T cast(@Nonnull Object object) throws ClassCastException {
        if (clazz.isInstance(object)) {
            return clazz.cast(object);
        }

        throw new ClassCastException("Invalid trait type! Expected %s, got %s!".formatted(
                clazz.getSimpleName(),
                object.getClass().getSimpleName()
        ));
    }

    @Nonnull
    public static List<SkinTraitType<?>> values() {
        return Lists.newArrayList(values);
    }

    private static <T extends SkinTrait> SkinTraitType<T> of(String name, Class<T> t) {
        final SkinTraitType<T> type = new SkinTraitType<>(name, t);
        values.add(type);

        return type;
    }

}
