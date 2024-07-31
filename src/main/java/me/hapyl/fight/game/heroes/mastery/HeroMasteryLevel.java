package me.hapyl.fight.game.heroes.mastery;

import me.hapyl.fight.util.Compile;
import me.hapyl.fight.util.Described;
import me.hapyl.fight.util.displayfield.DisplayFieldProvider;
import me.hapyl.fight.util.displayfield.DisplayFieldSerializer;
import me.hapyl.spigotutils.module.util.Validate;

import javax.annotation.Nonnull;

public class HeroMasteryLevel implements Described, DisplayFieldProvider {

    private final int level;
    private final String name;
    private final Compile<String> description;

    public HeroMasteryLevel(int level, String name, String description) {
        Validate.isTrue(level >= 1, "mastery level cannot be lower than 1");
        Validate.isTrue(level <= HeroMastery.MAX_LEVEL, "mastery level cannot be higher than " + HeroMastery.MAX_LEVEL);

        this.level = level;
        this.name = name;
        this.description = compileDescription(description);
    }

    public int getLevel() {
        return level;
    }

    @Nonnull
    @Override
    public String getName() {
        return this.name;
    }

    @Nonnull
    @Override
    public String getDescription() {
        return this.description.compile();
    }

    private Compile<String> compileDescription(@Nonnull String description) {
        return Compile.of(description, str -> DisplayFieldSerializer.formatString(str, this));
    }

}
