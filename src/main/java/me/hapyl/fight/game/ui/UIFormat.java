package me.hapyl.fight.game.ui;

import me.hapyl.fight.game.GamePlayer;
import me.hapyl.spigotutils.module.util.Validate;

import javax.annotation.Nonnull;

/**
 * A formatter for player UI.
 */
public class UIFormat {

    public static final String DIV = " &8⁑&r ";
    public static final UIFormat DEFAULT = new UIFormat("&c&l{Health} &c❤ {Div} &b&l{Ultimate} &b※");

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
    public String format(@Nonnull GamePlayer player) {
        if (player.isAbstract()) {
            return "cannot format abstract player";
        }

        String toFormat = format;

        toFormat = toFormat.replace("{Health}", player.getHealthFormatted());
        toFormat = toFormat.replace("{Ultimate}", player.getUltimateString());
        toFormat = toFormat.replace("{Div}", DIV);

        // UIComponent
        if (player.getHero() instanceof UIComponent uiHero) {
            final StringBuilder builder = new StringBuilder(toFormat);
            if (!uiHero.getString(player.getPlayer()).isEmpty()) {
                builder.append(" %s ".formatted(DIV)).append(uiHero.getString(player.getPlayer()));
            }

            toFormat = builder.toString();
        }

        return toFormat;
    }

}
