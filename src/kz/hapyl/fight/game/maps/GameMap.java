package kz.hapyl.fight.game.maps;

import kz.hapyl.fight.game.GameElement;
import kz.hapyl.fight.game.task.GameTask;
import kz.hapyl.fight.util.Final;
import kz.hapyl.spigotutils.module.util.CollectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import javax.annotation.Nullable;
import java.util.*;

public class GameMap implements GameElement {

	private final String name;
	private final Material material;
	private final List<Location> locations;
	private final Set<MapFeature> features;

	private final Final<Size> size;
	private String info;

	public GameMap(String name, Material material) {
		this(name, "", material);
	}

	public GameMap(String name, String info, Material material, Size size, @Nullable List<Location> locations, @Nullable Set<MapFeature> features) {
		this(name, info, material);
		this.setSize(size);
		if (locations != null) {
			for (final Location location : locations) {
				addLocation(location);
			}
		}
		if (features != null) {
			for (final MapFeature feature : features) {
				addFeature(feature);
			}
		}
	}

	public GameMap(String name, String info, Material material) {
		this.name = name;
		this.material = material;
		this.info = info;
		this.locations = new ArrayList<>();
		this.features = new HashSet<>();
		this.size = new Final<>();
	}

	public GameMap addFeature(MapFeature feature) {
		this.features.add(feature);
		return this;
	}

	public GameMap addFeature(Set<MapFeature> feature) {
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
		return addLocation(new Location(Bukkit.getWorlds().get(0), x, y, z));
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
		new GameTask() {
			private int tick = 0;

			@Override
			public void run() {
				final int tickMod20 = this.tick % 20;

				features.forEach(feature -> feature.tick(tickMod20));
				++tick;

			}
		}.runTaskTimer(0, 1);
	}

	@Override
	public final void onStop() {

	}

}
