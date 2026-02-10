package me.hapyl.fight.game.maps;

import org.bukkit.Location;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

public class PredicateLocation {

    private final Location location;
    private final Predicate<Level> predicate;

    public PredicateLocation(Location location, Predicate<Level> predicate) {
        this.location = location;
        this.predicate = predicate;
    }

    public PredicateLocation(Location location) {
        this(location, t -> true);
    }

    @Nonnull
    public Location getLocation() {
        return location;
    }

    public boolean predicate(Level map) {
        return predicate.test(map);
    }

}
