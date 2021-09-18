package kz.hapyl.fight.game.talents.storage;

import kz.hapyl.fight.game.Response;
import kz.hapyl.fight.game.talents.Talent;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class TripleShot extends Talent {
	public TripleShot() {
		super("Triple Shot", "Shoots three arrows that deals 150% of normal damage.", Type.COMBAT);
		this.setCd(40);
		this.setItem(Material.ARROW);
	}

	@Override
	public Response execute(Player player) {
		player.sendMessage("work");
		return Response.OK;
	}
}
