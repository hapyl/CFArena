package me.hapyl.fight.game.talents.archive.hercules;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.ChargedTalent;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Material;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public class HerculesShift extends ChargedTalent {
	public HerculesShift() {
        super("Shift", "Instantly propel yourself forward.", 3);

        setItem(Material.FEATHER);
        setCooldownSec(1);
        setRechargeTimeSec(6);
    }

	@Override
	public Response execute(@Nonnull GamePlayer player) {
		player.setVelocity(player.getLocation().getDirection().normalize().multiply(1.8d));

		PlayerLib.playSound(player.getLocation(), Sound.ENTITY_GHAST_SHOOT, 1.25f);
		PlayerLib.playSound(player.getLocation(), Sound.ENTITY_WITHER_SHOOT, 0.85f);

		return Response.OK;
	}
}
