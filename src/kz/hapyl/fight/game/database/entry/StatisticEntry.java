package kz.hapyl.fight.game.database.entry;

import kz.hapyl.fight.game.database.Database;
import kz.hapyl.fight.game.database.DatabaseEntry;

import java.util.Locale;

public class StatisticEntry extends DatabaseEntry {

	public final Type KILLS = new Type("kills");
	public final Type DEATHS = new Type("deaths");
	public final Type WINS = new Type("wins");
	public final Type PLAYTIME = new Type("playtime");

	public StatisticEntry(Database database) {
		super(database);
	}

	private long getValue(Type type) {
		return this.getConfig().getLong(type.path(), 0L);
	}

	private void setValue(Type type, long value) {
		this.getConfig().set(type.path(), value);
	}

	private void addValue(Type type, long value) {
		setValue(type, getValue(type) + value);
	}

	public class Type {

		private final String name;

		public Type(String name) {
			this.name = name;
		}

		public void setValue(long value) {
			StatisticEntry.this.setValue(this, value);
		}

		public void addValue(long value) {
			StatisticEntry.this.addValue(this, value);
		}

		public long getValue() {
			return StatisticEntry.this.getValue(this);
		}

		protected String path() {
			return "stat." + this.name.toUpperCase(Locale.ROOT);
		}

	}

}
