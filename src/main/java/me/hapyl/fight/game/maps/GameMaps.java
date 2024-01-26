package me.hapyl.fight.game.maps;

import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.maps.features.*;
import me.hapyl.fight.game.maps.gamepack.PackType;
import me.hapyl.fight.game.maps.maps.DragonsGorge;
import me.hapyl.fight.game.maps.maps.DwarfVault;
import me.hapyl.fight.game.maps.maps.MoonBase;
import me.hapyl.fight.game.maps.winery.WineryMap;
import me.hapyl.fight.translate.Translatable;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.util.Validate;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public enum GameMaps implements Selectable, Translatable {

    // non-playable map, storing here for easy coordinate grab and consistency
    TRAINING_GROUNDS(new NonPlayableGameMap("Training Grounds", "Test heroes abilities here!", -250, 64, 250, -90, 0)),
    SPAWN(new NonPlayableGameMap("Spawn", "You spawn here!", 0, 64, 0).setMaterial(Material.NETHER_STAR)),

    // april fools maps (replaces spawn and arena with classic maps)
    ARENA_APRIL_FOOLS(new NonPlayableGameMap("Classic Arena", "A classic arena from Classes Fight v1.0", -900, 63, 0, 180.0f, 0.0f)),
    SPAWN_APRIL_FOOLS(new NonPlayableGameMap("Classic Spawn", "A classic spawn from Classes Fight v1.0", -1000, 63, 0)),

    // == Playable Maps Separator ==

    ARENA(
            new GameMap("Arena")
                    .setDescription("A great arena to fight on!")
                    .setMaterial(Material.COARSE_DIRT)
                    .setSize(Size.MEDIUM)
                    .setTicksBeforeReveal(100)
                    .addLocation(100, 64, 0)
                    .addPackLocation(PackType.HEALTH, 79.5, 77.0, 22.5)
                    .addPackLocation(PackType.HEALTH, 100.5, 63.0, 13.5)
                    .addPackLocation(PackType.HEALTH, 89.5, 66.0, -24.5)
                    .addPackLocation(PackType.CHARGE, 116.5, 63.0, 8.5)
                    .addPackLocation(PackType.CHARGE, 87.5, 65.0, -4.5)
                    .addPackLocation(PackType.CHARGE, 112.5, 72.0, -29.5)
    ),

    JAPAN(
            new GameMap("Japan")
                    .setDescription("This map is based on real-life temple &e平等院 (Byōdō-in)&7!")
                    .setMaterial(Material.CHERRY_SAPLING)
                    .setSize(Size.LARGE)
                    .setTicksBeforeReveal(160)
                    .addFeature(new JapanFeature())
                    .addLocation(-492, 64, 6, 180, 0)
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
                    .addLocation(306, 66, -200)
                    .addLocation(319, 73, -200)
                    .addLocation(273, 75, -200)
    ),

    RAILWAY_STATION(
            new GameMap("Railway Station")
                    .setDescription("{}")
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
                    .addPackLocation(PackType.HEALTH, -20.5, 76.0, 199.5)
                    .addPackLocation(PackType.HEALTH, 10.5, 71.0, 200.5)
                    .addPackLocation(PackType.HEALTH, 10.5, 72.0, 222.5)
                    .addPackLocation(PackType.HEALTH, 45.5, 64.0, 203.5)
                    .addPackLocation(PackType.CHARGE, 10.5, 72.0, 169.5)
                    .addPackLocation(PackType.CHARGE, 45.5, 64.0, 211.5)
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
                    .addFeature(new LibraryVoid())
                    .addFeature(new LibraryCat())
                    .addLocation(0, 64, -90, -180, 0)
                    .addLocation(-10, 74, -95, -180, 0)
                    .addLocation(9, 74, -95, -180, 0)
                    .addPackLocation(PackType.HEALTH, -9.5, 74.0, -113.5)
                    .addPackLocation(PackType.HEALTH, -10.5, 65.0, -113.5)
                    .addPackLocation(PackType.HEALTH, 15.5, 67.0, -104.5)
                    .addPackLocation(PackType.HEALTH, 0.5, 72.0, -123.5)
                    .addPackLocation(PackType.CHARGE, -8.5, 74.0, -93.5)
                    .addPackLocation(PackType.CHARGE, 13.5, 66.0, -113.5)
                    .addPackLocation(PackType.CHARGE, 18.5, 74.0, -115.5)
    ),

    DRAGONS_GORGE(new DragonsGorge()), // complex map, stored in separate file
    WINERY(new WineryMap()), // complex map, stored in separate file
    MOON_BASE(new MoonBase()), // complex map, stored in separate file
    DWARF_VAULT(new DwarfVault()),
    LIMBO(
            new GameMap("Limbo")
                    .setDescription("Yes, no?")
                    .setMaterial(Material.SCULK_VEIN)
                    .setSize(Size.LARGE)
                    .setTicksBeforeReveal(100)
                    .setTime(18000)
                    .addFeature(new LimboFeature())
                    .addLocation(-831, 68, -237)
                    .addLocation(-830, 47, -267)
                    .addLocation(-830, 47, -267)
                    .addLocation(-832, 112, -218)
    ),

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

    @Override
    public boolean isSelected(@Nonnull Player player) {
        return Manager.current().getCurrentMap() == this;
    }

    @Nonnull
    @Override
    public String getParentTranslatableKey() {
        return "map." + name().toLowerCase() + ".";
    }

    @Override
    public void select(@Nonnull Player player) {
        Manager.current().setCurrentMap(this);

        Chat.broadcast("&2&lMAP! &a%s selected &l%s&a!", player.getName(), getName());
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

    @Nonnull
    public String getName() {
        return map.getName();
    }

    public static GameMaps byName(String str, GameMaps def) {
        final GameMaps gm = Validate.getEnumValue(GameMaps.class, str == null ? ARENA.name() : str);
        return gm == null ? def : gm;
    }


}
