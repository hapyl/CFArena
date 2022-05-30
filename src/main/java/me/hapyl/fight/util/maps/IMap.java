package me.hapyl.fight.util.maps;

import java.util.Set;

public interface IMap<K, V> {

	void put(K k, V v);

	void clear();

	void remove(K k);

	void removeByValue(V v);

	int size();

	boolean isEmpty();

	boolean hasKey(K k);

	boolean hasValue(V v);

	K getByValue(V v);

	V getByKey(K k);

	Set<K> keys();

	Set<V> values();

}
