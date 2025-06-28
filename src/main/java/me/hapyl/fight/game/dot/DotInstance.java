package me.hapyl.fight.game.dot;

import me.hapyl.eterna.module.util.Removable;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.util.RuleTrigger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DotInstance implements Removable {
    
    private final LivingGameEntity entity;
    private final Dot dot;
    private final RuleTrigger ruleTrigger;
    
    @Nullable private LivingGameEntity applier;
    private int stacks;
    
    DotInstance(@Nonnull LivingGameEntity entity, @Nonnull Dot dot) {
        this.entity = entity;
        this.dot = dot;
        this.ruleTrigger = new RuleTrigger(dot.cooldownRule()) {
            @Override
            public void trigger() {
                if (applier != null) {
                    entity.triggerDebuff(applier);
                }
            }
        };
    }
    
    @Nonnull
    public LivingGameEntity entity() {
        return entity;
    }
    
    @Nonnull
    public Dot dot() {
        return dot;
    }
    
    public int stacks() {
        return stacks;
    }
    
    @Nullable
    public LivingGameEntity applier() {
        return applier;
    }
    
    public void incrementStacks(int stacks, @Nullable LivingGameEntity applier) {
        this.stacks = Math.min(this.stacks + stacks, dot.maxStacks());
        
        if (applier != null) {
            this.applier = applier;
        }
    }
    
    public void setStacksMax(int stacks, @Nullable LivingGameEntity applier) {
        this.stacks = Math.clamp(stacks, this.stacks, dot.maxStacks());
        
        if (applier != null) {
            this.applier = applier;
        }
    }
    
    @Override
    public void remove() {
        // Call exhaust method
        dot.exhaust(this);
    }
    
    @Override
    public boolean removeIfShould() {
        return stacks <= 0;
    }
    
    public void tick() {
        dot.onTick(this);
    }
    
    public void affect() { // This only ticks when aliveTicks % period == 0
        if (entity.hasEffectResistanceAndNotify(applier)) {
            return;
        }
        
        stacks--;
        dot.affect(this);
        
        ruleTrigger.tryTrigger();
    }
}
