package me.hapyl.fight.game.maps;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.hapyl.fight.CF;
import me.hapyl.fight.annotate.AutoRegisteredListener;
import me.hapyl.fight.game.GameElement;
import me.hapyl.fight.game.PlayerElement;
import me.hapyl.fight.game.StaticServerEvent;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.gamemode.Modes;
import me.hapyl.fight.game.maps.gamepack.ChangePack;
import me.hapyl.fight.game.maps.gamepack.GamePack;
import me.hapyl.fight.game.maps.gamepack.HealthPack;
import me.hapyl.fight.game.maps.gamepack.PackType;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import me.hapyl.spigotutils.module.util.CollectionUtils;
import org.bukkit.*;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

@AutoRegisteredListener
public class GameMap implements GameElement, PlayerElement {

    private final String name;

    private final List<PredicateLocation> locations;
    private final Map<PackType, GamePack> gamePacks;
    private final List<MapFeature> features;
    private final Set<Modes> allowedModes;

    private int ticksBeforeReveal;
    private int mapTime;
    private Material material;
    private Size size;
    private String description;
    private WeatherType weatherType;

    private boolean isPlayable;

    protected GameMap(String name) {
        this(name, Material.BEDROCK, 0);
    }

    private GameMap(String name, Material material, int ticksBeforeReveal) {
        this(name, "No Description Provided", material, ticksBeforeReveal);
    }

    private GameMap(String name, String info, Material material, int ticksBeforeReveal) {
        this.name = name;
        this.material = material;
        this.description = info;
        this.mapTime = 6000;
        this.weatherType = WeatherType.CLEAR;
        this.locations = Lists.newArrayList();
        this.features = Lists.newArrayList();
        this.allowedModes = Sets.newHashSet();
        this.size = Size.SMALL;
        this.ticksBeforeReveal = ticksBeforeReveal;
        this.isPlayable = true;

        // init game packs
        this.gamePacks = Maps.newHashMap();
        this.gamePacks.put(PackType.HEALTH, new HealthPack());
        this.gamePacks.put(PackType.CHARGE, new ChangePack());

        if (this instanceof Listener listener) {
            CF.registerEvents(listener);
        }
    }

    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        for (MapFeature feature : getFeatures()) {
            feature.onDeath(player);
        }
    }

    public boolean isPlayable() {
        return isPlayable;
    }

    public GameMap setPlayable(boolean playable) {
        isPlayable = playable;
        return this;
    }

    public int getTime() {
        return mapTime;
    }

    public GameMap setTime(int time) {
        this.mapTime = time;
        return this;
    }

    public GameMap setTime(@Nonnull MinecraftTime time) {
        return setTime(time.time);
    }

    public WeatherType getWeather() {
        return weatherType;
    }

    public GameMap setWeather(WeatherType weather) {
        this.weatherType = weather;
        return this;
    }

    public Set<Modes> getAllowedModes() {
        return allowedModes;
    }

    public GameMap addAllowedMode(Modes mode) {
        this.allowedModes.add(mode);
        return this;
    }

    public boolean isAllowedMode(Modes mode) {
        return this.allowedModes.isEmpty() || this.allowedModes.contains(mode);
    }

    public int getTimeBeforeReveal() {
        return ticksBeforeReveal;
    }

    public Material getMaterial() {
        return material;
    }

    public GameMap setMaterial(Material material) {
        this.material = material;
        return this;
    }

    @Nonnull
    public List<MapFeature> getFeatures() {
        return features;
    }

    @Nonnull
    public List<MapFeature> getNonHiddenFeatures() {
        final List<MapFeature> list = Lists.newArrayList();

        for (MapFeature feature : features) {
            if (feature instanceof HiddenMapFeature) {
                continue;
            }

            list.add(feature);
        }

        return list;
    }

    public boolean hasFeatures() {
        if (features.isEmpty()) {
            return false;
        }

        int notHiddenCount = 0;
        for (MapFeature feature : features) {
            if (feature instanceof HiddenMapFeature) {
                continue;
            }

            notHiddenCount++;
        }

        return notHiddenCount > 0;
    }

    public GameMap addFeature(MapFeature feature) {
        this.features.add(feature);
        return this;
    }

    public GameMap addFeature(List<MapFeature> feature) {
        this.features.addAll(feature);
        return this;
    }

    public String getDescription() {
        return description;
    }

    public GameMap setDescription(String info) {
        this.description = info;
        return this;
    }

    public Size getSize() {
        return size;
    }

    public GameMap setSize(Size size) {
        this.size = size;
        return this;
    }

    public GameMap addLocation(double x, double y, double z) {
        return addLocation(x, y, z, 0.0f, 0.0f);
    }

    public GameMap addLocation(double x, double y, double z, float yaw, float pitch) {
        return addLocation(createLocation(x, y, z, yaw, pitch));
    }

    public GameMap addLocation(double x, double y, double z, float yaw, float pitch, Predicate<GameMap> predicate) {
        this.locations.add(new PredicateLocation(createLocation(x, y, z, yaw, pitch), predicate));
        return this;
    }

    public GameMap addLocation(double x, double y, double z, Predicate<GameMap> predicate) {
        return addLocation(x, y, z, 0.0f, 0.0f, predicate);
    }

    public GameMap addLocation(Location location) {
        this.locations.add(new PredicateLocation(location));
        return this;
    }

    public GameMap addLocation(Location location, Predicate<GameMap> predicate) {
        this.locations.add(new PredicateLocation(location, predicate));
        return this;
    }

    public boolean hasLocation() {
        return !locations.isEmpty();
    }

    public String getName() {
        return name;
    }

    public GameMap setTicksBeforeReveal(int tick) {
        this.ticksBeforeReveal = tick;
        return this;
    }

    /**
     * Returns a random location.
     *
     * @return a random location.
     */
    @SuppressWarnings("unchecked")
    @Nonnull
    public final Location getLocation() {
        // FIXME (hapyl): 005, Apr 5, 2023: Hardcoding for now, because don't want to rework the whole system for a joke
        if (StaticServerEvent.isAprilFools()) {
            if (name.equalsIgnoreCase("arena")) {
                return GameMaps.ARENA_APRIL_FOOLS.getMap().getLocation();
            }
            else if (name.equalsIgnoreCase("spawn")) {
                return GameMaps.SPAWN_APRIL_FOOLS.getMap().getLocation();
            }
        }

        int tries = 0;
        while (tries++ < Byte.MAX_VALUE) {
            final PredicateLocation predicateLocation = CollectionUtils.randomElement(locations, locations.get(0));

            if (predicateLocation.predicate(this)) {
                return predicateLocation.getLocation();
            }
        }

        return BukkitUtils.defLocation(0, 0, 0);
    }

    @Nonnull
    public World getWorld() {
        final PredicateLocation location = locations.get(0);
        final World world = location.getLocation().getWorld();

        if (world == null) {
            throw new IllegalStateException("perhaps loading the world would help");
        }

        return world;
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void onStart() {
        // Set map time
        final World world = getLocation().getWorld();

        if (world != null) {
            world.setTime(mapTime);
            world.setStorm(weatherType == WeatherType.DOWNFALL);
        }

        gamePacks.values().forEach(GamePack::onStart);

        // Features \/
        if (features.isEmpty()) {
            return;
        }

        features.forEach(MapFeature::onStart);

        new GameTask() {
            private int tick = 0;

            @Override
            public void run() {
                features.forEach(feature -> feature.tick(tick));
                ++tick;
            }
        }.runTaskTimer(0, 1);
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void onStop() {
        gamePacks.values().forEach(GamePack::onStop);
        features.forEach(MapFeature::onStop);
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void onPlayersRevealed() {
        gamePacks.values().forEach(GamePack::onPlayersRevealed);
        features.forEach(MapFeature::onPlayersRevealed);
    }

    public GameMap addPackLocation(PackType type, double x, double y, double z) {
        if (!gamePacks.containsKey(type)) {
            throw new IllegalStateException("game pack %s not initiated?".formatted(type.name()));
        }

        gamePacks.get(type).addLocation(BukkitUtils.defLocation(x, y, z));
        return this;
    }

    public Collection<GamePack> getGamePacks() {
        return gamePacks.values();
    }

    private Location createLocation(double x, double y, double z, float yaw, float pitch) {
        return new Location(Bukkit.getWorlds().get(0), Math.floor(x) + 0.5d, y, Math.floor(z) + 0.5d, yaw, pitch);
    }
}
