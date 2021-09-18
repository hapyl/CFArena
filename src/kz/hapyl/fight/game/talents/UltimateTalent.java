package kz.hapyl.fight.game.talents;

import kz.hapyl.fight.game.talents.Talent;

public abstract class UltimateTalent extends Talent {

	private final int cost;

	public UltimateTalent(String name, String description, int pointCost) {
		super(name, description, Type.ULTIMATE);
		this.cost = pointCost;
	}

	public int getCost() {
		return cost;
	}

}
