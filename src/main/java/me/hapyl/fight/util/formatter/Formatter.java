package me.hapyl.fight.util.formatter;

import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.talents.Talent;

import java.util.Map;

public class Formatter {

    private static final String DEFAULT_COLOR = "&7";
    private static final Map<Class<?>, Entry<?>> FORMAT;

    static {
        FORMAT = Map.of(
                Talent.class, new Entry<>(Talent.class, "&e") {
                    @Override
                    public String display(Talent talent) {
                        return talent.getName();
                    }
                },
                Number.class, new Entry<>(Number.class, "&b"),
                Hero.class, new Entry<>(Hero.class, "&c") {
                    @Override
                    public String display(Hero hero) {
                        return hero.getName();
                    }
                }
        );
    }

    private final String string;

    private Formatter(String string) {
        this.string = string; // allow default colors
    }

    public static Formatter of(String string) {
        return new Formatter(string);
    }

    public static String of(String string, Object... args) {
        return of(string).format(args);
    }

    public String format(Object... args) {
        String formatted = string;

        for (Object arg : args) {
            formatted = formatted.replaceFirst("\\{}", getFormatter(arg) + DEFAULT_COLOR);
        }

        return formatted;
    }

    private static String getFormatter(Object object) {
        return FORMAT.entrySet().stream()
                .filter(entry -> entry.getKey().isInstance(object))
                .map(Map.Entry::getValue)
                .map(entry -> entry.format(object))
                .findFirst()
                .orElse("$INVALID_FORMATTER$");
    }

}
