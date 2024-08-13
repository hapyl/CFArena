package me.hapyl.fight.game.maps;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.eterna.module.util.CollectionUtils;
import me.hapyl.fight.CF;
import me.hapyl.fight.annotate.AutoRegisteredListener;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.element.ElementHandler;
import me.hapyl.fight.game.element.PlayerElementHandler;
import me.hapyl.fight.game.gamemode.Modes;
import me.hapyl.fight.game.maps.gamepack.ChangePack;
import me.hapyl.fight.game.maps.gamepack.GamePack;
import me.hapyl.fight.game.maps.gamepack.HealthPack;
import me.hapyl.fight.game.maps.gamepack.PackType;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.handle.EnumHandle;
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
public class Level implements ElementHandler, PlayerElementHandler, EnumHandle<EnumLevel> {

    private final String name;
    private final EnumLevel handle;

    private final List<PredicateLocation> locations;
    private final Map<PackType, GamePack> gamePacks;
    private final List<LevelFeature> features;
    private final Set<Modes> allowedModes;

    private int ticksBeforeReveal;
    private int mapTime;
    private Material material;
    private Size size;
    private String description;
    private WeatherType weatherType;

    private boolean isPlayable;

    protected Level(@Nonnull EnumLevel handle, @Nonnull String name) {
        this.handle = handle;
        this.name = name;
        this.material = Material.BEDROCK;
        this.description = "No description.";
        this.mapTime = 6000;
        this.weatherType = WeatherType.CLEAR;
        this.locations = Lists.newArrayList();
        this.features = Lists.newArrayList();
        this.allowedModes = Sets.newHashSet();
        this.size = Size.SMALL;
        this.ticksBeforeReveal = 0;
        this.isPlayable = true;

        // init game packs
        this.gamePacks = Maps.newHashMap();
        this.gamePacks.put(PackType.HEALTH, new HealthPack());
        this.gamePacks.put(PackType.CHARGE, new ChangePack());

        if (this instanceof Listener listener) {
            CF.registerEvents(listener);
        }
    }
    public boolean isPlayable() {
        return isPlayable;
    }

    public Level setPlayable(boolean playable) {
        isPlayable = playable;
        return this;
    }

    public int getTime() {
        return mapTime;
    }

    public Level setTime(int time) {
        this.mapTime = time;
        return this;
    }

    public Level setTime(@Nonnull MinecraftTime time) {
        return setTime(time.time);
    }

    public WeatherType getWeather() {
        return weatherType;
    }

    public Level setWeather(WeatherType weather) {
        this.weatherType = weather;
        return this;
    }

    public Set<Modes> getAllowedModes() {
        return allowedModes;
    }

    public Level addAllowedMode(Modes mode) {
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

    public Level setMaterial(Material material) {
        this.material = material;
        return this;
    }

    @Nonnull
    public List<LevelFeature> getFeatures() {
        return features;
    }

    @Nonnull
    public List<LevelFeature> getNonHiddenFeatures() {
        final List<LevelFeature> list = Lists.newArrayList();

        for (LevelFeature feature : features) {
            if (feature instanceof HiddenLevelFeature) {
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
        for (LevelFeature feature : features) {
            if (feature instanceof HiddenLevelFeature) {
                continue;
            }

            notHiddenCount++;
        }

        return notHiddenCount > 0;
    }

    public Level addFeature(LevelFeature feature) {
        this.features.add(feature);
        return this;
    }

    public Level addFeature(List<LevelFeature> feature) {
        this.features.addAll(feature);
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Level setDescription(String info) {
        this.description = info;
        return this;
    }

    public Size getSize() {
        return size;
    }

    public Level setSize(Size size) {
        this.size = size;
        return this;
    }

    public Level addLocation(double x, double y, double z) {
        return addLocation(x, y, z, 0.0f, 0.0f);
    }

    public Level addLocation(double x, double y, double z, float yaw, float pitch) {
        return addLocation(createLocation(x, y, z, yaw, pitch));
    }

    public Level addLocation(double x, double y, double z, float yaw, float pitch, Predicate<Level> predicate) {
        this.locations.add(new PredicateLocation(createLocation(x, y, z, yaw, pitch), predicate));
        return this;
    }

    public Level addLocation(double x, double y, double z, Predicate<Level> predicate) {
        return addLocation(x, y, z, 0.0f, 0.0f, predicate);
    }

    public Level addLocation(Location location) {
        this.locations.add(new PredicateLocation(location));
        return this;
    }

    public Level addLocation(Location location, Predicate<Level> predicate) {
        this.locations.add(new PredicateLocation(location, predicate));
        return this;
    }

    public boolean hasLocation() {
        return !locations.isEmpty();
    }

    public String getName() {
        return name;
    }

    public Level setTicksBeforeReveal(int tick) {
        this.ticksBeforeReveal = tick;
        return this;
    }

    /**
     * Returns a random location.
     *
     * @return a random location.
     */
    @Nonnull
    public Location getLocation() {
        if (locations.size() == 1) {
            return locations.getFirst().getLocation();
        }

        // Inspired by NASA! (real)
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
    public void onStart(@Nonnull GameInstance instance) {
        // Set map time
        final World world = getLocation().getWorld();

        if (world != null) {
            world.setTime(mapTime);
            world.setStorm(weatherType == WeatherType.DOWNFALL);
        }

        gamePacks.values().forEach(GamePack::onStart);

        // Features
        if (!features.isEmpty()) {
            features.forEach(LevelFeature::onStart);

            new GameTask() {
                private int tick = 0;

                @Override
                public void run() {
                    features.forEach(feature -> feature.tick(tick));
                    ++tick;
                }
            }.runTaskTimer(0, 1);
        }

    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void onStop(@Nonnull GameInstance instance) {
        gamePacks.values().forEach(GamePack::onStop);
        features.forEach(LevelFeature::onStop);
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void onPlayersRevealed(@Nonnull GameInstance instance) {
        gamePacks.values().forEach(GamePack::activatePacks);
    }

    public Level addPackLocation(PackType type, double x, double y, double z) {
        if (!gamePacks.containsKey(type)) {
            throw new IllegalStateException("game pack %s not initiated?".formatted(type.name()));
        }

        gamePacks.get(type).addLocation(BukkitUtils.defLocation(x + 0.5, y, z + 0.5));
        return this;
    }

    public Collection<GamePack> getGamePacks() {
        return gamePacks.values();
    }

    @Nonnull
    @Override
    public EnumLevel getHandle() {
        return handle;
    }

    private Location createLocation(double x, double y, double z, float yaw, float pitch) {
        return new Location(Bukkit.getWorlds().getFirst(), Math.floor(x) + 0.5d, y, Math.floor(z) + 0.5d, yaw, pitch);
    }
}
