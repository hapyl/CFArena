package me.hapyl.fight.util;

import com.google.common.collect.Lists;
import me.hapyl.fight.annotate.ExcludeInSort;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.inventory.gui.PlayerGUI;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.inventory.ClickType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Allows sorting elements by an Enum constants.
 *
 * @param <E> - Elements to sort.
 * @param <S> - Sorting enum.
 */
public abstract class Sortable<E, S extends Enum<S>> {

    private final Class<S> clazz;
    private final List<S> values;
    private S sort;

    @SafeVarargs
    public Sortable(@Nonnull Class<S> clazz, @Nonnull S... excludes) {
        this.clazz = clazz;
        this.values = Lists.newArrayList();
        this.sort = null;

        // init constants
        setValues(excludes);
    }

    public final int ordinal(@Nullable S s) {
        return s == null ? -1 : values.indexOf(s);
    }

    public abstract boolean isKeep(@Nonnull E e, @Nonnull S s);

    public List<E> sort(@Nonnull List<E> contents) {
        contents.removeIf(e -> sort != null && !isKeep(e, sort));
        return contents;
    }

    public void setSortItem(@Nonnull PlayerGUI gui, int slot, @Nonnull BiConsumer<PlayerGUI, S> onClick) {
        final ItemBuilder item = ItemBuilder.of(Material.NAME_TAG, "Sort", "&8Sort by " + clazz.getSimpleName()).addLore();

        item.addLoreIf("&a➥ &nNone", sort == null);
        item.addLoreIf(" &8None", sort != null);

        for (S value : values) {
            final boolean currentValue = sort == value;

            item.addLore((currentValue ? "&a➥ " : "&8 ") + value.toString());
            if (currentValue && value instanceof Described described) {
                item.addSmartLore(described.getDescription(), " &7&o");
            }
        }

        item.addLore();
        item.addLore("&7Left Click to cycle.");
        item.addLore("&7Right Click to cycle backwards.");

        gui.setItem(slot, item.asIcon());

        gui.setClick(slot, player -> {
            onClick.accept(gui, next());
            PlayerLib.playSound(player, Sound.BLOCK_LEVER_CLICK, 1);
        }, ClickType.LEFT, ClickType.SHIFT_LEFT);

        gui.setClick(slot, player -> {
            onClick.accept(gui, previous());
            PlayerLib.playSound(player, Sound.BLOCK_LEVER_CLICK, 1);
        }, ClickType.RIGHT, ClickType.SHIFT_RIGHT);
    }

    /**
     * Gets the current sort element, null if set to "none."
     *
     * @return the current sort element, null if set to "None."
     */
    @Nullable
    public S current() {
        return sort;
    }

    /**
     * Switches to the next element and get it, or null if it is "None."
     *
     * @return the next element and get it, or null if it is "None."
     */
    @Nullable
    public S next() {
        if (sort == null) {
            sort = values.get(0);
        }
        else {
            final int nextOrdinal = ordinal() + 1;

            if (nextOrdinal >= values.size()) {
                sort = null;
            }
            else {
                sort = values.get(nextOrdinal);
            }
        }

        return sort;
    }

    /**
     * Switches to the previous element and get it, or null if it is "None."
     *
     * @return the previous element and get it, or null if it is "none."
     */
    @Nullable
    public S previous() {
        if (sort == null) {
            sort = values.get(values.size() - 1);
        }
        else {
            final int previousOrdinal = ordinal() - 1;

            if (previousOrdinal < 0) {
                sort = null;
            }
            else {
                sort = values.get(previousOrdinal);
            }
        }

        return sort;
    }

    private int ordinal() {
        return ordinal(sort);
    }

    @SafeVarargs
    private void setValues(@Nonnull S... excludes) {
        values.clear();

        for (S s : clazz.getEnumConstants()) {
            // check exclusions
            if (checkArray(s, excludes)) {
                continue;
            }

            try {
                final Field field = clazz.getField(s.name());
                if (field.isAnnotationPresent(ExcludeInSort.class)) {
                    continue;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            values.add(s);
        }
    }

    @SafeVarargs
    private boolean checkArray(S element, S... array) {
        if (array == null) {
            return false;
        }

        for (S s : array) {
            if (element == s || element.equals(s)) {
                return true;
            }
        }

        return false;
    }

}
