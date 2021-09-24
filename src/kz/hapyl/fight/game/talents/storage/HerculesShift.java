package kz.hapyl.fight.game.talents.storage;

import kz.hapyl.fight.game.Response;
import kz.hapyl.fight.game.talents.ChargedTalent;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class HerculesShift extends ChargedTalent {
	public HerculesShift() {
		super("Shift", "Instantly propel yourself forward.", 3);
		this.setItem(Material.FEATHER);
		this.setCdSec(1);
		this.setRechargeTimeSec(6);
	}

	@Override
	public Response execute(Player player) {
		player.setVelocity(player.getLocation().getDirection().normalize().multiply(1.8d));

		PlayerLib.playSound(player.getLocation(), Sound.ENTITY_GHAST_SHOOT, 1.25f);
		PlayerLib.playSound(player.getLocation(), Sound.ENTITY_WITHER_SHOOT, 0.85f);

		return Response.OK;
	}
}
