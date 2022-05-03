package kz.hapyl.fight.event;

public class DamageOutput {

	private boolean cancelDamage;
	private double damage; // not an additional damage, override damage
	private String[] extraDisplayStrings;

	public DamageOutput() {
		this(0.0d, false);
	}

	public DamageOutput(double damage) {
		this(damage, false);
	}

	public DamageOutput(boolean cancelDamage) {
		this(0.0d, cancelDamage);
	}

	public DamageOutput(double damage, boolean cancelEvent) {
		this.damage = damage;
		this.cancelDamage = cancelEvent;
		this.extraDisplayStrings = null;
	}

	public String[] getExtraDisplayStrings() {
		return extraDisplayStrings;
	}

	public boolean hasExtraDisplayStrings() {
		return extraDisplayStrings != null;
	}

	public void addExtraDisplayStrings(String... strings) {
		this.extraDisplayStrings = strings;
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
