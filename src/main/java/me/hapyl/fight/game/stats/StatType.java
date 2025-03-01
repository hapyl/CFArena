package me.hapyl.fight.game.stats;

import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.registry.KeyedEnum;
import me.hapyl.fight.game.heroes.mastery.HeroMastery;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public enum StatType implements KeyedEnum {
    COINS(
            "You've earned &6%.0f&7 coins this game!",
            "You haven't earned any coins this game."
    ),

    KILLS(
            "You've killed &c%.0f&7 opponents this game!",
            "You haven't killed anyone this game."
    ),

    ASSISTS(
            "You've assisted &a%s&7 opponents this game!",
            "You haven't assisted anyone this game."
    ),

    EXP(
            "You've earned &9%.0f&7 exp this game!",
            "You haven't earned any exp this game."
    ),

    DEATHS(
            "You've died &4%.0f&7 times this game!",
            "You haven't died this game. Wow."
    ),

    DAMAGE_DEALT(
            "You've dealt &c%.0f&7 damage this game!",
            "You haven't dealt any damage this game."
    ),

    DAMAGE_TAKEN(
            "You've taken &c%.1f&7 damage this game!",
            "You haven't taken any damage this game."
    ),

    ULTIMATE_USED(
            "You've used your ultimate &b%.0f&7 times this game!",
            "You haven't used your ultimate this game."
    ),

    MASTERY_EARNED(
            "You've earned &6%.0f&7 mastery this game!",
            "You haven't earned any mastery this game."
    ) {
        @Override
        public double value(@Nonnull StatContainer container) {
            long earnedExp = 0;

            if (container.isWinner()) {
                earnedExp += HeroMastery.EXP_WIN;
            }

            return earnedExp + (long) (container.getValue(StatType.KILLS) * HeroMastery.EXP_ELIMINATION);
        }
    },

    // Used to store in the database, but unused in player stats
    WINS,
    PLAYED,

    ;

    private final String textHas;
    private final String textHasnt;

    StatType() {
        this("", "");
    }

    StatType(String textHas, String textHasnt) {
        this.textHas = textHas;
        this.textHasnt = textHasnt;
    }

    public String getTextHas() {
        return textHas;
    }

    public String getTextHasnt() {
        return textHasnt;
    }

    @Nonnull
    public String getReportString(@Nonnull StatContainer statContainer) {
        final double value = value(statContainer);

        return (value > 0 ? getTextHas().formatted(value) : getTextHasnt());
    }

    public double value(@Nonnull StatContainer container) {
        return container.getValue(this);
    }

    public void sendReportMessage(@Nonnull Player player, @Nonnull StatContainer stat) {
        Chat.sendMessage(player, " &7" + getReportString(stat));
    }

    public void sendReportMessageIfValueGreaterThanZero(@Nonnull Player player, @Nonnull StatContainer stat) {
        if (!(value(stat) > 0.0d)) {
            return;
        }

        Chat.sendMessage(player, " &7" + getReportString(stat));
    }
}
