package kz.hapyl.fight.game.database;

import org.bukkit.configuration.file.YamlConfiguration;

public class IEntry<E> extends DatabaseEntry {

	private final Class<E> clazz;
	private final String path;

	public IEntry(Database database, String path, Class<E> clazz) {
		super(database);
		this.path = path;
		this.clazz = clazz;
	}

	@SuppressWarnings("all")
	public final E getValue() {
		final YamlConfiguration config = getConfig();

		if (clazz == Integer.class) {
			return (E)(Integer)config.getInt(path);
		}
		else if (clazz == Long.class) {
			return (E)(Long)config.getLong(path);
		}
		else if (clazz == Double.class || clazz == Float.class) {
			return (E)(Double)config.getDouble(path);
		}
		else if (clazz == String.class) {
			return (E)(String)config.getString(path);
		}

		return (E)config.get(path);
	}

	public final void setValue(E e) {
		getConfig().set(path, e);
	}

	public final void addValue(E e) {
		if (!isNumeric()) {
			return;
		}
		if (clazz == Integer.class) {
			getConfig().set(path, getConfig().getInt(path) + (int)e);
		}
		else if (clazz == Long.class) {
			getConfig().set(path, getConfig().getLong(path) + (long)e);
		}
		else if (clazz == Double.class || clazz == Float.class) {
			getConfig().set(path, getConfig().getDouble(path) + (clazz == Double.class ? (double)e : (float)e));
		}
	}

	public boolean isNumeric() {
		return clazz == Integer.class || clazz == Long.class || clazz == Double.class || clazz == Float.class;
	}

	public Class<E> getType() {
		return clazz;
	}

}
