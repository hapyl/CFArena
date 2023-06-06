package me.hapyl.fight.game.heroes.archive.witcher;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public class Combo {

	private final long delayBetweenHits = 2500;

	private final Player player;

	private LivingEntity entity;
	private int combo;
	private long lastHit;

	public Combo(Player player) {
		this.player = player;
	}

	public void reset() {
		this.combo = 0;
		this.lastHit = 0;
		this.entity = null;
	}

	public void incrementCombo() {
		this.combo += 1;
		this.lastHit = System.currentTimeMillis();
	}

	public boolean validateCanCombo() {
		return this.lastHit == 0 || (System.currentTimeMillis() - this.lastHit) < delayBetweenHits;
	}

	public boolean validateSameEntity(LivingEntity entity) {
		return getEntity() != null && getEntity() == entity;
	}

	public Player getPlayer() {
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
