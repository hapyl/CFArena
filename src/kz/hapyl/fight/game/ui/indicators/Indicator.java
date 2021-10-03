package kz.hapyl.fight.game.ui.indicators;

import org.bukkit.entity.ArmorStand;

import java.util.Collection;

public interface Indicator {

	Collection<ArmorStand> getIndicators();

	void clear();

	void create();


}
