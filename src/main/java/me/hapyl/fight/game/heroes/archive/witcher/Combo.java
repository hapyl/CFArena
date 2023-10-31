package me.hapyl.fight.game.heroes.archive.witcher;

import me.hapyl.fight.game.entity.GamePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;

import javax.annotation.Nullable;

public class Combo {

	private static final long DELAY_BETWEEN_HITS = 2500;

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
	}

	public boolean validateCanCombo() {
		return this.lastHit == 0 || (System.currentTimeMillis() - this.lastHit) < DELAY_BETWEEN_HITS;
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
		validateCanCombo();
		return combo;
	}

	public long getLastHit() {
		return lastHit;
	}

}
