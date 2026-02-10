package me.hapyl.fight.game.attribute;

import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.damage.DamageFlag;
import me.hapyl.fight.game.damage.DamageType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public final class AttributeCalculator {
    
    public static final double DEFENSE_SCALING;
    public static final double ATTACK_SPEED_SCALING;
    
    private static final Map<DamageType, AttributeType> CAUSE_DAMAGE_BONUS_MAP;
    
    static {
        DEFENSE_SCALING = 0.5d;
        ATTACK_SPEED_SCALING = Math.PI * 2 / 10;
        
        CAUSE_DAMAGE_BONUS_MAP = Map.of(
                DamageType.DIRECT_MELEE, AttributeType.DIRECT_DAMAGE_BONUS,
                DamageType.DIRECT_RANGE, AttributeType.DIRECT_DAMAGE_BONUS,
                DamageType.TALENT, AttributeType.TALENT_DAMAGE_BONUS,
                DamageType.ULTIMATE, AttributeType.ULTIMATE_DAMAGE_BONUS
        );
    }
    
    private final BaseAttributes attributes;
    
    public AttributeCalculator(@Nonnull BaseAttributes attributes) {
        this.attributes = attributes;
    }
    
    public double outgoingHealing(double healing) {
        return healing * attributes.normalized(AttributeType.MENDING);
    }
    
    public double incomingHealing(double healing) {
        return healing * attributes.normalized(AttributeType.VITALITY);
    }
    
    public double incomingDamage(double damage, @Nonnull DamageCause cause) {
        // True damage ignores defense so assume 0 defense
        if (cause.hasFlag(DamageFlag.BREACH_DAMAGE)) {
            return defense(damage, 0);
        }
        
        return defense(damage, attributes.normalized(AttributeType.DEFENSE));
    }
    
    public double defense(double damage, double defense) {
        return damage / (defense * DEFENSE_SCALING + (1 - DEFENSE_SCALING));
    }
    
    public double outgoingDamage(double damage, @Nonnull DamageCause cause) {
        final double causeBonus = damageCauseBonus(cause);
        
        return damage * attributes.normalized(AttributeType.ATTACK) * (1 + causeBonus);
    }
    
    public int rangeAttackSpeed(int speed) {
        final double attackSpeed = attributes.normalized(AttributeType.ATTACK_SPEED);
        
        return (int) Math.max(0, speed / (attackSpeed * ATTACK_SPEED_SCALING + (1 - ATTACK_SPEED_SCALING)));
    }
    
    public boolean dodge() {
        return attributes.random.checkBound(AttributeType.DODGE);
    }
    
    public boolean critical() {
        return attributes.random.checkBound(AttributeType.CRIT_CHANCE);
    }
    
    public boolean critical(double critChance) {
        return attributes.random.checkBound(critChance);
    }
    
    public int attackCooldown(double cooldown) {
        return (int) (cooldown * (1 / attributes.normalized(AttributeType.ATTACK_SPEED)));
    }
    
    public int ferocityStrikes() {
        final double ferocity = attributes.normalized(AttributeType.FEROCITY);
        
        int strikes = (int) ferocity;
        double remainder = ferocity % 1;
        
        if (remainder > 0.0d && attributes.random.checkBound(remainder)) {
            strikes++;
        }
        
        return strikes;
    }
    
    public double damageCauseBonus(@Nonnull DamageCause cause) {
        @Nullable final AttributeType type = CAUSE_DAMAGE_BONUS_MAP.get(cause.type());
        
        return type != null ? attributes.normalized(type) : 0;
    }
}
