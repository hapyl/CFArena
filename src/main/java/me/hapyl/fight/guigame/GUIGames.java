package me.hapyl.fight.guigame;

import me.hapyl.fight.guigame.archive.NimGame;

import javax.annotation.Nonnull;

public enum GUIGames {

    NIM(new NimGame()),

    ;

    private final GUIGame game;

    GUIGames(@Nonnull GUIGame game) {
        this.game = game;
    }

    @Nonnull
    public GUIGame getGame() {
        return game;
    }
}
