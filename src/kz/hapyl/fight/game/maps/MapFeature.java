package kz.hapyl.fight.game.maps;

public abstract class MapFeature {

	private final String name;
	private final String info;

	protected MapFeature(String name, String info) {
		this.name = name;
		this.info = info;
	}

	public String getName() {
		return name;
	}

	public String getInfo() {
		return info;
	}

	/**
	 * @param tick a modulo value of 20 of the runnable.
	 */
	public abstract void tick(int tick);

}
