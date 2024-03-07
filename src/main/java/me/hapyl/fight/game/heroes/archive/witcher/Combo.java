package me.hapyl.fight.game.heroes.archive.witcher;

import me.hapyl.fight.game.achievement.Achievements;
import me.hapyl.fight.game.entity.GamePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;

import javax.annotation.Nullable;

public class Combo {

	private static final long MIN_DELAY_BETWEEN_COMBO = 500;
	private static final long MAX_DELAY_BETWEEN_COMBO = 2500;

	private final GamePlayer player;

	private LivingEntity entity;
	private int combo;
	private long lastHit;

	public Combo(GamePlayer player) {
		this.player = player;
	}

	public void reset() {
		this.combo = 0;
		this.lastHit = 0;
		this.entity = null;

		// Fx
		player.sendMessage("&eYou combo has reset!");
		player.playSound(Sound.ITEM_SHIELD_BREAK, 0.0f);
	}

	public void incrementCombo() {
		this.combo += 1;
		this.lastHit = System.currentTimeMillis();

		// Achievement
		if (combo >= 16) {
			Achievements.COMBO.complete(player);
		}
	}

	public boolean validateCanCombo() {
		final long l = System.currentTimeMillis() - this.lastHit;

		return this.lastHit == 0 || l >= MIN_DELAY_BETWEEN_COMBO;
	}

	public boolean validateSameEntity(LivingEntity entity) {
		return entity != null && getEntity() != null && getEntity() == entity;
	}

	public GamePlayer getPlayer() {
		return player;
	}

	@Nullable
	public LivingEntity getEntity() {
		return entity;
	}

	public void setEntity(LivingEntity entity) {
		this.entity = entity;
	}

	public int getCombo() {
		return combo;
	}

	public long getLastHit() {
		return lastHit;
	}

	public boolean isTimedOut() {
		final long l = System.currentTimeMillis() - lastHit;

		return lastHit != 0 && l >= MAX_DELAY_BETWEEN_COMBO;
	}
}
