package me.hapyl.fight.util;

import me.hapyl.eterna.module.util.CollectionUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

import javax.annotation.Nonnull;

public final class ComponentUtils {

    public static Component showText(@Nonnull Component... components) {
        final TextComponent.Builder builder = Component.text();

        for (int i = 0; i < components.length; i++) {
            final Component component = components[i];

            if (component == null) {
                builder.appendNewline();
                continue;
            }

            builder.append(component);

            // Don't append last new line
            if (i < components.length - 1) {
                builder.appendNewline();
            }
        }

        return builder.build();
    }
    
    @Nonnull
    public static Component random(@Nonnull Component... components) {
        return CollectionUtils.randomElementOrFirst(components);
    }

}
