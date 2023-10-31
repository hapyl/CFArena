package me.hapyl.fight.game.talents.archive.troll;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.util.Collect;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class TrollSpin extends Talent {
	public TrollSpin() {
		super("Spin", "Rotates all enemies heads 180 degrees.", Type.COMBAT);

		setItem(Material.NAUTILUS_SHELL);
        setCooldown(300);
	}

	@Override
	public Response execute(@Nonnull GamePlayer player) {
		Collect.enemyPlayers(player).forEach(victim -> {
			final Player victimPlayer = victim.getPlayer();
			final Location location = victimPlayer.getLocation();

			location.setYaw(location.getYaw() + 180);
			victimPlayer.teleport(location);
			PlayerLib.playSound(victimPlayer, Sound.ENTITY_BLAZE_HURT, 2.0f);
		});

		return Response.OK;
	}
}
