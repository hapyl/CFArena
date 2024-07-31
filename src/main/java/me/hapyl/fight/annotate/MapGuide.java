package me.hapyl.fight.annotate;

import javax.annotation.Nonnull;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

/**
 * Used as a guide for a {@link Map} with ambiguous key and values.
 * <br>
 * <pre><code>
 *     /@MapGuide(key = "level", value = "health")
 *     Map<Integer, Integer> healthMap;
 * </code></pre>
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ ElementType.FIELD })
public @interface MapGuide {

    /**
     * Description of the key.
     *
     * @return description of the key.
     */
    @Nonnull
    String key();

    /**
     * Description of the value.
     *
     * @return description of the value.
     */
    @Nonnull
    String value();

}

