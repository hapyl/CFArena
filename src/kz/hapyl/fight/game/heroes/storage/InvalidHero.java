package kz.hapyl.fight.game.heroes.storage;

import kz.hapyl.fight.game.heroes.Hero;
import kz.hapyl.fight.game.talents.Talent;
import org.bukkit.Material;

public class InvalidHero extends Hero {
	public InvalidHero() {
		super("Disabled Hero");
		this.setInfo("This hero is currently disabled. Sorry!");
		this.setItem(Material.BARRIER);
	}

	@Override
	public Talent getFirstTalent() {
		return null;
	}

	@Override
	public Talent getSecondTalent() {
		return null;
	}

	@Override
	public Talent getPassiveTalent() {
		return null;
	}
}
