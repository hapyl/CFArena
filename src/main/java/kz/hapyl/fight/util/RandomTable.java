package kz.hapyl.fight.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomTable<E> {

	private final List<E> collection;

	public RandomTable() {
		this.collection = new ArrayList<>();
	}

	public RandomTable<E> add(E e) {
		this.collection.add(e);
		return this;
	}

	public RandomTable<E> remove(E e) {
		this.collection.remove(e);
		return this;
	}

	public void clear() {
		this.collection.clear();
	}

	public E getRandomElement() {
		if (collection.isEmpty()) {
			throw new NullPointerException("random table is empty, cannot retrieve an element");
		}
		return this.collection.get(ThreadLocalRandom.current().nextInt(this.collection.size()));
	}

}
