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
 * Allows filtering elements by an {@link Enum} constants.
 *
 * @param <E> - Elements to filter.
 * @param <S> - Filtering enum.
 */
public abstract class Filter<E, S extends Enum<S>> {

    private final Class<S> clazz;
    private final List<S> values;
    private S current;

    @SafeVarargs
    public Filter(@Nonnull Class<S> clazz, @Nonnull S... excludes) {
        this.clazz = clazz;
        this.values = Lists.newArrayList();
        this.current = null;

        // init constants
        setValues(excludes);
    }

    public final int ordinal(@Nullable S s) {
        return s == null ? -1 : values.indexOf(s);
    }

    public abstract boolean isKeep(@Nonnull E e, @Nonnull S s);

    @Nonnull
    public List<E> filter(@Nonnull List<E> contents) {
        contents.removeIf(e -> current != null && !isKeep(e, current));
        return contents;
    }

    public void setFilterItem(@Nonnull PlayerGUI gui, int slot, @Nonnull BiConsumer<PlayerGUI, S> onClick) {
        final ItemBuilder item = ItemBuilder.of(Material.NAME_TAG, "Filter", "&8Filter by " + clazz.getSimpleName()).addLore();

        item.addLoreIf("&a➥ &nNone", current == null);
        item.addLoreIf(" &8None", current != null);

        for (S value : values) {
            final boolean currentValue = current == value;

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
     * Gets the current sort element, null if set to "None."
     *
     * @return the current sort element, null if set to "None."
     */
    @Nullable
    public S current() {
        return current;
    }

    /**
     * Switches to the next element and get it, or null if it is "None."
     *
     * @return the next element and get it, or null if it is "None."
     */
    @Nullable
    public S next() {
        if (current == null) {
            current = values.get(0);
        }
        else {
            final int nextOrdinal = ordinal() + 1;

            if (nextOrdinal >= values.size()) {
                current = null;
            }
            else {
                current = values.get(nextOrdinal);
            }
        }

        return current;
    }

    /**
     * Switches to the previous element and get it, or null if it is "None."
     *
     * @return the previous element and get it, or null if it is "None."
     */
    @Nullable
    public S previous() {
        if (current == null) {
            current = values.get(values.size() - 1);
        }
        else {
            final int previousOrdinal = ordinal() - 1;

            if (previousOrdinal < 0) {
                current = null;
            }
            else {
                current = values.get(previousOrdinal);
            }
        }

        return current;
    }

    private int ordinal() {
        return ordinal(current);
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
