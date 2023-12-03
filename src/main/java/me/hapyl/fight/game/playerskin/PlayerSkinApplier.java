package me.hapyl.fight.game.playerskin;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class PlayerSkinApplier {

    private final Player player;
    private final PlayerSkin skin;

    @Nonnull
    protected Location location;

    public PlayerSkinApplier(Player player, PlayerSkin skin) {
        this.player = player;
        this.skin = skin;
        this.location = player.getLocation();
    }
}
