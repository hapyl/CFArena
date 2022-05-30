package me.hapyl.fight.util.maps;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class KVMap<K, V> implements IMap<K, V> {

	private final Map<K, Node> nodes;

	public KVMap() {
		this.nodes = new HashMap<>();
	}

	@Override
	public int size() {
		return nodes.size();
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public void put(K k, V v) {
		nodes.put(k, new Node(k, v));
	}

	@Override
	public boolean hasKey(K k) {
		return nodes.containsKey(k);
	}

	@Override
	public boolean hasValue(V v) {
		return searchValue(v) != null;
	}

	@Nullable
	private Node searchValue(V v) {
		for (final Node node : nodes.values()) {
			if (node != null && node.v.equals(v)) {
				return node;
			}
		}
		return null;
	}

	@Override
	@Nullable
	public K getByValue(V v) {
		final Node node = searchValue(v);
		return node == null ? null : node.k;
	}

	@Override
	@Nullable
	public V getByKey(K k) {
		final Node node = nodes.get(k);
		return node == null ? null : node.v;
	}

	@Override
	public void clear() {
		this.nodes.clear();
	}

	@Override
	public void remove(K k) {
		this.nodes.remove(k);
	}

	@Override
	public void removeByValue(V v) {
		final Node node = searchValue(v);
		if (node != null) {
			remove(node.k);
		}
	}

	@Override
	public Set<K> keys() {
		return this.nodes.keySet();
	}

	@Override
	public Set<V> values() {
		final Set<V> values = new HashSet<>();
		for (final Node value : this.nodes.values()) {
			values.add(value.v);
		}
		return values;
	}

	private class Node {

		private final K k;
		private final V v;

		private Node(K k, V v) {
			this.k = k;
			this.v = v;
		}

	}

}
