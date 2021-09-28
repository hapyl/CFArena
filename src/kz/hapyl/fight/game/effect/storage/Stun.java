package kz.hapyl.fight.game.effect.storage;

import kz.hapyl.fight.game.effect.GameEffect;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class Stun extends GameEffect {
	public Stun() {
		super("Stun");
		this.setAbout("Stunned players cannot move or use their abilities. Effect will be cleared upon taking damage.");
		this.setPositive(false);
	}

	@Override
	public void onTick(Player player, int tick) {
	}

	@Override
	public void onStart(Player player) {
		PlayerLib.addEffect(player, PotionEffectType.SLOW, 999999, 250);
		PlayerLib.addEffect(player, PotionEffectType.JUMP, 999999, 250);
		PlayerLib.addEffect(player, PotionEffectType.WEAKNESS, 999999, 250);
	}

	@Override
	public void onStop(Player player) {
		PlayerLib.removeEffect(player, PotionEffectType.SLOW);
		PlayerLib.removeEffect(player, PotionEffectType.JUMP);
		PlayerLib.removeEffect(player, PotionEffectType.WEAKNESS);
	}
}
