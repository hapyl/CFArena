package me.hapyl.fight.util;

import javax.annotation.Nonnull;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentConsumerHashMap<K, V> extends ConcurrentHashMap<K, V> {

    public void replace(@Nonnull K k, @Nonnull V v, @Nonnull NullableConsumer<V> consumer) {
        consumer.acceptNullable(put(k, v));
    }

    public void remove(@Nonnull K k, @Nonnull NullableConsumer<V> consumer) {
        consumer.acceptNullable(remove(k));
    }

    public void clear(@Nonnull NullableConsumer<V> consumer) {
        values().forEach(consumer);
        clear();
    }

}
