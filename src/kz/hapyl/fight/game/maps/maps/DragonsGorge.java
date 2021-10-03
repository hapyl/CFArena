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
		this.addLocation(-143, 64, 86);
		this.addLocation(-150, 64, 100);
		this.addLocation(-172, 64, 119);
	}
}
