package me.hapyl.fight.util;

import com.google.common.collect.Maps;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Map2Val<K, A, B> implements Map<K, Map2Val.Node<A, B>> {

    private final Map<K, Node<A, B>> hashMap;

    public Map2Val() {
        hashMap = Maps.newHashMap();
    }

    @Override
    public int size() {
        return hashMap.size();
    }

    @Override
    public boolean isEmpty() {
        return hashMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return hashMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return hashMap.containsValue(value);
    }

    @Override
    @Deprecated
    @Nullable
    public Node<A, B> get(Object key) {
        return hashMap.get(key);
    }

    @Nullable
    public A getA(K key) {
        final Node<A, B> node = get(key);

        return node == null ? null : node.a;
    }

    @Nullable
    public B getB(K key) {
        final Node<A, B> node = get(key);

        return node == null ? null : node.b;
    }

    @Override
    @Deprecated
    public Node<A, B> put(K key, Node<A, B> value) {
        return hashMap.put(key, value);
    }

    @Nullable
    public Node<A, B> put(K key, A a, B b) {
        return hashMap.put(key, new Node<>(a, b));
    }

    @Override
    public Node<A, B> remove(Object key) {
        return hashMap.remove(key);
    }

    @Override
    public void putAll(@Nonnull Map<? extends K, ? extends Node<A, B>> m) {
        hashMap.putAll(m);
    }

    @Override
    public void clear() {
        hashMap.clear();
    }

    @Override
    public Set<K> keySet() {
        return hashMap.keySet();
    }

    @Override
    public Collection<Node<A, B>> values() {
        return hashMap.values();
    }

    @Override
    public Set<Entry<K, Node<A, B>>> entrySet() {
        return hashMap.entrySet();
    }

    public static <A, B> Node<A, B> of(A a, B b) {
        return new Node<>(a, b);
    }

    @Nonnull
    public Node<A, B> computeIfAbsent(K key, A a, B b) {
        final Node<A, B> oldNode = get(key);

        if (oldNode != null) {
            return oldNode;
        }

        final Node<A, B> node = new Node<>(a, b);
        put(key, node);

        return node;
    }

    public static class Node<A, B> {

        private A a;
        private B b;

        public Node(A a, B b) {
            this.a = a;
            this.b = b;
        }

        public A getA() {
            return a;
        }

        public void setA(A a) {
            this.a = a;
        }

        public B getB() {
            return b;
        }

        public void setB(B b) {
            this.b = b;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Node<?, ?> node = (Node<?, ?>) o;
            return Objects.equals(a, node.a) && Objects.equals(b, node.b);
        }

        @Override
        public int hashCode() {
            return Objects.hash(a, b);
        }
    }

}
