package me.hapyl.fight.guigame;

import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class GUIGameInstance {

    protected final GUIGame guiGame;
    protected final Player[] players;

    public GUIGameInstance(@Nonnull GUIGame guiGame, @Nonnull Player[] players) {
        this.guiGame = guiGame;
        this.players = players;
    }
}
