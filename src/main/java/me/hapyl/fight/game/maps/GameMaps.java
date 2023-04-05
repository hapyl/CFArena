package me.hapyl.fight.game.maps;

import me.hapyl.fight.game.maps.features.CloudFeatures;
import me.hapyl.fight.game.maps.features.JapanFeature;
import me.hapyl.fight.game.maps.features.LibraryCat;
import me.hapyl.fight.game.maps.features.LibraryFeature;
import me.hapyl.fight.game.maps.maps.DragonsGorge;
import me.hapyl.fight.game.maps.maps.WineryMap;
import me.hapyl.spigotutils.module.util.Validate;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public enum GameMaps {

    // non-playable map, storing here for easy coordinate grab and consistency
    TRAINING_GROUNDS(new NonPlayableGameMap("Training Grounds", "Test heroes abilities here!", 100, 64, 100)),
    SPAWN(new NonPlayableGameMap("Spawn", "You spawn here!", 0, 64, 0)),

    // april fools maps (replaces spawn and arena with classic maps)
    ARENA_APRIL_FOOLS(new NonPlayableGameMap("Classic Arena", "A classic arena from Classes Fight v1.0", -900, 63, 0)),
    SPAWN_APRIL_FOOLS(new NonPlayableGameMap("Classic Spawn", "A classic spawn from Classes Fight v1.0", -1000, 63, 0)),

    // == Playable Maps Separator ==

    ARENA(
            new GameMap("Arena")
                    .setDescription("A great arena to fight on!")
                    .setMaterial(Material.COARSE_DIRT)
                    .setSize(Size.MEDIUM)
                    .setTicksBeforeReveal(100)
                    .addLocation(100, 64, 0)
    ),

    JAPAN(
            new GameMap("Japan")
                    .setDescription("This map based on real-life temple &e平等院 (Byōdō-in)&7!")
                    .setMaterial(Material.PINK_GLAZED_TERRACOTTA)
                    .setSize(Size.LARGE)
                    .setTicksBeforeReveal(160)
                    .addFeature(new JapanFeature())
                    .addLocation(300, 64, 0, 180, 0)
    ),

    GREENHOUSE(
            new GameMap("Greenhouse")
                    .setDescription("This greenhouse has a lot of flowers to hide, and bunch of secret passages.__&8&oWho's made them?")
                    .setSize(Size.SMALL)
                    .setMaterial(Material.OAK_SAPLING)
                    .setTicksBeforeReveal(100)
                    .addLocation(-99, 64, -6)
    ),

    RAILWAY(
            new GameMap("Railway")
                    .setDescription("The action happening in the unknown Railway Station. Big area to fight, and to hide.")
                    .setMaterial(Material.RAIL)
                    .setSize(Size.LARGE)
                    .setTicksBeforeReveal(120)
                    .addLocation(32, 70, 99)
                    .addLocation(-16, 70, 99)
    ),

    MIDJOURNEY(
            new GameMap("Mid Journey")
                    .setDescription("Mid Journey is a fierce and treacherous arena located deep within the Nether.____" +
                            "Its jagged walls and pillars of obsidian provide cover and obstacles for players " +
                            "to use to their advantage as they battle for supremacy in this dangerous realm.")
                    .setMaterial(Material.CRIMSON_NYLIUM)
                    .setSize(Size.MEDIUM)
                    .setTicksBeforeReveal(100)
                    .addLocation(306, 66.0, -200)
                    .addLocation(319, 73.0, -200)
                    .addLocation(273, 75.0, -200)
    ),

    RAILWAY_STATION(
            new GameMap("Railway Station")
                    .setDescription(
                            "You arrive at the railway station, greeted by a grand fountain and bustling travelers. The station features a cafe, a library, and a scene for music lovers. Descending the stairs, you find the underground platform, ready to take you on your next adventure."
                    )
                    .setMaterial(Material.POWERED_RAIL)
                    .setSize(Size.LARGE)
                    .setTicksBeforeReveal(100)
                    .addLocation(10, 72, 217)
                    .addLocation(10, 72, 184)
                    .addLocation(20, 64, 200)
                    .addLocation(-2, 64, 200)
                    .addLocation(41, 72, 200)
                    .addLocation(-19, 76, 200)
                    .addLocation(2, 52, 200)
    ),

    CLOUDS(
            new GameMap("The Clouds")
                    .setDescription("Ruined city built on the clouds somewhere in the sky.")
                    .setMaterial(Material.WHITE_STAINED_GLASS)
                    .setSize(Size.MASSIVE)
                    .setTicksBeforeReveal(120)
                    .addFeature(new CloudFeatures())
                    .addLocation(500, 64, 500)
    ),

    LIBRARY(
            new GameMap("Infinity Library")
                    .setDescription("A library that stuck in the void.")
                    .setMaterial(Material.BOOKSHELF)
                    .setSize(Size.MEDIUM)
                    .setTicksBeforeReveal(100)
                    .addFeature(new LibraryFeature())
                    .addFeature(new LibraryCat())
                    .addLocation(0, 64, -90, -180, 0)
                    .addLocation(-10, 74, -95, -180, 0)
                    .addLocation(9, 74, -95, -180, 0)
    ),

    DRAGONS_GORGE(new DragonsGorge()), // complex map, stored in separate file

    WINERY(new WineryMap()), // complex map, stored in separate file

    ;

    private final GameMap map;

    GameMaps(GameMap map) {
        this.map = map;
    }

    public boolean isPlayable() {
        return map.isPlayable();
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

}
