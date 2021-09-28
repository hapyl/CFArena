package kz.hapyl.fight.game.maps.maps;

import kz.hapyl.fight.game.maps.GameMap;
import kz.hapyl.fight.game.maps.MapFeature;
import kz.hapyl.fight.game.maps.Size;
import org.bukkit.Material;

public class JapanMap extends GameMap {
	public JapanMap() {
		super("Japan", "This map based on real-life temple &e平等院 (Byōdō-in)&7!", Material.PINK_GLAZED_TERRACOTTA);
		this.setSize(Size.LARGE);
		this.addFeature(new MapFeature("Healing Sakura", "Stand inside &eSakura's &7range to feel it's healing petals!") {
			@Override
			public void tick(int tick) {

			}
		});
		this.addLocation(106, 12, -115);
	}
}
