package me.hapyl.fight.game.maps;

import org.bukkit.event.Listener;

public abstract class MapFeatureListener extends MapFeature implements Listener {
	protected MapFeatureListener(String name, String info) {
		super(name, info);
	}
}
