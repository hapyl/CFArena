package me.hapyl.fight.annotate;

import org.bukkit.Location;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated parameter is cloned, either by using {@link Object#clone()} or creating a new instance of the object, before mutating it.
 * <p>Usually applied to {@link Location}, but supports any {@link Object}.
 */
@Target({ ElementType.PARAMETER })
public @interface ClonedBeforeMutation {
}
