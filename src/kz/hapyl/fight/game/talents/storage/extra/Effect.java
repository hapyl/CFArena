package kz.hapyl.fight.game.talents.storage.extra;

import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Effect {

	private final String effectName;
	private final String effectChar;
	private final PotionEffect potionEffect;

	public void affect(Player player) {
	}

	public Effect(String effectChar, String effectName, PotionEffectType effect, int duration, int level) {
		this.effectChar = effectChar;
		this.effectName = effectName;
		this.potionEffect = effect == null ? null : new PotionEffect(effect, duration, level);
	}

	public Effect(String effectChar, String effectName) {
		this(effectChar, effectName, null, 0, 0);
	}

	public String getEffectChar() {
		return effectChar;
	}

	public String getEffectName() {
		return effectName;
	}

	public PotionEffect getPotionEffect() {
		return potionEffect;
	}

	public void applyEffectsIgnoreFx(Player player) {
		affect(player);
		if (potionEffect != null) {
			PlayerLib.addEffect(player, potionEffect.getType(), potionEffect.getDuration(), potionEffect.getAmplifier());
		}
	}

	public void applyEffects(Player player) {
		applyEffectsIgnoreFx(player);
		PlayerLib.playSound(player.getLocation(), Sound.ENTITY_PLAYER_SWIM, 1.8f);
		Chat.sendTitle(player, "&a" + effectChar, "&6Gained " + effectName, 5, 10, 5);
		Chat.sendMessage(player, "&a" + effectChar + " &6Gained " + effectName);
	}

}
