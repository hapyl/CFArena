package kz.hapyl.fight.game.effect.storage;

import kz.hapyl.fight.game.effect.GameEffect;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class Corrosion extends GameEffect {
	public Corrosion() {
		super("Corrosion");
		this.setAbout("Slows, disturbs vision and rapidly damages players.");
		this.setPositive(false);
	}

	@Override
	public void onTick(Player player, int tick) {

	}

	@Override
	public void onStart(Player player) {
		PlayerLib.addEffect(player, PotionEffectType.SLOW, 999999, 4);
		PlayerLib.addEffect(player, PotionEffectType.POISON, 999999, 4);
		PlayerLib.addEffect(player, PotionEffectType.BLINDNESS, 999999, 4);
	}

	@Override
	public void onStop(Player player) {
		PlayerLib.removeEffect(player, PotionEffectType.SLOW);
		PlayerLib.removeEffect(player, PotionEffectType.POISON);
		PlayerLib.removeEffect(player, PotionEffectType.BLINDNESS);
	}

	@Override
	public String getExtra() {
		return "";
	}
}
