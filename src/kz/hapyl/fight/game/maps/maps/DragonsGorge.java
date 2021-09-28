package kz.hapyl.fight.game.maps.maps;

import kz.hapyl.fight.game.maps.GameMap;
import kz.hapyl.fight.game.maps.MapFeature;
import kz.hapyl.fight.game.maps.Size;
import org.bukkit.Material;

public class DragonsGorge extends GameMap {
	public DragonsGorge() {
		super("Dragon's Gorge", Material.DARK_OAK_BOAT);
		this.setInfo("...");
		this.setSize(Size.MEDIUM);
		this.addFeature(new MapFeature("Sheer Cold", "This water is so cold! Better keep an eye on your cold-o-meter!") {
			@Override
			public void tick(int tick) {

			}
		});
		this.addLocation(-184, 20, 154);
		this.addLocation(-199, 18, 140);
		this.addLocation(-223, 18, 173);
	}
}
