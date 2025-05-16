package me.hapyl.fight.game.heroes.ultimate;

import me.hapyl.fight.game.attribute.AttributeType;

import javax.annotation.Nullable;

public interface Resource {
    
    /**
     * Gets the base amount of resource regenerated every second.
     *
     * @return the base amount of resource regenerated every second.
     */
    double passive();
    
    /**
     * Gets the base amount of resource regenerated upon player elimination.
     *
     * @return the base amount of resource regenerated upon player elimination.
     */
    double playerElimination();
    
    /**
     * Gets the attribute the base amount of resource is multiplied by, or {@code null} if not multiplied.
     *
     * @return the attribute the base amount of resource is multiplied by, or {@code null} if not multiplied.
     */
    @Nullable
    AttributeType effectiveAttribute();
    
}
