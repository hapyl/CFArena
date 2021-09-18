package kz.hapyl.fight.game.heroes.storage;

import kz.hapyl.fight.game.heroes.Hero;
import kz.hapyl.fight.game.talents.Talent;
import kz.hapyl.fight.game.talents.Talents;
import kz.hapyl.fight.game.talents.UltimateTalent;
import kz.hapyl.fight.game.weapons.Weapons;
import org.bukkit.Material;

public class Archer extends Hero {

	public Archer() {
		super("Archer");
		this.setAbout("One of the best archer joins the fight! Not alone though but with his &bcustom-made &7&obow.");
		this.setItem(Material.BOW);
		this.setWeapon(Weapons.ARCHER_BOW);
	}

	@Override
	public Talent getFirstTalent() {
		return Talents.TRIPLE_SHOT.getTalent();
	}

	@Override
	public Talent getSecondTalent() {
		return Talents.SHOCK_DARK.getTalent();
	}

	@Override
	public Talent getPassiveTalent() {
		return Talents.HAWKEYE_ARROW.getTalent();
	}

	@Override
	public UltimateTalent getUltimate() {
		return (UltimateTalent)Talents.BOOM_BOW.getTalent();
	}

}
