package me.hapyl.fight.game.dot;

import me.hapyl.eterna.module.annotate.EventLike;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.registry.Keyed;
import me.hapyl.eterna.module.util.Described;
import me.hapyl.fight.CF;
import me.hapyl.fight.annotate.AutoRegisteredListener;
import me.hapyl.fight.game.NamedColoredPrefixed;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.terminology.EnumTerm;
import me.hapyl.fight.util.RuleTrigger;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Range;

import javax.annotation.Nonnull;
import java.util.Objects;

@AutoRegisteredListener
public abstract class Dot implements NamedColoredPrefixed, Keyed, Described {
    
    private final Key key;
    private final String prefix;
    private final String name;
    private final Color color;
    
    private final int maxStacks;
    private final int affectPeriod;
    
    @Nonnull private String description;
    @Nonnull private RuleTrigger.Rule cooldownRule;
    
    Dot(
            @Nonnull Key key,
            @Nonnull String prefix,
            @Nonnull String name,
            @Nonnull Color color,
            @Range(from = 1, to = Integer.MAX_VALUE) int affectPeriod,
            @Range(from = 1, to = Integer.MAX_VALUE) int maxStacks
    ) {
        this.key = key;
        this.prefix = prefix;
        this.name = name;
        this.description = "No description.";
        this.color = color;
        this.maxStacks = maxStacks;
        this.affectPeriod = affectPeriod;
        this.cooldownRule = RuleTrigger.defaultRule();
        
        if (this instanceof Listener listener) {
            CF.registerEvents(listener);
        }
    }
    
    @Nonnull
    public RuleTrigger.Rule cooldownRule() {
        return cooldownRule;
    }
    
    public void cooldownRule(@Nonnull RuleTrigger.Rule cooldownRule) {
        this.cooldownRule = cooldownRule;
    }
    
    @Nonnull
    @Override
    public String getDescription() {
        return description;
    }
    
    @Override
    public void setDescription(@Nonnull String description) {
        this.description = """
                           %s
                           &b🧊 &6On Exhaust
                           %s
                           """.formatted(description, exhaustDescription());
    }
    
    public int affectPeriod() {
        return affectPeriod;
    }
    
    public int maxStacks() {
        return maxStacks;
    }
    
    public abstract void affect(@Nonnull DotInstance instance);
    
    public abstract void exhaust(@Nonnull DotInstance instance);
    
    @Nonnull
    public String exhaustDescription() {
        return "Does nothing.";
    }
    
    @EventLike
    public void onStart(@Nonnull DotInstance instance) {
    }
    
    @EventLike
    public void onStop(@Nonnull DotInstance instance) {
    }
    
    @EventLike
    public void onTick(@Nonnull DotInstance instance) {
    }
    
    @Nonnull
    public DotInstance newInstance(@Nonnull LivingGameEntity entity) {
        return new DotInstance(entity, this);
    }
    
    @Nonnull
    @Override
    public Key getKey() {
        return key;
    }
    
    @Nonnull
    @Override
    public String getPrefix() {
        return prefix;
    }
    
    @Nonnull
    @Override
    public String getName() {
        return name;
    }
    
    @Nonnull
    @Override
    public Color getColor() {
        return color;
    }
    
    /**
     * Gets the string representation of this DoT following the pattern:
     * <p>`Name <u>DoT</u>`</p>
     *
     * @return the string representation of this DoT.
     */
    @Override
    @Nonnull
    public String toString() {
        return toString0() + " " + EnumTerm.DAMAGE_OVER_TIME;
    }
    
    @Override
    public final boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        
        final Dot that = (Dot) o;
        return Objects.equals(this.key, that.key);
    }
    
    @Override
    public final int hashCode() {
        return Objects.hashCode(this.key);
    }
}
