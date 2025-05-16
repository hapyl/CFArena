package me.hapyl.fight.game.attribute;

import com.google.common.collect.Sets;
import me.hapyl.eterna.module.annotate.SelfReturn;
import me.hapyl.fight.game.Constants;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.task.GameTask;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Stream;

public class AttributeModifier extends GameTask implements Iterable<AttributeModifierEntry> {
    
    protected final Set<AttributeModifierEntry> entries;
    
    private final EntityAttributes attributes;
    private final ModifierSource source;
    private final int duration;
    @Nullable private final LivingGameEntity applier;
    
    AttributeModifier(@Nonnull EntityAttributes attributes, @Nonnull ModifierSource source, int duration, @Nullable LivingGameEntity applier) {
        this.attributes = attributes;
        this.source = source;
        this.entries = Sets.newLinkedHashSet();
        this.duration = duration;
        this.applier = applier;
        
        if (duration != Constants.INFINITE_DURATION) {
            runTaskLater(duration);
        }
    }
    
    @SelfReturn
    public AttributeModifier of(@Nonnull AttributeType type, @Nonnull ModifierType modifierType, double value) {
        entries.add(new AttributeModifierEntry(type, modifierType, value));
        return this;
    }
    
    @Override
    public void run() {
        // Don't think there is a need to directly call remove on a map because it's impossible
        // for modifier to NOT be this one, unless illegal modifications to the map are made
        this.attributes.removeModifier(source);
    }
    
    @Nonnull
    public BaseAttributes attributes() {
        return attributes;
    }
    
    @Nonnull
    public ModifierSource source() {
        return source;
    }
    
    @Nonnull
    public Stream<AttributeModifierEntry> valueOf(@Nonnull ModifierType type) {
        return entries.stream().filter(entry -> entry.modifierType() == type);
    }
    
    public int duration() {
        return duration;
    }
    
    @Nullable
    public LivingGameEntity applier() {
        return applier;
    }
    
    @Nonnull
    @Override
    public Iterator<AttributeModifierEntry> iterator() {
        return entries.iterator();
    }
}
