package kz.hapyl.fight.game.database.entry;

import kz.hapyl.fight.game.database.Database;
import kz.hapyl.fight.game.database.DatabaseEntry;
import kz.hapyl.fight.game.heroes.Heroes;
import kz.hapyl.spigotutils.module.util.Validate;

public class HeroEntry extends DatabaseEntry {

	public HeroEntry(Database database) {
		super(database);
	}

	public Heroes getSelectedHero() {
		final String string = this.getConfig().getString("selected-hero", "ARCHER");
		return Validate.getEnumValue(Heroes.class, string);
	}

	public void setSelectedHero(Heroes hero) {
		this.getConfig().set("selected-hero", hero.name());
	}

}
