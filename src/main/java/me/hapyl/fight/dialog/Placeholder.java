package me.hapyl.fight.dialog;

import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.npc.PersistentNPC;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.function.Function;

/**
 * A collection of placeholders that may be formatted at runtime into a string.
 *
 * @param <T> - Placeholder type.
 * @see #formatAll(String, Object...)
 */
public abstract class Placeholder<T> {

    public static final Placeholder<PersistentNPC> NPC_NAME
            = of("npc_name", PersistentNPC.class, PersistentNPC::getName);

    public static final Placeholder<Player> PLAYER_NAME
            = of("player", Player.class, Player::getName);

    public static final Placeholder<Hero> HERO
            = of("hero", Hero.class, Hero::getName);

    private final String pattern;
    private final Class<T> clazz;

    Placeholder(String name, Class<T> clazz) {
        this.pattern = "{" + name + "}";
        this.clazz = clazz;
    }

    @Nonnull
    public abstract String format(@Nonnull T t);

    @Nonnull
    public String format(@Nonnull String string, @Nonnull T t) {
        if (!string.contains("{") || !string.contains("}")) {
            return string;
        }

        return string.replace(pattern, format(t));
    }

    private String checkAndFormat(String string, Object... object) {
        for (Object obj : object) {
            if (!clazz.isInstance(obj)) {
                continue;
            }

            string = format(string, clazz.cast(obj));
        }

        return string;
    }

    @Nonnull
    public static String formatAll(@Nonnull String string, @Nonnull Object... object) {
        string = NPC_NAME.checkAndFormat(string, object);
        string = PLAYER_NAME.checkAndFormat(string, object);

        return string;
    }

    private static <T> Placeholder<T> of(String name, Class<T> clazz, Function<T, String> fn) {
        return new Placeholder<>(name, clazz) {
            @Nonnull
            @Override
            public String format(@Nonnull T t) {
                return fn.apply(t);
            }
        };
    }

}
