package me.hapyl.fight.game.database;

import org.bukkit.util.NumberConversions;

public abstract class Type<E> {

	public static final Type<Integer> INT = new Type<>(Integer.class) {
		@Override
		public Integer fromObject(Object obj) {
			return obj instanceof Number ? NumberConversions.toInt(obj) : 0;
		}
	};

	public static final Type<String> STR = new Type<>(String.class) {
		@Override
		public String fromObject(Object obj) {
			return obj == null ? "null" : obj.toString();
		}
	};

	public static final Type<Double> DOUBLE = new Type<>(Double.class) {
		@Override
		public Double fromObject(Object obj) {
			return obj instanceof Double ? NumberConversions.toDouble(obj) : 0.0d;
		}
	};

	public static final Type<Long> LONG = new Type<>(Long.class) {
		@Override
		public Long fromObject(Object obj) {
			return obj instanceof Long ? NumberConversions.toLong(obj) : 0L;
		}
	};

	public static final Type<Boolean> BOOL = new Type<>(Boolean.class) {
		@Override
		public Boolean fromObject(Object obj) {
			if (obj == null) {
				return false;
			}
			return obj instanceof Boolean ? (Boolean)obj : false;
		}
	};

	private final Class<E> type;

	Type(Class<E> type) {
		this.type = type;
	}

	public Class<E> getType() {
		return type;
	}

	public abstract E fromObject(Object obj);
}