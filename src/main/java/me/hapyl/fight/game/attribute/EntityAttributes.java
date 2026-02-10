package me.hapyl.fight.game.attribute;

import com.google.common.collect.Maps;
import me.hapyl.fight.event.custom.AttributeModifyEvent;
import me.hapyl.fight.event.custom.AttributeUpdateEvent;
import me.hapyl.fight.game.element.PlayerElementHandler;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.talents.Timed;
import me.hapyl.fight.game.ui.display.StringDisplay;
import me.hapyl.fight.util.MapView;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.stream.Stream;

public class EntityAttributes extends BaseAttributes implements PlayerElementHandler {
    
    protected final LivingGameEntity entity;
    protected final Map<ModifierSource, AttributeModifier> modifiers;
    
    public EntityAttributes(@Nonnull LivingGameEntity entity, @Nonnull BaseAttributes attributes) {
        super(attributes); // Copy attributes
    
        this.entity = entity;
        this.modifiers = Maps.newLinkedHashMap();
    }
    
    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        modifiers.values().forEach(AttributeModifier::cancel);
        modifiers.clear();
    }
    
    @Override
    public double get(@Nonnull AttributeType type) {
        final double value = super.get(type);
        final double additiveBonus = getAdditiveBonus(type);
        final double multiplicativeBonus = getMultiplicativeBonus(type);
        final double flatBonus = getFlatBonus(type);
        
        return type.clamp(value * (1 + additiveBonus) * (1 + multiplicativeBonus) + flatBonus);
    }
    
    @Override
    public void set(@Nonnull AttributeType type, double value) {
        final double currentValue = super.get(type);
        
        if (new AttributeUpdateEvent(entity, type, currentValue, value).callEvent()) {
            return;
        }
        
        super.set(type, value);
        type.attribute.update(entity, value);
    }
    
    public void updateAttributes() {
        attributes.keySet().forEach(type -> type.attribute.update(entity, get(type)));
    }
    
    public void addModifier(@Nonnull ModifierSource source, int duration, @Nonnull AttributeModifierHandler handler) {
        addModifier(source, duration, null, handler);
    }
    
    public void addModifier(@Nonnull ModifierSource source, int duration, @Nullable LivingGameEntity applier, @Nonnull AttributeModifierHandler handler) {
        final AttributeModifier modifier = new AttributeModifier(this, source, duration, applier);
        
        // We have to handle here because even
        handler.handle(modifier);
        
        if (new AttributeModifyEvent(entity, modifier).callEvent()) {
            modifier.cancel();
            return;
        }
        
        final boolean newModifier = !hasModifier(source);
        
        // Remove previous modifier
        removeModifier(source);
        
        // Spawn display if first modifier and source isn't silent
        if (newModifier && !source.silent()) {
            modifier.forEach(this::display);
        }
        
        modifiers.put(source, modifier);
        updateAttributes();
    }
    
    public void addModifier(@Nonnull ModifierSource source, @Nonnull Timed timed, @Nonnull AttributeModifierHandler handler) {
        addModifier(source, timed, null, handler);
    }
    
    public void addModifier(@Nonnull ModifierSource source, @Nonnull Timed timed, @Nullable LivingGameEntity applier, @Nonnull AttributeModifierHandler handler) {
        addModifier(source, timed.getDuration(), applier, handler);
    }
    
    public boolean removeModifier(@Nonnull ModifierSource source) {
        final AttributeModifier modifier = modifiers.remove(source);
        
        if (modifier != null) {
            modifier.cancel();
            updateAttributes();
            return true;
        }
        
        return false;
    }
    
    public double getAdditiveBonus(@Nonnull AttributeType type) {
        return getBonus(type, ModifierType.ADDITIVE)
                .mapToDouble(AttributeModifierEntry::value)
                .sum();
    }
    
    public double getMultiplicativeBonus(@Nonnull AttributeType type) {
        return getBonus(type, ModifierType.MULTIPLICATIVE)
                .mapToDouble(modifier -> 1 + modifier.value())
                .reduce(1, (a, b) -> a * b) - 1;
    }
    
    public double getFlatBonus(@Nonnull AttributeType type) {
        return getBonus(type, ModifierType.FLAT)
                .mapToDouble(AttributeModifierEntry::value)
                .sum();
    }
    
    public boolean hasModifiers() {
        return !modifiers.isEmpty();
    }
    
    public boolean hasModifier(@Nonnull ModifierSource source) {
        return modifiers.containsKey(source);
    }
    
    public boolean hasModifier(@Nonnull ModifierSource source, @Nonnull AttributeType type) {
        for (AttributeModifier modifier : modifiers.values()) {
            if (modifier.source() != source) {
                continue;
            }
            
            for (AttributeModifierEntry entry : modifier.entries) {
                if (entry.attributeType() == type) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    @Nonnull
    public LivingGameEntity getEntity() {
        return entity;
    }
    
    @Override
    public String toString() {
        return entity.toString() + super.toString();
    }
    
    private void display(AttributeModifierEntry entry) {
        final Location location = entity.getMidpointLocation();
        final AttributeType type = entry.attributeType();
        final boolean isBuff = entry.value() > 0;
        
        if (isBuff) {
            StringDisplay.buff(location, type);
        }
        else {
            StringDisplay.debuff(location, type);
        }
    }
    
    @Nonnull
    public SnapshotAttributes snapshot() {
        final SnapshotAttributes snapshot = new SnapshotAttributes(entity);
        
        // Copy the "effective" values to "base" values of the copy, making them "snapshot"
        // the current values, even if modifiers have expired in the original
        for (AttributeType attributeType : attributes.keySet()) {
            snapshot.set(attributeType, get(attributeType));
        }
        
        return snapshot;
    }
    
    @Nonnull
    public MapView<ModifierSource, AttributeModifier> getModifiers() {
        return MapView.of(modifiers);
    }
    
    @Nonnull
    @Override
    public String toDebugString() {
        return "EntityAttributes{" +
                "attributes=" + attributes.entrySet().stream().map(entry -> "%s=%.1f".formatted(entry.getKey().name(), entry.getValue())).toList() +
                ", modifiers=" + modifiers.entrySet().stream().map(entry -> "%s=%s".formatted(entry.getKey(), entry.getValue())).toList() +
                ", entity=" + entity +
                '}';
    }
    
    public void removeModifiers() {
        modifiers.values().forEach(AttributeModifier::cancel);
        modifiers.clear();
    }
    
    private Stream<AttributeModifierEntry> getBonus(AttributeType attributeType, ModifierType modifierType) {
        return modifiers.values().stream()
                        .flatMap(modifier -> modifier.entries.stream())
                        .filter(entry -> entry.attributeType() == attributeType && entry.modifierType() == modifierType);
    }
}
