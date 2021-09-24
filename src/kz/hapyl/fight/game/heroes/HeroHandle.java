package kz.hapyl.fight.game.heroes;

import kz.hapyl.fight.game.heroes.storage.Alchemist;
import kz.hapyl.fight.game.heroes.storage.Archer;
import kz.hapyl.fight.game.heroes.storage.Moonwalker;
import kz.hapyl.fight.game.heroes.storage.Pytaria;
import kz.hapyl.fight.game.talents.storage.DrEd;

/**
 * This class is used to access hero handles faster.
 */
public class HeroHandle {

	public static final Archer ARCHER = (Archer)Heroes.ARCHER.getHero();
	public static final Alchemist ALCHEMIST = (Alchemist)Heroes.ALCHEMIST.getHero();
	public static final Moonwalker MOONWALKER = (Moonwalker)Heroes.MOONWALKER.getHero();
	public static final Pytaria PYTARIA = (Pytaria)Heroes.PYTARIA.getHero();
	public static final DrEd DR_ED = (DrEd)Heroes.DR_ED.getHero();

}
