package me.hapyl.fight.util.displayfield;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public interface DisplayFieldProvider {
    
    /**
     * Gets a {@link List} of extra display fields for this object.
     *
     * <p>Returns {@code null} if copying <b><u>to</u></b> this object is <b><u>not</u></b> supported.</p>
     *
     * @return a list of extra display fields.
     * @see #copyDisplayFieldsFrom(DisplayFieldProvider)
     * @see #copyDisplayFieldsTo(DisplayFieldProvider)
     */
    @Nullable
    default List<DisplayFieldInstance> extraDisplayFields() {
        return null;
    }
    
    /**
     * Copies {@link DisplayField}s from the given {@link DisplayFieldProvider} to here.
     *
     * @param provider - The provider to copy the fields from.
     */
    default void copyDisplayFieldsFrom(@Nonnull DisplayFieldProvider provider) {
        copy0(provider, this);
    }
    
    /**
     * Copies {@link DisplayField}s from here to the given {@link DisplayFieldProvider}
     *
     * @param provider - The provider to copy the fields to.
     */
    default void copyDisplayFieldsTo(@Nonnull DisplayFieldProvider provider) {
        copy0(this, provider);
    }
    
    private static void copy0(DisplayFieldProvider from, DisplayFieldProvider to) {
        final List<DisplayFieldInstance> fields = to.extraDisplayFields();
        
        if (fields != null) {
            fields.addAll(DisplayFieldSerializer.serialize(from));
        }
    }
    
}