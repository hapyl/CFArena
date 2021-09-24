package kz.hapyl.fight.game;

import org.bukkit.entity.Player;

public interface PlayerElement {

	void onStart(Player player);

	default void onStop(Player player) {

	}

}
