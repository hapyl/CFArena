package me.hapyl.fight.game.maps.gamepack;

import me.hapyl.fight.game.entity.GamePlayer;

import javax.annotation.Nonnull;

public abstract class HackedPack {

    public final GamePlayer player;

    public HackedPack(@Nonnull GamePlayer player) {
        this.player = player;
    }

    public abstract void onPickup(@Nonnull GamePlayer player);
}
