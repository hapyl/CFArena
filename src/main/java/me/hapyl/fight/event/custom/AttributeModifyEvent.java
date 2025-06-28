package me.hapyl.fight.event.custom;

import me.hapyl.fight.game.attribute.AttributeModifier;
import me.hapyl.fight.game.attribute.AttributeModifierEntry;
import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Predicate;

public class AttributeModifyEvent extends GameEntityEvent implements Cancellable {
    
    private static final HandlerList HANDLER_LIST = new HandlerList();
    
    private final AttributeModifier modifier;
    private boolean cancel;
    
    public AttributeModifyEvent(@Nonnull LivingGameEntity entity, @Nonnull AttributeModifier modifier) {
        super(entity);
        
        this.modifier = modifier;
    }
    
    @Nonnull
    public final AttributeModifier modifier() {
        return modifier;
    }
    
    @Nullable
    public LivingGameEntity getApplier() {
        return modifier().applier();
    }
    
    public int getDuration() {
        return modifier().duration();
    }
    
    @Nonnull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
    
    public boolean hasModification(@Nonnull ModificationType type) {
        for (AttributeModifierEntry entry : modifier()) {
            if (type.test(entry)) {
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public boolean isCancelled() {
        return cancel;
    }
    
    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
    
    @Nonnull
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
    
    @Nonnull
    public static AttributeModifyEvent createDummyEvent(@Nonnull LivingGameEntity entity, @Nonnull LivingGameEntity applier, boolean isBuff) {
        return new AttributeModifyDummyEvent(entity, applier, isBuff ? ModificationType.BUFF : ModificationType.DEBUFF);
    }
    
    public enum ModificationType implements Predicate<AttributeModifierEntry> {
        BUFF {
            @Override
            public boolean test(@Nonnull AttributeModifierEntry entry) {
                final double value = entry.value();
                
                return entry.attributeType().isBuff(value, -value);
            }
        },
        DEBUFF {
            @Override
            public boolean test(@Nonnull AttributeModifierEntry entry) {
                return BUFF.negate().test(entry);
            }
        };
        
    }
    
    public static class AttributeModifyDummyEvent extends AttributeModifyEvent {
        
        private final LivingGameEntity applier;
        private final ModificationType modificationType;
        
        AttributeModifyDummyEvent(LivingGameEntity entity, LivingGameEntity applier, ModificationType modificationType) {
            super(entity, AttributeModifier.dummyModifier(entity));
            
            this.applier = applier;
            this.modificationType = modificationType;
        }
        
        @Nullable
        @Override
        public LivingGameEntity getApplier() {
            return applier;
        }
        
        @Override
        public int getDuration() {
            return 0;
        }
        
        @Override
        public boolean hasModification(@Nonnull ModificationType type) {
            return type == modificationType;
        }
    }
}
