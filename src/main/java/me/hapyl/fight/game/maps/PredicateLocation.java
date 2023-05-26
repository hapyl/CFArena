package me.hapyl.fight.game.maps;

import org.bukkit.Location;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

public class PredicateLocation<T extends GameMap> {

    private final Location location;
    private final Predicate<T> predicate;

    public PredicateLocation(Location location, Predicate<T> predicate) {
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

    public boolean predicate(T map) {
        return predicate.test(map);
    }

}
