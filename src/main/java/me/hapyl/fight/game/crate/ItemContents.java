package me.hapyl.fight.game.crate;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.hapyl.eterna.module.util.CollectionUtils;
import me.hapyl.fight.game.cosmetic.Cosmetic;
import me.hapyl.fight.game.cosmetic.Rarity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * There are two ways of doing this:
 *
 * <ul>
 *     <li>Reworking the whole system to not use enums but instead registries.</li>
 *     <li>Limiting crates to cosmetics only (which realistically it will always be).</li>
 * </ul>
 * <p>
 * The better way is to remove enums and use registries; but it is also the longer way.
 * So for now I'm just limiting crates to cosmetics only.
 */
public class ItemContents<T extends Cosmetic> {

    private final Map<Rarity, List<T>> contents;

    public ItemContents() {
        this.contents = Maps.newLinkedHashMap(); // need to preserve rarity order
        for (Rarity value : Rarity.values()) {
            if (!value.isDroppable()) {
                continue;
            }

            this.contents.put(value, Lists.newArrayList());
        }
    }

    public void setContents(@Nonnull List<T> list) {
        if (list.isEmpty()) {
            return;
        }

        for (T item : list) {
            addItem(item);
        }
    }

    public void addItem(@Nonnull T item) {
        getItems(item.getRarity()).add(item);
    }

    /**
     * @return a random item by a rarity, or null if empty.
     */
    @Nullable
    public T randomItem(@Nonnull Rarity rarity) {
        return CollectionUtils.randomElement(getItems(rarity));
    }

    @Nullable
    public T firstItem() {
        for (List<T> list : contents.values()) {
            if (list == null) {
                return null;
            }

            return list.getFirst();
        }

        return null;
    }

    /**
     * Gets a <b>copy</b> of items by the rarity.
     *
     * @param rarity - Rarity.
     * @return a <b>copy</b> of items by the rarity.
     */
    @Nonnull
    public List<T> getItemsAsCopy(@Nonnull Rarity rarity) {
        return Lists.newArrayList(getItems(rarity));
    }

    public boolean isEmpty() {
        return contents.isEmpty();
    }

    public void forEachRarityIfNotEmpty(@Nonnull Consumer<Rarity> consumer) {
        for (Map.Entry<Rarity, List<T>> entry : contents.entrySet()) {
            if (entry.getValue().isEmpty()) {
                continue;
            }

            consumer.accept(entry.getKey());
        }
    }

    public void forEachRarity(@Nonnull Consumer<Rarity> consumer) {
        contents.keySet().forEach(consumer);
    }

    public void forEachItem(@Nonnull Consumer<T> consumer) {
        contents.values().forEach(list -> list.forEach(consumer));
    }

    public List<T> listAll() {
        final List<T> list = Lists.newArrayList();
        contents.values().forEach(list::addAll);

        return list;
    }

    public void forEach(@Nonnull BiConsumer<Rarity, List<T>> consumer) {
        contents.forEach(consumer);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder("{");

        for (Rarity rarity : contents.keySet()) {
            builder.append(rarity.name()).append("[");
            builder.append(concatWithCommas(getItems(rarity))).append("]");
        }

        return builder.append("}").toString();
    }

    private List<T> getItems(Rarity rarity) {
        return contents.computeIfAbsent(rarity, fn -> Lists.newArrayList());
    }

    private String concatWithCommas(List<? extends Cosmetic> list) {
        final StringBuilder builder = new StringBuilder();

        for (int i = 0; i < list.size(); i++) {
            if (i != 0) {
                builder.append(", ");
            }

            final Cosmetic rareItem = list.get(i);
            builder.append(rareItem.getKey());
        }

        return builder.toString();
    }

}
