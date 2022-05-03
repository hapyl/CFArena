package kz.hapyl.fight.game.shop;

public class ShopItem {

	private final String name;
	private final String description;
	private final long cost;

	public ShopItem(String name, String description, long cost) {
		this.name = name;
		this.description = description;
		this.cost = cost;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public long getCost() {
		return cost;
	}

}
