package me.hapyl.fight.util.displayfield;

import java.lang.reflect.Field;

public class DisplayFieldData {

    protected final Field field;
    protected final DisplayField displayField;
    protected final Object instance;

    protected DisplayFieldData(Field field, DisplayField displayField, Object instance) {
        this.field = field;
        this.displayField = displayField;
        this.instance = instance;
    }

}
