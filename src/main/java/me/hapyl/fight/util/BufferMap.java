package me.hapyl.fight.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BufferMap<K, V> extends ConcurrentHashMap<K, Buffer<V>> {

    public BufferMap() {
        super();
    }

    private BufferMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    private BufferMap(int initialCapacity) {
        super(initialCapacity);
    }

    private BufferMap(Map<? extends K, ? extends Buffer<V>> m) {
        super(m);
    }

    public Buffer<V> computeIfAbsent(K k, int maxCapacity) {
        return computeIfAbsent(k, f -> new Buffer<>(maxCapacity));
    }

    public void removeBuffer(K k) {
        final Buffer<V> buffer = get(k);

        if (buffer == null) {
            return;
        }

        buffer.clear();
        remove(k);
    }

    public void removeBuffers() {
        for (Buffer<V> value : values()) {
            value.clear();
        }

        clear();
    }
}
