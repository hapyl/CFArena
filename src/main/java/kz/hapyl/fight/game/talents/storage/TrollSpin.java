package kz.hapyl.fight.game.talents.storage;

import kz.hapyl.fight.game.Manager;
import kz.hapyl.fight.game.Response;
import kz.hapyl.fight.game.talents.Talent;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class TrollSpin extends Talent {
	public TrollSpin() {
		super("Spin", "Rotates all enemies heads 180 degrees.", Type.COMBAT);
		this.setItem(Material.NAUTILUS_SHELL);
		this.setCd(300);
	}

	@Override
	public Response execute(Player player) {
		Manager.current().getCurrentGame().getAlivePlayers().forEach(victim -> {
			final Player victimPlayer = victim.getPlayer();
			if (victimPlayer == player) {
				return;
			}
			final Location location = victimPlayer.getLocation();
			location.setYaw(location.getYaw() + 180);
			victimPlayer.teleport(location);
			PlayerLib.playSound(victimPlayer, Sound.ENTITY_BLAZE_HURT, 2.0f);
		});
		return Response.OK;
	}
}
