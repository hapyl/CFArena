package me.hapyl.fight.game.ui;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.spigotutils.module.util.Validate;
import org.bukkit.ChatColor;

import javax.annotation.Nonnull;

/**
 * A formatter for player UI.
 */
public class UIFormat {

    public static final String DIV_RAW = "⁑";
    public static final String DIV = " &8⁑&r ";
    public static final UIFormat DEFAULT = new UIFormat("&c&l{Health} &c❤ {Div} {Ultimate} &b※");

    private final String format;

    public UIFormat(String format) {
        Validate.isTrue(format.contains("{Health}"), "format must contain '{Health}'");
        Validate.isTrue(format.contains("{Ultimate}"), "format must contain '{Ultimate}'");
        this.format = format;
    }

    public String getFormat() {
        return format;
    }

    @Nonnull
    public String format(@Nonnull GamePlayer player, @Nonnull ChatColor ultimateColor) {
        String toFormat = format;

        toFormat = toFormat.replace("{Health}", player.getHealthFormatted());
        toFormat = toFormat.replace("{Ultimate}", player.getUltimateString(ultimateColor));
        toFormat = toFormat.replace("{Div}", DIV);

        // UIComponent
        final Hero hero = player.getHero();

        if (hero instanceof UIComponent component) {
            toFormat = sew(toFormat, component, player);
        }

        if (hero.getWeapon() instanceof UIComponent component) {
            toFormat = sew(toFormat, component, player);
        }

        return toFormat;
    }

    private String sew(String sew, UIComponent component, GamePlayer player) {
        final StringBuilder builder = new StringBuilder(sew);
        final String string = component.getString(player);

        if (!string.isEmpty()) {
            builder.append(" %s ".formatted(DIV)).append(string);
        }

        return builder.toString();
    }

}
