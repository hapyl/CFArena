package me.hapyl.fight.util;

public class Nulls {

	public static <E> void runIfNotNull(E e, Function<E> function) {
		if (e != null) {
			function.execute(e);
		}
	}


}
