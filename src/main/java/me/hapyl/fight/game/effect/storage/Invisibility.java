package me.hapyl.fight.game.effect.storage;

import me.hapyl.fight.game.effect.GameEffect;
import me.hapyl.fight.util.Utils;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class Invisibility extends GameEffect {
	public Invisibility() {
		super("Invisibility");
	}

	@Override
	public void onStart(Player player) {
		Utils.hidePlayer(player);
		PlayerLib.addEffect(player, PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1);
	}

	@Override
	public void onStop(Player player) {
		Utils.showPlayer(player);
		player.removePotionEffect(PotionEffectType.INVISIBILITY);
	}

	@Override
	public void onTick(Player player, int tick) {

	}
}
