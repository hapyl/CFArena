package kz.hapyl.fight.game.talents.storage;

import kz.hapyl.fight.game.GamePlayer;
import kz.hapyl.fight.game.Response;
import kz.hapyl.fight.game.effect.GameEffectType;
import kz.hapyl.fight.game.talents.Talent;
import kz.hapyl.fight.game.task.GameTask;
import kz.hapyl.fight.util.Utils;
import kz.hapyl.spigotutils.module.player.PlayerLib;
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
		this.setItem(Material.INK_SAC);
		this.setCdSec(20);
	}

	@Override
	protected Response execute(Player player) {
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
