package me.hapyl.fight.util;

import java.util.function.Consumer;

public class Supplier<E> {

	private final E e;

	public Supplier(E e) {
		this.e = e;
	}

	public E supply(Consumer<E> action) {
		action.accept(e);
		return e;
	}

	public E get() {
		return e;
	}
}
