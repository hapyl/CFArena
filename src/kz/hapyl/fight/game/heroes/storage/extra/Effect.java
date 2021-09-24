package kz.hapyl.fight.game.heroes.storage.extra;

import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Effect {

	private final String suffix;
	private final PotionEffect effect;
	private final boolean isPositive;

	public void affect(Player player) {

	}

	public Effect(String suffix, PotionEffectType type, int duration, int level) {
		this.suffix = suffix;
		this.effect = type == null ? null : new PotionEffect(type, duration * 20, level);
		this.isPositive = duration == 30;
	}

	public String getSuffix() {
		return suffix;
	}

	public Effect(String suffix, int duration) {
		this(suffix, null, duration, 0);
	}

	public void applyEffects(Player player) {
		this.affect(player);
		if (effect != null) {
			PlayerLib.addEffect(player, effect.getType(), effect.getDuration(), effect.getAmplifier());
		}
		Chat.sendMessage(player, isPositive ?
				"&a&l☘ &aAlchemical Madness %s&a!".formatted(suffix) :
				"&c☠ Alchemical Madness %s&c!".formatted(suffix));
	}

}
