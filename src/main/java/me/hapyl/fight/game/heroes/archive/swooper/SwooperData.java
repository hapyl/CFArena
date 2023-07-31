package me.hapyl.fight.game.heroes.archive.swooper;

import me.hapyl.fight.game.entity.GamePlayer;
import org.bukkit.Location;

import javax.annotation.Nonnull;

/**
 * This class stores data for swooper for the given time.
 */
public record SwooperData(GamePlayer player, Location location, double health) {

    public SwooperData(GamePlayer player) {
        this(player, player.getPlayer().getLocation(), player.getHealth());
    }

    @Nonnull
    public Location getLocation() {
        return new Location(location.getWorld(), location.getX(), location.getY(), location.getZ());
    }

}
