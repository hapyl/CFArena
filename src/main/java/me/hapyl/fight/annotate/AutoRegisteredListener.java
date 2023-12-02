package me.hapyl.fight.annotate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Annotates that this class is automatically registered if {@link org.bukkit.event.Listener} is present.
 */
@Target(ElementType.TYPE)
public @interface AutoRegisteredListener {
}
