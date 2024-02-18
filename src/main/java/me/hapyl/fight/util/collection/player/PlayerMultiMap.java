package me.hapyl.fight.util.collection.player;

import com.google.common.collect.Maps;
import me.hapyl.fight.game.entity.GamePlayer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;

public class PlayerMultiMap<A, B> implements PlayerMap<PlayerMultiMap<A, B>.MultiEntry> {

    private final HashMap<GamePlayer, MultiEntry> hashMap;

    public PlayerMultiMap() {
        this.hashMap = Maps.newHashMap();
    }

    public void putA(@Nonnull GamePlayer key, @Nonnull A a) {
        getOrCompute(key).a = a;
    }

    public void putB(@Nonnull GamePlayer key, @Nonnull B b) {
        getOrCompute(key).b = b;
    }

    @Nullable
    public A removeA(@Nonnull GamePlayer key) {
        final MultiEntry entry = getOrCompute(key);
        A oldA = entry.a;

        entry.a = null;
        return oldA;
    }

    @Nullable
    public B removeB(@Nonnull GamePlayer key) {
        final MultiEntry entry = getOrCompute(key);
        B oldB = entry.b;

        entry.b = null;
        return oldB;
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
        for (MultiEntry entry : hashMap.values()) {
            if (entry.contains(value)) {
                return true;
            }
        }

        return false;
    }

    public boolean containsA(@Nonnull GamePlayer key, @Nullable A a) {
        final MultiEntry entry = getOrCompute(key);

        return entry != null && entry.a != null && entry.a == a;
    }

    public boolean containsB(@Nonnull GamePlayer key, @Nullable B b) {
        final MultiEntry entry = getOrCompute(key);

        return entry != null && entry.b != null && entry.b == b;
    }

    @Override
    @Deprecated
    public MultiEntry get(Object key) {
        return hashMap.get(key);
    }

    @Override
    @Deprecated
    public MultiEntry put(GamePlayer key, MultiEntry value) {
        return hashMap.put(key, value);
    }

    @Override
    public MultiEntry remove(Object key) {
        return hashMap.remove(key);
    }

    @Override
    @Deprecated
    public void putAll(@Nonnull Map<? extends GamePlayer, ? extends MultiEntry> m) {
        hashMap.putAll(m);
    }

    @Override
    public void clear() {
        hashMap.clear();
    }

    @Nonnull
    @Override
    public Set<GamePlayer> keySet() {
        return hashMap.keySet();
    }

    @Nonnull
    @Override
    public Collection<MultiEntry> values() {
        return hashMap.values();
    }

    @Nonnull
    @Override
    public Set<Entry<GamePlayer, MultiEntry>> entrySet() {
        return hashMap.entrySet();
    }

    public void removeAnd(@Nonnull GamePlayer key, @Nullable Consumer<A> consumerA, @Nullable Consumer<B> consumerB) {
        final MultiEntry entry = remove(key);

        if (entry == null) {
            return;
        }

        entry.consume(consumerA, consumerB);
    }

    public void forEachAndClear(@Nullable Consumer<A> consumerA, @Nullable Consumer<B> consumerB) {
        forEach((k, entry) -> {
            entry.consume(consumerA, consumerB);
        });
        clear();
    }

    private MultiEntry getOrCompute(GamePlayer key) {
        return computeIfAbsent(key, MultiEntry::new);
    }

    public class MultiEntry {
        private final GamePlayer player;
        private A a;
        private B b;

        MultiEntry(GamePlayer player) {
            this.player = player;
        }

        @Nonnull
        public GamePlayer getPlayer() {
            return player;
        }

        @Nullable
        public A getA() {
            return a;
        }

        @Nullable
        public B getB() {
            return b;
        }

        @Override
        @SuppressWarnings("unchecked")
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || getClass() != object.getClass()) {
                return false;
            }

            final MultiEntry that = (MultiEntry) object;
            return Objects.equals(player, that.player);
        }

        @Override
        public int hashCode() {
            return Objects.hash(player);
        }

        public boolean contains(Object value) {
            return value != null && (a != null && a == value) || (b != null && b == value);
        }

        public void consume(Consumer<A> cA, Consumer<B> cB) {
            if (a != null && cA != null) {
                cA.accept(a);
            }

            if (b != null && cB != null) {
                cB.accept(b);
            }
        }
    }

}
