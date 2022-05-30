package me.hapyl.fight.game.heroes.storage;

import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.talents.Talent;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class InvalidHero extends Hero {
	public InvalidHero() {
		super("Disabled Hero");
		this.setInfo("This hero is currently disabled. Sorry!");
		this.setItem(Material.BARRIER);
	}

	@Override
	public void useUltimate(Player player) {

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
