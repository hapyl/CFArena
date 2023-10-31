package me.hapyl.fight.guigame;

import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class GUISingleGameInstance extends GUIGameInstance {
    public GUISingleGameInstance(@Nonnull GUIGame guiGame, @Nonnull Player player) {
        super(guiGame, new Player[] { player });
    }

    @Nonnull
    public Player getPlayer() {
        return players[0];
    }
}
