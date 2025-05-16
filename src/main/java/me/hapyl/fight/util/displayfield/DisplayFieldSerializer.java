package me.hapyl.fight.util.displayfield;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.inventory.ItemBuilder;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.List;

public final class DisplayFieldSerializer {
    
    private DisplayFieldSerializer() {
    }
    
    @Nonnull
    public static List<DisplayFieldInstance> serialize(@Nonnull DisplayFieldProvider provider) {
        final List<DisplayFieldInstance> fields = Lists.newArrayList();
        
        for (Field field : provider.getClass().getDeclaredFields()) {
            final DisplayField display = field.getAnnotation(DisplayField.class);
            
            if (display != null) {
                fields.add(new DisplayFieldInstance(field, display, provider));
            }
        }
        
        // Append extra fields if present
        final List<DisplayFieldInstance> extraFields = provider.extraDisplayFields();
        
        if (extraFields != null) {
            fields.addAll(extraFields);
        }
        
        return fields;
    }
    
    public static void serialize(@Nonnull ItemBuilder builder, @Nonnull DisplayFieldProvider provider) {
        serialize(provider).forEach(instance -> builder.addLore(instance.toString()));
    }
    
    @Nonnull
    public static String serialize(@Nonnull String string, @Nonnull DisplayFieldProvider provider) {
        return serialize(provider)
                .stream()
                .reduce(
                        string,
                        (result, instance) -> result.replace("{%s}".formatted(instance.field().getName()), instance.value()),
                        (s1, s2) -> s1
                );
    }
    
}
