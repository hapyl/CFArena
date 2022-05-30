package me.hapyl.fight.game.heroes.storage.extra;

public class CauldronEffect {

	private int doublePotion;
	private int effectHits;

	public CauldronEffect() {
		this.doublePotion = 5;
		this.effectHits = 10;
	}

	public void decrementDoublePotions() {
		--this.doublePotion;
	}

	public void decrementEffectPotions() {
		--this.effectHits;
	}

	public int getDoublePotion() {
		return doublePotion;
	}

	public int getEffectHits() {
		return effectHits;
	}
}
