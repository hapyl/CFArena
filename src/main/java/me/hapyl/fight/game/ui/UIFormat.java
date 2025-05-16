package me.hapyl.fight.game.ui;

import me.hapyl.eterna.module.util.Validate;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.ultimate.UltimateTalent;
import me.hapyl.fight.util.StrBuilder;

import javax.annotation.Nonnull;

/**
 * A formatter for player UI.
 */
public class UIFormat {

    public static final String DIV_RAW = "⁑";
    public static final String DIV = " &8⁑&r ";
    public static final UIFormat DEFAULT = new UIFormat("{Health} {Div} {Ultimate}");

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
    public String format(@Nonnull GamePlayer player, @Nonnull UltimateTalent.DisplayColor type) {
        final StrBuilder builder = new StrBuilder(format);

        builder.replace("{Health}", player.getHealthFormatted());
        builder.replace("{Ultimate}", player.getUltimateString(type));
        builder.replace("{Div}", DIV);

        // UIComponent
        final Hero hero = player.getHero();

        if (hero instanceof UIComponent component) {
            builder.append(sew(component, player));
        }

        if (hero.getWeapon() instanceof UIComponent component) {
            builder.append(sew(component, player));
        }

        builder.append(sew(player.getUIComponentCache(), player));

        return builder.toString();
    }

    private String sew(UIComponent component, GamePlayer player) {
        final StringBuilder builder = new StringBuilder();
        final String string = component.getString(player);

        if (!string.isEmpty()) {
            builder.append(" %s ".formatted(DIV)).append(string);
        }

        return builder.toString();
    }

}
