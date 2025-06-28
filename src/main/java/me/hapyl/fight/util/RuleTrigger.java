package me.hapyl.fight.util;

import javax.annotation.Nonnull;

public abstract class RuleTrigger {
    
    private static final Rule defaultRule = new Rule() {
        @Override
        public int hitRule() {
            return 3;
        }
        
        @Override
        public long timeRule() {
            return 2500L;
        }
    };
    
    private final Rule rule;
    
    protected int hits;
    protected long lastTrigger;
    
    public RuleTrigger(@Nonnull Rule rule) {
        this.rule = rule;
        this.lastTrigger = 0L; // 0 allows the first hit to always trigger
    }
    
    public abstract void trigger();
    
    public void tryTrigger() {
        final long currentTimeMillis = System.currentTimeMillis();
        final long timeSinceLastTrigger = currentTimeMillis - lastTrigger;
        
        // Time rule has priority
        if (timeSinceLastTrigger >= rule.timeRule()) {
            trigger();
            
            // Reset both hits and timer
            hits = 0;
            lastTrigger = currentTimeMillis;
            return;
        }
        
        if (hits++ >= rule.hitRule()) {
            trigger();
            
            // Only reset hits
            hits = 0;
        }
    }
    
    @Nonnull
    public static Rule defaultRule() {
        return defaultRule;
    }
    
    public interface Rule {
        
        int hitRule();
        
        long timeRule();
        
    }
}
