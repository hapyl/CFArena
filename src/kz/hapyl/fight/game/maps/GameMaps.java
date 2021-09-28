package kz.hapyl.fight.game.maps;

import kz.hapyl.fight.game.maps.maps.DragonsGorge;
import kz.hapyl.fight.game.maps.maps.JapanMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public enum GameMaps {

	// non-playable map, storing here for easy coordinate grab and consistency
	SPAWN(new GameMap("Spawn", "You spawn here!", Material.BLUE_BED).addLocation(0, 15, 0), false),

	ARENA("Arena", "A great arena to fight on!", Material.COARSE_DIRT, Size.MEDIUM, asList(asLoc(-94, 3, -112))),
	JAPAN(new JapanMap()),
	GREENHOUSE(
			"Greenhouse",
			"This greenhouse has a lot of flowers to hide, and bunch of secret passages.__&8&oWho's made them?",
			Material.OAK_SAPLING,
			Size.SMALL,
			asSingleLoc(10, 7, 65)
	),
	RAILWAY(
			"Railway Station",
			"The action happening in the unknown Railway Station. Big area to fight, and to hide.",
			Material.RAIL,
			Size.LARGE,
			asSingleLoc(110, 16, 11)
	),
	NETHER(
			"The Nether",
			"The nether is hot place with a bunch of lava, so if you didn't bring your suntan cream with you, don't stay there for a long time!",
			Material.CRIMSON_NYLIUM,
			Size.SMALL,
			asSingleLoc(-165, 3, -66)
	),
	CLOUDS(
			"The Clouds",
			"Ruined city built on the clouds somewhere in the sky.",
			Material.WHITE_STAINED_GLASS,
			Size.MASSIVE,
			asSet(new MapFeature("Boosters", "Propel yourself to another island. Do not fall though, it would be hard to explain your death...") {
				@Override
				public void tick(int tick) {

				}
			}),
			asSingleLoc(1052, 26, 1008)
	),
	LIBRARY(
			"Infinite Library",
			"A library that stuck in the void.",
			Material.BOOKSHELF,
			Size.MEDIUM,
			asList(
					asLoc(99, 10, 115, -180, 0),
					asLoc(108, 21, 114, -180, 0),
					asLoc(90, 21, 114, -180, 0)
			)
	),
	DRAGONS_GORGE(new DragonsGorge());

	private final GameMap map;
	private final boolean isPlayable;

	GameMaps(String name, String info, Material material, Size size, List<Location> locations) {
		this(new GameMap(name, info, material).setSize(size).addLocation(locations), true);
	}

	GameMaps(String name, String info, Material material, Size size, Set<MapFeature> features, List<Location> locations) {
		this(new GameMap(name, info, material).setSize(size).addLocation(locations).addFeature(features), true);
	}

	GameMaps(GameMap map) {
		this(map, true);
	}

	GameMaps(GameMap map, boolean isPlayable) {
		this.map = map;
		this.isPlayable = isPlayable;
	}

	public boolean isPlayable() {
		return isPlayable;
	}

	public GameMap getMap() {
		return map;
	}

	protected static <E> List<E> asList(E... e) {
		return Arrays.asList(e);
	}

	protected static <E> Set<E> asSet(E... e) {
		return new HashSet<>(Arrays.asList(e));
	}

	protected static List<Location> asSingleLoc(double x, double y, double z) {
		return asList(asLoc(x, y, z));
	}

	protected static Location asLoc(double x, double y, double z) {
		return new Location(Bukkit.getWorlds().get(0), x, y, z);
	}

	protected static Location asLoc(double x, double y, double z, float f, float b) {
		return new Location(Bukkit.getWorlds().get(0), x, y, z, f, b);
	}

}
