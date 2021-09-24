package kz.hapyl.fight.game.talents;

import kz.hapyl.fight.game.Response;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class PassiveTalent extends Talent {

	public PassiveTalent(String name, String description,  Material item) {
		super(name, description, Type.PASSIVE);
		this.setItem(item);
	}

	@Override
	public final Response execute(Player player) {
		return Response.OK;
	}
}
