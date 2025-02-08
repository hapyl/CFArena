package me.hapyl.fight.game.maps;

import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.registry.KeyedEnum;
import me.hapyl.eterna.module.util.Enums;
import me.hapyl.fight.Message;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.event.ServerEvents;
import me.hapyl.fight.game.maps.features.CloudFeatures;
import me.hapyl.fight.game.maps.features.japan.JapanFeature;
import me.hapyl.fight.game.maps.features.library.LibraryCat;
import me.hapyl.fight.game.maps.features.library.LibraryVoid;
import me.hapyl.fight.game.maps.gamepack.PackType;
import me.hapyl.fight.game.maps.maps.DragonsGorge;
import me.hapyl.fight.game.maps.maps.DwarfVault;
import me.hapyl.fight.game.maps.maps.moon.MoonBase;
import me.hapyl.fight.game.maps.winery.WineryMap;
import me.hapyl.fight.util.handle.EnumHandleFunction;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WeatherType;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public enum EnumLevel implements Selectable, KeyedEnum {

    // april fools maps (replaces spawn and arena with classic maps)
    SPAWN_APRIL_FOOLS(handle -> new NonPlayableLevel(
            handle,
            "Classic Spawn",
            "A classic spawn from Classes Fight v1.0",
            -500,
            64,
            0,
            -180,
            0f
    )),
    ARENA_APRIL_FOOLS(handle -> new NonPlayableLevel(handle, "Classic Arena", "A classic arena from Classes Fight v1.0", -1000, 64, 0)),

    // non-playable map, storing here for easy coordinate grab and consistency
    TRAINING_GROUNDS(handle -> new NonPlayableLevel(handle, "Training Grounds", "Test heroes talents here!", -1500, 64, 0, -90, 0)),
    SPAWN(handle -> new NonPlayableLevel(handle, "Spawn", "You spawn here!", 0, 64, 0) {
        @Nonnull
        @Override
        public Location getLocation() {
            if (ServerEvents.APRIL_FOOLS.isActive()) {
                return SPAWN_APRIL_FOOLS.getLevel().getLocation();
            }

            return super.getLocation();
        }
    }.setMaterial(Material.NETHER_STAR)),

    // *=* PvP Levels *=* //

    ARENA(handle -> new Level(handle, "Arena") {
        @Nonnull
        @Override
        public Location getLocation() {
            if (ServerEvents.APRIL_FOOLS.isActive()) {
                return ARENA_APRIL_FOOLS.getLevel().getLocation();
            }

            return super.getLocation();
        }
    }
            .setDescription("A great arena to fight on!")
            .setMaterial(Material.COARSE_DIRT)
            .setSize(Size.MEDIUM)
            .setTicksBeforeReveal(100)
            .addLocation(500, 64, 0)
            .addPackLocation(PackType.HEALTH, 479, 77, 22)
            .addPackLocation(PackType.HEALTH, 500, 63, 13)
            .addPackLocation(PackType.HEALTH, 489, 66, -26)
            .addPackLocation(PackType.CHARGE, 516, 63, 8)
            .addPackLocation(PackType.CHARGE, 487, 65, -5)
            .addPackLocation(PackType.CHARGE, 513, 72, -32)
    ),

    JAPAN("Japan", level -> level
            .setDescription("This map is based on real-life temple &e平等院 (Byōdō-in)&7!")
            .setMaterial(Material.CHERRY_SAPLING)
            .setSize(Size.LARGE)
            .setTicksBeforeReveal(160)
            .addFeature(new JapanFeature())
            .addLocation(1000, 64, 6, 180, 0)
    ),

    GREENHOUSE("Greenhouse", level -> level
            .setDescription("This greenhouse has a lot of flowers to hide, and bunch of secret passages.__&8&oWho's made them?")
            .setSize(Size.SMALL)
            .setMaterial(Material.OAK_SAPLING)
            .setTicksBeforeReveal(100)
            .addLocation(1514, 65.1, 0, 90f, 0f)
            .addLocation(1500, 65.1, -14)
            .addLocation(1486, 65, 0, -90f, 0f)
            .addLocation(1500, 65, 14, -180f, 0f)
    ),

    RAILWAY("Railway", level -> level
            .setDescription("The action happening in the unknown Railway Station. Big area to fight, and to hide.")
            .setMaterial(Material.RAIL)
            .setSize(Size.LARGE)
            .setTicksBeforeReveal(120)
            .addLocation(1984.0, 70, 0.0, -90f, 0f)
            .addLocation(2034.0, 70, 0.0, 90f, 0)
    ),

    MIDJOURNEY("Mid Journey", level -> level
            .setDescription("""
                    DiDenPro add description.
                    DiDenPro add description.
                    DiDenPro add description.
                    """)
            .setMaterial(Material.CRIMSON_NYLIUM)
            .setSize(Size.MEDIUM)
            .setTicksBeforeReveal(100)
            .addLocation(2500, 64, 0, -90f, 0f)
            .addLocation(2472, 72, 0, -90f, 0f)
            .addLocation(2518, 70, 0, 90f, 0f)
            .addLocation(2500, 72, 27, -180f, 0f)
            .addLocation(2500, 72, -27)
    ),

    RAILWAY_STATION("Railway Station", level -> level
            .setDescription("{}")
            .setMaterial(Material.POWERED_RAIL)
            .setSize(Size.LARGE)
            .setTicksBeforeReveal(100)
            .addLocation(3000, 64, 0, -90f, 0f)
            .addLocation(3041, 72, 0, 90f, 0f)
            .addLocation(3010, 72, -18)
            .addLocation(3010, 72, -18, -180f, 0f)
            .addLocation(3001, 52, 0, -90f, 0f)
            .addLocation(2981, 76, 0, -90f, 0f)
            .addLocation(3020, 64, 0, -90f, 0f)
            .addPackLocation(PackType.HEALTH, 3045, 64, 3)
            .addPackLocation(PackType.HEALTH, 3045, 64, -11)
            .addPackLocation(PackType.HEALTH, 3010, 71, 0)
            .addPackLocation(PackType.HEALTH, 3010, 72, 21)
            .addPackLocation(PackType.CHARGE, 3045, 64, 11)
            .addPackLocation(PackType.CHARGE, 3010, 72, -35)
    ),

    CLOUDS("The Clouds", level -> level
            .setDescription("Ruined city built on the clouds somewhere in the sky.")
            .setMaterial(Material.WHITE_STAINED_GLASS)
            .setSize(Size.MASSIVE)
            .setTicksBeforeReveal(120)
            .addFeature(new CloudFeatures())
            .addLocation(3500, 64, 0, -180f, 0f)
            .addLocation(3521, 63.5, -17)
            .addLocation(3489, 63, 24, -90f, 0f)
            .addLocation(3547, 68, -11, 90f, 0f)
            .addPackLocation(PackType.HEALTH, 3521, 60, -17)
            .addPackLocation(PackType.HEALTH, 3523, 54, -46)
            .addPackLocation(PackType.HEALTH, 3504, 88, -69)
            .addPackLocation(PackType.CHARGE, 3502, 51, -13)
            .addPackLocation(PackType.CHARGE, 3525, 73, -12)
    ),

    LIBRARY("Infinity Library", level -> level
            .setDescription("A library that stuck in the void.")
            .setMaterial(Material.BOOKSHELF)
            .setSize(Size.MEDIUM)
            .setTicksBeforeReveal(100)
            .addFeature(new LibraryVoid())
            .addFeature(new LibraryCat())
            .addLocation(4000, 64.1, 0, -180f, 0f)
            .addLocation(3991, 74, 5, -180f, 0f)
            .addLocation(4018, 74, -7, 90f, 0f)
            .addPackLocation(PackType.HEALTH, 3990, 65, -15)
            .addPackLocation(PackType.HEALTH, 4015, 67, -4)
            .addPackLocation(PackType.HEALTH, 4000, 72, -29)
            .addPackLocation(PackType.HEALTH, 3991, 74, -13)
            .addPackLocation(PackType.CHARGE, 4013, 66.5, -14)
            .addPackLocation(PackType.CHARGE, 3960, 75, 5)
    ),

    DRAGONS_GORGE(DragonsGorge::new), // complex map, stored in separate file
    WINERY(WineryMap::new), // complex map, stored in separate file
    MOON_BASE(MoonBase::new), // complex map, stored in separate file
    DWARF_VAULT(DwarfVault::new),
    LIMBO(LimboMap::new),

    FORGOTTEN_CHURCH("Forgotten Church", level -> level
            .setDescription("""
                    All forgotten church covered in snow.
                    
                    A long time has passed since it last seen guests.
                    """)
            .setMaterial(Material.TOTEM_OF_UNDYING)
            .setSize(Size.MEDIUM)
            .setWeather(WeatherType.DOWNFALL)
            .setTicksBeforeReveal(100)
            .addLocation(7000, 64, 0)
    ),

    // *=* Commission Levels *=* //

    ;

    private final Level level;

    EnumLevel(@Nonnull String name, @Nonnull EnumHandleFunction.Handler<EnumLevel, Level> handler) {
        this(e -> handler.apply(new Level(e, name)));
    }

    EnumLevel(@Nonnull EnumHandleFunction<EnumLevel, Level> function) {
        this.level = function.apply(this);
    }

    public boolean isPlayable() {
        return level.isPlayable();
    }

    public Level getLevel() {
        return level;
    }

    @Override
    public boolean isSelected(@Nonnull Player player) {
        return Manager.current().getCurrentMap() == this;
    }

    @Override
    public void select(@Nonnull Player player) {
        if (Manager.current().getCurrentMap() == this) {
            Message.error(player, "This map is already selected!");
            return;
        }

        Manager.current().setCurrentMap(this);

        Chat.broadcast("&2&lMAP! &a%s selected &l%s&a!".formatted(player.getName(), getName()));
    }

    @Nonnull
    public String getName() {
        return level.getName();
    }

    public static List<EnumLevel> getPlayableMaps() {
        final List<EnumLevel> maps = new ArrayList<>();

        for (final EnumLevel value : values()) {
            if (value == null || !value.isPlayable()) {
                continue;
            }
            maps.add(value);
        }
        return maps;
    }

    public static EnumLevel byName(String str, EnumLevel def) {
        final EnumLevel gm = Enums.byName(EnumLevel.class, str == null ? ARENA.name() : str);
        return gm == null ? def : gm;
    }


}
