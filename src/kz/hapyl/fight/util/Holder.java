package kz.hapyl.fight.util;

public class Holder<E> {

	private E e;

	public Holder() {
		this.e = null;
	}

	public Holder(E e) {
		this.e = e;
	}

	public E get() {
		return e;
	}

	public E getOr(E or) {
		return get() == null ? or : get();
	}

	public void set(E e) {
		this.e = e;
	}


}
