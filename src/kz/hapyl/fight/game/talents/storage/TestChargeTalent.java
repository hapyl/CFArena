package kz.hapyl.fight.game.talents.storage;

import kz.hapyl.fight.game.Response;
import kz.hapyl.fight.game.talents.ChargedTalent;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class TestChargeTalent extends ChargedTalent {
	public TestChargeTalent() {
		super("Test Ability", "this is a test ability", 3);
		this.setRechargeTime(60);
		this.setCd(20);
		this.setItem(Material.COBWEB);
	}

	@Override
	public Response execute(Player player) {
		player.sendMessage("executed");
		return Response.OK;
	}
}
