package me.hapyl.fight.cmds.extra;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.bukkit.entity.Player;
import org.bukkit.util.NumberConversions;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public abstract class Acceptor {

    private final Map<Integer, List<String>> additionalArguments;

    public Acceptor() {
        additionalArguments = Maps.newHashMap();
        this.createAdditionalArguments();
    }

    public abstract void execute(Player player, String[] args);

    public void createAdditionalArguments() {
    }

    public final void addArgument(int i, String s) {
        additionalArguments.computeIfAbsent(i, l -> Lists.newArrayList()).add(s);
    }

    public final void addArgument(int i, String... s) {
        additionalArguments.computeIfAbsent(i, l -> Lists.newArrayList()).addAll(Arrays.stream(s).toList());
    }

    public final Map<Integer, List<String>> additionalArguments() {
        return this.additionalArguments;
    }

    protected final boolean checkLength(String[] array, int length) {
        return array.length >= length;
    }

    protected final Object arrayValue(String[] array, int pos, Object def) {
        return pos >= array.length ? def : array[pos];
    }

    protected final int intValue(String[] array, int pos) {
        return intValue(array, pos, 0);
    }

    protected final int intValue(String[] array, int pos, int def) {
        return NumberConversions.toInt(arrayValue(array, pos, def));
    }

    protected final long longValue(String[] array, int pos) {
        return intValue(array, pos, 0);
    }

    protected final long longValue(String[] array, int pos, long def) {
        return NumberConversions.toLong(arrayValue(array, pos, def));
    }

    protected final double doubleValue(String[] array, int pos) {
        return doubleValue(array, pos, 0.0d);
    }

    protected final double doubleValue(String[] array, int pos, double def) {
        return NumberConversions.toDouble(arrayValue(array, pos, def));
    }

    protected final String stringValue(String[] array, int pos, String def) {
        final Object object = arrayValue(array, pos, def);
        return object == null ? def : object.toString();
    }

}
