package kz.hapyl.fight.game.cosmetic;

import org.bukkit.Location;

public enum Cosmetics {

	BLOOD(new Cosmetic("Blood", "Displays blood.", 150, Type.KILL) {
		@Override
		public void display(Location location) {

		}
	});

	private final Cosmetic cosmetic;

	Cosmetics(Cosmetic cosmetic) {
		this.cosmetic = cosmetic;
	}
}
