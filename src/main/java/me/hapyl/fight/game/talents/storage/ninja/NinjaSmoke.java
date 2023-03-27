package me.hapyl.fight.game.talents.storage.ninja;

import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Utils;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class NinjaSmoke extends Talent {
	public NinjaSmoke() {
		super(
				"Smoke Bomb",
				"Instantly throw a smoke bomb at your current location and become invisible. Players inside the smoke will have their vision disturbed."
		);

		setItem(Material.INK_SAC);
		setCdSec(20);
	}

	@Override
	public Response execute(Player player) {
		final Location location = player.getLocation();
		GamePlayer.getPlayer(player).addEffect(GameEffectType.INVISIBILITY, 120);

		GameTask.runTaskTimerTimes((task, time) -> {
			PlayerLib.spawnParticle(location, Particle.EXPLOSION_NORMAL, 20, 1, 0, 1, 0.0f);
			Utils.getPlayersInRange(location, 2).forEach(range -> PlayerLib.addEffect(range, PotionEffectType.BLINDNESS, 20, 1));
		}, 20, 5);

		PlayerLib.playSound(player, Sound.ITEM_ARMOR_EQUIP_LEATHER, 0.0f);
		return Response.OK;
	}
}
