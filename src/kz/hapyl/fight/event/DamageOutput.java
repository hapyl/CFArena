package kz.hapyl.fight.event;

public class DamageOutput {

	private boolean cancelDamage;
	private double damage; // not an additional damage, override damage

	public DamageOutput() {
		this.damage = 0.0d;
		this.cancelDamage = false;
	}

	public DamageOutput setCancelDamage(boolean cancelDamage) {
		this.cancelDamage = cancelDamage;
		return this;
	}

	public boolean isCancelDamage() {
		return cancelDamage;
	}

	public DamageOutput addDamage(DamageInput input, double damage) {
		this.damage = input.getDamage() + damage;
		return this;
	}

	public DamageOutput setDamage(double damage) {
		this.damage = damage;
		return this;
	}

	public double getDamage() {
		return damage;
	}
}
