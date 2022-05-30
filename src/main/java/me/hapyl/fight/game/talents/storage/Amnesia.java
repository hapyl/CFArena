package me.hapyl.fight.game.talents.storage;

import me.hapyl.fight.game.effect.GameEffect;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class Amnesia extends GameEffect {
	public Amnesia() {
		super("Amnesia");
		this.setAbout("Players will move randomly and their vision is disturbed.");
		this.setPositive(false);
	}

	@Override
	public void onTick(Player player, int tick) {

	}

	@Override
	public void onStart(Player player) {
		PlayerLib.addEffect(player, PotionEffectType.CONFUSION, 99999, 1);
	}

	@Override
	public void onStop(Player player) {
		PlayerLib.removeEffect(player, PotionEffectType.CONFUSION);
	}
}
