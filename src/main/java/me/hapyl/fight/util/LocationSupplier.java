package me.hapyl.fight.util;

import org.bukkit.Location;

import java.util.function.Consumer;

@Deprecated(forRemoval = true)
public class LocationSupplier {

	private final Location location;

	public LocationSupplier(Location e) {
		this.location = e;
	}

	public Location supply(Consumer<Location> action) {
		action.accept(location);
		return location;
	}

	public Location get() {
		return location;
	}
}
