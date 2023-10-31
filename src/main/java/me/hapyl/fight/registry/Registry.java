package me.hapyl.fight.registry;

import me.hapyl.fight.item.ItemRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public interface Registry<T extends EnumId> {

    ItemRegistry ITEM_REGISTRY = new ItemRegistry();

    @Nullable
    T get(@Nonnull EnumId id);

    @Nonnull
    default Optional<T> getOptional(@Nonnull String id) {
        return Optional.empty();
    }

    boolean register(@Nonnull T t);

    boolean unregister(@Nonnull T t);


}
