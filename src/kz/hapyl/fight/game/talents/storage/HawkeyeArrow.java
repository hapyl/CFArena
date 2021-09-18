package kz.hapyl.fight.game.talents.storage;

import kz.hapyl.fight.game.Response;
import kz.hapyl.fight.game.talents.Talent;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class HawkeyeArrow extends Talent {
	public HawkeyeArrow() {
		super("Hawkeye Arrow", "Fully charged shots while sneaking have 20% chance to fire hawkeye arrow which homes to nearby enemies.", Type.PASSIVE);
		this.setItem(Material.ENDER_EYE);
	}

	@Override
	public Response execute(Player player) {
		return null;
	}
}
