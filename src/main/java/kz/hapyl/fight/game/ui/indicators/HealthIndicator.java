package kz.hapyl.fight.game.ui.indicators;

import kz.hapyl.fight.game.GameInstance;
import kz.hapyl.fight.game.GamePlayer;
import kz.hapyl.fight.util.Nulls;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class HealthIndicator implements Indicator {

	private final GameInstance game;
	private final Map<Player, ArmorStand> indicators;

	public HealthIndicator(GameInstance instance) {
		this.game = instance;
		this.indicators = new HashMap<>();
		create();
	}

	@Override
	public Collection<ArmorStand> getIndicators() {
		return indicators.values();
	}

	public void remove(Player player) {
		Nulls.runIfNotNull(indicators.get(player), Entity::remove);
		indicators.remove(player);
	}

	@Override
	public void clear() {
		indicators.values().forEach(Entity::remove);
		indicators.clear();
	}

	@Override
	public void create() {
		for (final GamePlayer player : game.getAlivePlayers()) {

		}
	}

}
