package kz.hapyl.fight.game.maps;

import kz.hapyl.fight.Main;
import kz.hapyl.fight.game.GameElement;
import kz.hapyl.fight.game.Manager;
import org.bukkit.event.Listener;

public abstract class MapFeature implements GameElement {

	private final String name;
	private final String info;

	public MapFeature(String name, String info) {
		this.name = name;
		this.info = info;
		if (this instanceof Listener listener) {
			Main.getPlugin().addEvent(listener);
		}
	}

	public String getName() {
		return name;
	}

	public String getInfo() {
		return info;
	}

	@Override
	public void onStart() {

	}

	@Override
	public void onStop() {

	}

	protected final boolean validateCurrentMap(GameMaps maps) {
		return Manager.current().getCurrentMap() == maps;
	}

	/**
	 * @param tick a modulo value of 20 of the runnable.
	 */
	public abstract void tick(int tick);

}
