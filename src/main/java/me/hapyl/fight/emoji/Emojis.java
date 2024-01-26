package me.hapyl.fight.emoji;

import me.hapyl.fight.database.rank.PlayerRank;

import javax.annotation.Nullable;

public enum Emojis {

    SMILE("smile", "&a🙂");

    private static final String prefixChar = ":";

    private final String text;
    private final String emoji;
    private final PlayerRank rank;

    Emojis(String text, String emoji) {
        this(text, emoji, PlayerRank.DEFAULT);
    }

    Emojis(String text, String emoji, PlayerRank rank) {
        this.text = prefixChar + text + prefixChar;
        this.emoji = emoji;
        this.rank = rank;
    }

    @Nullable
    public Emojis byText(String text) {
        return null;
    }
}
