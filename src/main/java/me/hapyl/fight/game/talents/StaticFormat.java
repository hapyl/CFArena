package me.hapyl.fight.game.talents;

import me.hapyl.fight.game.talents.archive.techie.Talent;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.fight.util.displayfield.DisplayFieldProvider;
import me.hapyl.fight.util.displayfield.DisplayFieldSerializer;
import me.hapyl.spigotutils.module.util.BukkitUtils;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.function.Function;

public final class StaticFormat<T> {

    public static final StaticFormat<Nameable> NAME
            = create("{name}", nameable -> "&a" + nameable.getName() + "&7");

    public static final StaticFormat<Timed> DURATION
            = create("{duration}", timed -> "&b" + BukkitUtils.roundTick(timed.getDuration()) + "s&7");

    public static final StaticFormat<Cooldown> COOLDOWN
            = create("{cooldown}", cooldown -> "&b" + BukkitUtils.roundTick(cooldown.getCooldown()) + "s&7");

    public static final StaticFormat<UltimateTalent> ULTIMATE_CAST_DURATION
            = create("{cast}", ultimate -> "&b" + BukkitUtils.roundTick(ultimate.getCastDuration()) + "s&7");

    private final String target;
    private final Function<T, String> function;

    private static <T> StaticFormat<T> create(@Nonnull String target, @Nonnull Function<T, String> function) {
        return new StaticFormat<>(target, function);
    }

    StaticFormat(String target, Function<T, String> function) {
        this.target = target;
        this.function = function;
    }

    public String format(@Nonnull String string, @Nonnull T t) {
        string = string.replace(target, function.apply(t));

        // Format display fields
        if (t instanceof DisplayFieldProvider) {
            for (Field field : t.getClass().getDeclaredFields()) {
                final DisplayField annotation = field.getAnnotation(DisplayField.class);

                if (annotation == null) {
                    continue;
                }

                string = string.replace("{" + field.getName() + "}", DisplayFieldSerializer.formatField(field, t, annotation));
            }
        }

        return string;
    }

    @Nonnull
    public static String formatTalent(@Nonnull String string, @Nonnull Talent talent) {
        string = NAME.format(string, talent);
        string = DURATION.format(string, talent);
        string = COOLDOWN.format(string, talent);

        if (talent instanceof UltimateTalent ultimateTalent) {
            string = ULTIMATE_CAST_DURATION.format(string, ultimateTalent);
        }

        return string;
    }

}
