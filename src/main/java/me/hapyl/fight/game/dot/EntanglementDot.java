package me.hapyl.fight.game.dot;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;

public class EntanglementDot extends Dot implements Listener {
    
    private final int maxSnareStacks = 5;
    
    EntanglementDot(@Nonnull Key key) {
        super(key, "\uD83E\uDEA2", "Entanglement", Color.PURPLE_SHADOW, 15, 20);
        
        setDescription("""
                       Deals periodic damage based on the number of &5Snare&7 stacks.
                       
                       &6Snare
                       When an entity with En
                       Each time an entity with %sEntanglement&7 is damaged, it gains one stack of &5Snare&7 up to &b%2$s&7 stacks.
                       
                       Each stack increase the damage of %1$s&7.
                       """.formatted(getColor(), maxSnareStacks));
    }
    
    @Override
    public void affect(@Nonnull DotInstance instance) {
    
    }
    
    @Override
    public void exhaust(@Nonnull DotInstance instance) {
    }
    
    @Nonnull
    @Override
    public EntanglementDotInstance newInstance(@Nonnull LivingGameEntity entity) {
        return new EntanglementDotInstance(entity);
    }
    
    public class EntanglementDotInstance extends DotInstance {
        
        private int snare;
        
        EntanglementDotInstance(@Nonnull LivingGameEntity entity) {
            super(entity, EntanglementDot.this);
        }
    }
}
