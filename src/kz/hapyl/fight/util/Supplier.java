package kz.hapyl.fight.util;

import kz.hapyl.spigotutils.module.util.Action;

public class Supplier<E> {

	private final E e;

	public Supplier(E e) {
		this.e = e;
	}

	public E supply(Action<E> action) {
		action.use(e);
		return e;
	}

	public E get() {
		return e;
	}
}
