package me.hapyl.fight.game.maps;

import me.hapyl.fight.game.GameElement;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Final;
import me.hapyl.spigotutils.module.util.CollectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GameMap implements GameElement {

    private final String name;
    private final Material material;
    private final List<Location> locations;
    private final List<MapFeature> features;
    private final int ticksBeforeReveal;

    private final Final<Size> size;
    private String info;

    public GameMap(String name, Material material, int ticksBeforeReveal) {
        this(name, "", material, ticksBeforeReveal);
    }

    public GameMap(String name, String info, Material material, int ticksBeforeReveal) {
        this.name = name;
        this.material = material;
        this.info = info;
        this.locations = new ArrayList<>();
        this.features = new ArrayList<>();
        this.size = new Final<>();
        this.ticksBeforeReveal = ticksBeforeReveal;
    }

    public int getTimeBeforeReveal() {
        return ticksBeforeReveal;
    }

    public Material getMaterial() {
        return material;
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

	public String getInfo() {
		return info;
	}

	public GameMap setSize(Size size) {
		this.size.set(size);
		return this;
	}

	public Size getSize() {
		return size.getOr(Size.SMALL);
	}

	public void setInfo(String info) {
		this.info = info;
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

	public Location getLocation() {
		return CollectionUtils.randomElement(locations, locations.get(0));
	}

	@Override
	public final void onStart() {
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
		features.forEach(MapFeature::onStop);
	}

}
