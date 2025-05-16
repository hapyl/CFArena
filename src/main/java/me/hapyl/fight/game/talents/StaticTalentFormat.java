package me.hapyl.fight.game.talents;

import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.eterna.module.util.Named;
import me.hapyl.fight.game.heroes.ultimate.UltimateTalent;
import me.hapyl.fight.util.displayfield.DisplayFieldProvider;
import me.hapyl.fight.util.displayfield.DisplayFieldSerializer;

import javax.annotation.Nonnull;
import java.util.function.Function;

public final class StaticTalentFormat<T> {

    public static final StaticTalentFormat<Named> NAME
            = create("{name}", nameable -> "&a" + nameable.getName() + "&7");

    public static final StaticTalentFormat<Timed> DURATION
            = create("{duration}", timed -> "&b" + BukkitUtils.roundTick(timed.getDuration()) + "s&7");

    public static final StaticTalentFormat<Cooldown> COOLDOWN
            = create("{cooldown}", cooldown -> "&b" + BukkitUtils.roundTick(cooldown.getCooldown()) + "s&7");

    public static final StaticTalentFormat<UltimateTalent> ULTIMATE_CAST_DURATION
            = create("{cast}", ultimate -> "&b" + BukkitUtils.roundTick(ultimate.getCastDuration()) + "s&7");

    private final String target;
    private final Function<T, String> function;

    StaticTalentFormat(String target, Function<T, String> function) {
        this.target = target;
        this.function = function;
    }

    @Nonnull
    public String format0(@Nonnull String string, @Nonnull T t) {
        string = string.replace(target, function.apply(t));

        // Format display fields
        if (t instanceof DisplayFieldProvider provider) {
            string = DisplayFieldSerializer.serialize(string, provider);
        }

        return string;
    }

    @Nonnull
    public static String format(@Nonnull String string, @Nonnull Talent talent) {
        string = NAME.format0(string, talent);
        string = DURATION.format0(string, talent);
        string = COOLDOWN.format0(string, talent);

        if (talent instanceof UltimateTalent ultimateTalent) {
            string = ULTIMATE_CAST_DURATION.format0(string, ultimateTalent);
        }

        return string;
    }

    private static <T> StaticTalentFormat<T> create(@Nonnull String target, @Nonnull Function<T, String> function) {
        return new StaticTalentFormat<>(target, function);
    }

}
