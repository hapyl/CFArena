package me.hapyl.fight.game.maps;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.hapyl.fight.game.GameElement;
import me.hapyl.fight.game.PlayerElement;
import me.hapyl.fight.game.ServerEvent;
import me.hapyl.fight.game.gamemode.Modes;
import me.hapyl.fight.game.maps.healthpack.ChangePack;
import me.hapyl.fight.game.maps.healthpack.GamePack;
import me.hapyl.fight.game.maps.healthpack.HealthPack;
import me.hapyl.fight.game.maps.healthpack.PackType;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import me.hapyl.spigotutils.module.util.CollectionUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GameMap implements GameElement, PlayerElement {

    private final String name;
    private final List<Location> locations;
    private final List<Location> hordeSpawnLocations;
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
        this.hordeSpawnLocations = Lists.newArrayList();
        this.size = Size.SMALL;
        this.ticksBeforeReveal = ticksBeforeReveal;
        this.isPlayable = true;

        // init game packs
        this.gamePacks = Maps.newHashMap();
        this.gamePacks.put(PackType.HEALTH, new HealthPack());
        this.gamePacks.put(PackType.CHARGE, new ChangePack());
    }

    @Override
    public void onDeath(Player player) {
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

    public WeatherType getWeather() {
        return weatherType;
    }

    public void setWeather(WeatherType weather) {
        this.weatherType = weather;
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

    public List<Location> getHordeSpawnLocations() {
        return hordeSpawnLocations;
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

    public List<Location> getLocations() {
        return locations;
    }

    public List<MapFeature> getFeatures() {
        return features;
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

    public GameMap addHordeLocation(double x, double y, double z) {
        hordeSpawnLocations.add(new Location(Bukkit.getWorlds().get(0), x + 0.5d, y, z + 0.5d));
        return this;
    }

    public GameMap addLocation(double x, double y, double z) {
        return addLocation(x, y, z, 0.0f, 0.0f);
    }

    public GameMap addLocation(double x, double y, double z, float a, float b) {
        return addLocation(new Location(Bukkit.getWorlds().get(0), x + 0.5d, y, z + 0.5d, a, b));
    }

    public GameMap addLocation(Location location) {
        this.locations.add(location);
        return this;
    }

    public GameMap addLocation(Collection<Location> location) {
        this.locations.addAll(location);
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
     * Returns first or random location.
     *
     * @return first or random location.
     */
    public Location getLocation() {
        // FIXME (hapyl): 005, Apr 5, 2023: Hardcoding for now, because don't want to rework the whole system for a joke
        if (ServerEvent.isAprilFools()) {
            if (name.equalsIgnoreCase("arena")) {
                return GameMaps.ARENA_APRIL_FOOLS.getMap().getLocation();
            }
            else if (name.equalsIgnoreCase("spawn")) {
                return GameMaps.SPAWN_APRIL_FOOLS.getMap().getLocation();
            }
        }

        return CollectionUtils.randomElement(locations, locations.get(0));
    }

    @Override
    public final void onStart() {
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
                final int tickMod20 = tick % 20;

                features.forEach(feature -> feature.tick(tickMod20));
                ++tick;
            }
        }.runTaskTimer(0, 1);
    }

    @Override
    public final void onStop() {
        gamePacks.values().forEach(GamePack::onStop);
        features.forEach(MapFeature::onStop);
    }

    @Override
    public final void onPlayersReveal() {
        gamePacks.values().forEach(GamePack::onPlayersReveal);
        features.forEach(MapFeature::onPlayersReveal);
    }

    public GameMap addPackLocation(PackType type, double x, double y, double z) {
        if (!gamePacks.containsKey(type)) {
            throw new IllegalStateException("game pack %s no initiated?".formatted(type.name()));
        }

        gamePacks.get(type).addLocation(BukkitUtils.defLocation(x, y, z));
        return this;
    }

    public Collection<GamePack> getGamePacks() {
        return gamePacks.values();
    }
}
