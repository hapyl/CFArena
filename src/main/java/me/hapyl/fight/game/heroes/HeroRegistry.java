package me.hapyl.fight.game.heroes;

import me.hapyl.fight.enumclass.EnumClass;
import me.hapyl.fight.enumclass.EnumClassInspector;
import me.hapyl.fight.game.heroes.archer.Archer;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public final class HeroRegistry extends EnumClass {

    /**
     * {@link Archer}
     */
    public static final Archer ARCHER;

    // *==* Private *==* //
    private static final Set<Hero> registry;

    static {
        // Instantiate enum class
        EnumClass.instantiate(HeroRegistry.class, new EnumClassInspector()
                .hasMethod("values", Set.class)
        );

        // Prepare registry
        registry = new LinkedHashSet<>();

        // Register classes below
        ARCHER = register(new Archer(Heroes.ARCHER));
    }

    @Nonnull
    public static Set<Hero> values() {
        return new HashSet<>(registry);
    }

    private static <E extends Hero> E register(@Nonnull E e) {
        registry.add(e);
        return e;
    }

}
