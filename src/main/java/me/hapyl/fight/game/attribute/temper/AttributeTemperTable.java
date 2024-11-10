package me.hapyl.fight.game.attribute.temper;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.util.collection.ConcurrentTable;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.entity.LivingGameEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * A map (a table, rather) of tempers and their values.
 * <p>
 * A map is structured like a table:
 * <pre>
 *       Temper
 *      |------|--------|---------|
 * Type |      | Attack | Defense |
 *      |------|--------|---------|
 *      | A    | 69     | 0       |
 *      |------|--------|---------|
 *      | B    | 0.1    | 1       |
 *      |------|--------|---------|
 * </pre>
 * Where two keys point to a single value.
 * <p>
 * Type can hold as many different tempers and tempers can hold as many different types.
 * But only ony single temper and one single type can point to the same value.
 * <p>
 * Temper <code>A</code> can modify as many types as there are, but will override the existing type.
 */
public final class AttributeTemperTable implements Iterable<TemperData> {

    private final ConcurrentTable<Temper, AttributeType, AttributeTemper> tempers;
    private final EntityAttributes attributes;

    public AttributeTemperTable(EntityAttributes attributes) {
        this.attributes = attributes;
        this.tempers = new ConcurrentTable<>();
    }

    public void add(@Nonnull Temper temper, @Nonnull AttributeType type, double value, int duration, boolean isBuff, @Nullable LivingGameEntity applier) {
        if (tempers.contains(temper, type)) {
            remove(temper, type);
        }

        tempers.put(temper, type, new AttributeTemper(value, duration, isBuff, applier) {
            @Override
            public void run() {
                remove(temper, type);
                attributes.triggerUpdate(type);
            }
        });
    }

    public double get(@Nonnull AttributeType type) {
        return get(null, type);
    }

    public double get(@Nonnull Temper temper) {
        return get(temper, null);
    }

    /**
     * Gets the temper values for the gives temper and/or type.
     * This method accepts either both values, one or none. Though if
     * none are provided, the return value is always 0.0d.
     * <ul>
     *     <li>
     *         Providing <b>only the temper</b> will look for all the values done by that temper.
     *     </li>
     *     <li>
     *         Providing <b>only the type</b> will look for all the values done to that type.
     *     </li>
     *     <li>
     *         Providing <b>both temper and type</b> will only look for values done by that temper to that type.
     *     </li>
     * </ul>
     *
     * @param temper - Temper.
     * @param type   - Type.
     * @return the values done by either temper/type or both.
     */
    public double get(@Nullable Temper temper, @Nullable AttributeType type) {
        double total = 0.0d;

        if (temper == null && type == null) {
            return total;
        }

        for (AttributeTemper at : tempers.matchOR(temper, type)) {
            total += at.value;
        }

        return total;
    }

    public boolean has(Temper temper) {
        return tempers.containsRow(temper);
    }

    public boolean has(Temper temper, AttributeType type) {
        return tempers.contains(temper, type);
    }

    public void cancelAll() {
        tempers.forEach(AttributeTemper::cancel);
        tempers.clear();
    }

    @Nonnull
    @Override
    public Iterator<TemperData> iterator() {
        final Map<Temper, TemperData> map = Maps.newHashMap();

        tempers.entrySet().forEach(entry -> {
            final ConcurrentTable.Cell<Temper, AttributeType> cell = entry.getKey();
            final Temper temper = cell.row();
            final AttributeTemper attributeTemper = entry.getValue();

            map.compute(temper, (t, d) -> {
                (d = d != null ? d : new TemperData(temper)).values.put(cell.column(), attributeTemper);

                return d;
            });

        });

        return map.values().iterator();
    }

    public boolean isEmpty() {
        return tempers.isEmpty();
    }

    public void cancel(@Nonnull Temper temper) {
        final Collection<AttributeTemper> attributeTempers = tempers.removeAll(temper);

        attributeTempers.forEach(AttributeTemper::cancel);
    }

    public void putAll(@Nonnull AttributeTemperTable table) {
        tempers.putAll(table.tempers);
    }

    public void clear() {
        tempers.clear();
    }

    private void remove(@Nonnull Temper temper, @Nonnull AttributeType type) {
        final AttributeTemper remove = tempers.remove(temper, type);

        if (remove == null) {
            return;
        }

        remove.cancel();
        tempers.remove(temper, type);
    }
}
