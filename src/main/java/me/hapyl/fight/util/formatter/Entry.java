package me.hapyl.fight.util.formatter;

public class Entry<E> {

    private final Class<E> clazz;
    private final String format;

    public Entry(Class<E> clazz, String format) {
        this.clazz = clazz;
        this.format = format;
    }

    // override this for custom formatting
    public String display(E e) {
        return String.valueOf(e);
    }

    public final String format(Object e) {
        return this.format + display((E) e);
    }

}
