package me.hapyl.fight.guigame.archive;

import me.hapyl.fight.guigame.GUIGame;
import me.hapyl.fight.guigame.GUIGameInstance;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class NimGame extends GUIGame {
    public NimGame() {
        super("Nim", "Nim");
    }

    @Nonnull
    @Override
    public GUIGameInstance createGameInstance(@Nonnull Player[] players) {
        return new GUIGameInstance(this, players);
    }
}
