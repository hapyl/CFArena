package kz.hapyl.fight.game.cosmetic;

import kz.hapyl.fight.game.shop.ShopItem;
import org.bukkit.Location;

public abstract class Cosmetic extends ShopItem {

	private final Type type;

	protected Cosmetic(String name, String description, long cost, Type type) {
		super(name, description, cost);
		this.type = type;
	}

	public abstract void display(Location location);

	public Type getType() {
		return type;
	}
}
