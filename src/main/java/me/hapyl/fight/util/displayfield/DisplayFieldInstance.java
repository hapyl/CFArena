package me.hapyl.fight.util.displayfield;

import me.hapyl.fight.util.CFUtils;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class DisplayFieldInstance {
    
    private final Field field;
    private final DisplayField display;
    private final Object instance;
    
    private final String name;
    private final String value;
    
    DisplayFieldInstance(Field field, DisplayField display, Object instance) {
        this.field = field;
        this.display = display;
        this.instance = instance;
        
        // Parse name
        this.name = !display.name().isEmpty()
                    ? display.name()
                    : Arrays.stream(field.getName().replaceAll("([a-z])([A-Z])", "$1 $2").split("(?<=\\w)(?=\\p{Lu})"))
                            .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1))
                            .collect(Collectors.joining(" "));
        
        // Parse value
        this.value = ((Supplier<String>) () -> {
            try {
                field.setAccessible(true);
                
                // Parse field value
                double scale = display.scale();
                String suffix = display.suffix();
                
                // Handle percentages
                if (display.percentage()) {
                    scale = 100;
                    suffix = "%" + display.suffix();
                }
                
                final Object value = field.get(instance);
                
                return switch (value) {
                    // Double and Float use dynamic decimal place, based on the decimal
                    //  eg: 1.5 -> "1.5", 1.0 -> "1"
                    case Double decimal -> scaleFormat(decimal * scale);
                    case Float decimal -> scaleFormat(decimal * scale);
                    
                    // Integers are ALWAYS considered as ticks, use short for other cases
                    case Integer tick -> CFUtils.formatTick(tick);
                    
                    // Other numbers are parsed as integers
                    case Number number -> scaleFormat(number.intValue() * scale);
                    
                    // Other objects call toString()
                    default -> value.toString();
                } + suffix;
            }
            catch (Exception e) {
                e.printStackTrace();
                return "&4Error parsing DisplayField, see console!";
            }
        }).get();
    }
    
    @Nonnull
    public Field field() {
        return field;
    }
    
    @Nonnull
    public DisplayField display() {
        return display;
    }
    
    @Nonnull
    public Object instance() {
        return instance;
    }
    
    @Nonnull
    public String name() {
        return name;
    }
    
    @Nonnull
    public String value() {
        return value;
    }
    
    @Override
    public String toString() {
        return "&7%s: &f&l%s".formatted(name, value);
    }
    
    private static String scaleFormat(double v) {
        return v % 1 == 0 ? "%.0f".formatted(v) : "%.1f".formatted(v);
    }
}
