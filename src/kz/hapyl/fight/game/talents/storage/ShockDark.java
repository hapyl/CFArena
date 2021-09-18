package kz.hapyl.fight.game.talents.storage;

import kz.hapyl.fight.game.Response;
import kz.hapyl.fight.game.talents.Talent;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ShockDark extends Talent {
	public ShockDark() {
		super("Shock Dart", "Shoots an arrow infused with &oshocking &7power. Upon hit, charges and explodes dealing damage.", Type.COMBAT);
		this.setItem(Material.LIGHT_BLUE_DYE);
		this.setCdSec(2);
	}

	@Override
	public Response execute(Player player) {
		return null;
	}
}
