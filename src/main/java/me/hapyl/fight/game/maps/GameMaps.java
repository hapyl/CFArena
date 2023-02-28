package me.hapyl.fight.game.maps;

import me.hapyl.fight.game.maps.features.CloudFeatures;
import me.hapyl.fight.game.maps.features.LibraryCat;
import me.hapyl.fight.game.maps.features.LibraryFeature;
import me.hapyl.fight.game.maps.maps.DragonsGorge;
import me.hapyl.fight.game.maps.maps.JapanMap;
import me.hapyl.spigotutils.module.util.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.*;

public enum GameMaps {

    // non-playable map, storing here for easy coordinate grab and consistency
    SPAWN(new GameMap("Spawn", "You spawn here!", Material.BLUE_BED, 0).addLocation(0, 64, 0), false),
    TRAINING_GROUNDS(new GameMap("Training Grounds", "Test heroes abilities here!", Material.WITHER_SKELETON_SKULL, 0).addLocation(
            100,
            64,
            100
    ), false),

    ARENA("Arena", "A great arena to fight on!", Material.COARSE_DIRT, 100, Size.MEDIUM, asList(asLoc(100, 64, 0))),

    JAPAN(new JapanMap()),

    GREENHOUSE(
            "Greenhouse",
            "This greenhouse has a lot of flowers to hide, and bunch of secret passages.__&8&oWho's made them?",
            Material.OAK_SAPLING,
            100,
            Size.SMALL,
            asSingleLoc(-99, 64, -6)
    ),

    RAILWAY(
            "Railway Station",
            "The action happening in the unknown Railway Station. Big area to fight, and to hide.",
            Material.RAIL,
            120,
            Size.LARGE,
            asList(asLoc(32, 70, 99), asLoc(-16, 70, 99))
    ),

    NEW_RAILWAY("Railway", "{}", Material.POWERED_RAIL, 100, Size.LARGE, asList(
            asLoc(10, 72, 217),
            asLoc(10, 72, 184),
            asLoc(20, 64, 200),
            asLoc(-2, 64, 200),
            asLoc(41, 72, 200),
            asLoc(-19, 76, 200),
            asLoc(2, 52, 200)
    )),

    CLOUDS(
            "The Clouds",
            "Ruined city built on the clouds somewhere in the sky.",
            Material.WHITE_STAINED_GLASS,
            120,
            Size.MASSIVE,
            asList(new CloudFeatures()),
            asSingleLoc(500, 64, 500)
    ),

    LIBRARY(
            "Infinite Library",
            "A library that stuck in the void.",
            Material.BOOKSHELF,
            100,
            Size.MEDIUM,
            asList(new LibraryFeature(), new LibraryCat()),
            asList(asLoc(0, 64, -90, -180, 0), asLoc(-10, 74, -95, -180, 0), asLoc(9, 74, -95, -180, 0))
    ),

    DRAGONS_GORGE(new DragonsGorge());

    private final GameMap map;
    private final boolean isPlayable;

    GameMaps(String name, String info, Material material, int ticks, Size size, List<Location> locations) {
        this(new GameMap(name, info, material, ticks).setSize(size).addLocation(locations), true);
    }

    GameMaps(String name, String info, Material material, int ticks, Size size, List<MapFeature> features, List<Location> locations) {
        this(new GameMap(name, info, material, ticks).setSize(size).addLocation(locations).addFeature(features), true);
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

    public static List<GameMaps> getPlayableMaps() {
        final List<GameMaps> maps = new ArrayList<>();
        for (final GameMaps value : values()) {
            if (value == null || !value.isPlayable()) {
                continue;
            }
            maps.add(value);
        }
        return maps;
    }

    public String getName() {
        return map.getName();
    }

    public static GameMaps byName(String str, GameMaps def) {
        final GameMaps gm = Validate.getEnumValue(GameMaps.class, str == null ? ARENA.name() : str);
        return gm == null ? def : gm;
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
