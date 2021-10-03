package kz.hapyl.fight.game.database;

public abstract class PDEntry<E extends Number> extends DatabaseEntry {

	private final String path;

	private PDEntry(Database database, String path) {
		super(database);
		this.path = path;
	}

	public final E getValue() {
		return longToE(this.getConfig().getLong(path, 0L));
	}

	protected abstract E longToE(long l);

	protected abstract long EtoLong(E e);

	public static class LongEntry extends PDEntry<Long> {
		public LongEntry(Database database, String path) {
			super(database, path);
		}

		@Override
		public final Long longToE(long l) {
			return l;
		}

		@Override
		public final long EtoLong(Long aLong) {
			return aLong;
		}

	}

}
