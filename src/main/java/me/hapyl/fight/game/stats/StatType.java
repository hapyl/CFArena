package me.hapyl.fight.game.stats;

import me.hapyl.eterna.module.registry.KeyedEnum;

public enum StatType implements KeyedEnum {
    COINS("&7You've earned &e&l%s &7coins this game!", "&7You haven't earned any coins this game."),
    KILLS("&7You've killed &e&l%s &7opponents this game!", "&7You haven't killed anyone this game."),
    ASSISTS("&7You've assisted &e&l%s &7opponents this game!", "&7You haven't assisted anyone this game."),
    EXP("&7You've earned &b&l%s &7exp this game!", "&7You haven't earned any exp this game."),
    DEATHS("&7You've died &e&l%s &7times this game!", "&7You haven't died this game. Wow."),
    DAMAGE_DEALT("&7You've dealt &c&l%s &7damage this game!", "&7You haven't dealt any damage this game."),
    DAMAGE_TAKEN("&7You've taken &c&l%s &7damage this game!", "&7You haven't taken any damage this game."),
    ULTIMATE_USED("&7You've used your ultimate &e&l%s &7times this game!", "&7You haven't used your ultimate this game."),

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
}
