package kz.hapyl.fight.cmds.extra;

import org.bukkit.entity.Player;
import org.bukkit.util.NumberConversions;

public abstract class Acceptor {

	public Acceptor() {
	}

	public abstract void execute(Player player, String[] args);

	protected final boolean checkLength(String[] array, int length) {
		return array.length >= length;
	}

	protected final Object arrayValue(String[] array, int pos, Object def) {
		return pos >= array.length ? def : array[pos];
	}

	protected final int intValue(String[] array, int pos) {
		return intValue(array, pos, 0);
	}

	protected final int intValue(String[] array, int pos, int def) {
		return NumberConversions.toInt(arrayValue(array, pos, def));
	}

	protected final long longValue(String[] array, int pos) {
		return intValue(array, pos, 0);
	}

	protected final long longValue(String[] array, int pos, long def) {
		return NumberConversions.toLong(arrayValue(array, pos, def));
	}

	protected final double doubleValue(String[] array, int pos) {
		return doubleValue(array, pos, 0.0d);
	}

	protected final double doubleValue(String[] array, int pos, double def) {
		return NumberConversions.toDouble(arrayValue(array, pos, def));
	}

	protected final String stringValue(String[] array, int pos, String def) {
		final Object object = arrayValue(array, pos, def);
		return object == null ? def : object.toString();
	}

}
