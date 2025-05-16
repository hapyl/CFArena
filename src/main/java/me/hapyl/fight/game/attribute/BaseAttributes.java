package me.hapyl.fight.game.attribute;

import com.google.common.collect.Maps;
import me.hapyl.eterna.builtin.Debuggable;
import me.hapyl.eterna.module.annotate.SelfReturn;
import me.hapyl.fight.game.color.Color;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

public class BaseAttributes implements Iterable<AttributeView>, Debuggable {
    
    protected final Map<AttributeType, Double> attributes;
    protected final AttributeRandom random;
    
    public BaseAttributes() {
        this.attributes = Maps.newHashMap();
        this.random = new AttributeRandom(this);
        
        // Write defaults
        for (AttributeType type : AttributeType.values()) {
            this.attributes.put(type, type.defaultValue());
        }
    }
    
    public BaseAttributes(@Nonnull BaseAttributes other) {
        this();
        
        // Copy base attributes
        this.attributes.putAll(other.attributes);
    }
    
    public double get(@Nonnull AttributeType type) {
        return attributes.getOrDefault(type, 0.0);
    }
    
    public final double base(@Nonnull AttributeType type) {
        return attributes.getOrDefault(type, 0.0);
    }
    
    public double normalized(@Nonnull AttributeType type) {
        return get(type) / 100;
    }
    
    public void set(@Nonnull AttributeType type, double value) {
        this.attributes.put(type, value);
    }
    
    public void add(@Nonnull AttributeType type, double value) {
        set(type, base(type) + value);
    }
    
    public void subtract(@Nonnull AttributeType type, double value) {
        add(type, -value);
    }
    
    @SelfReturn
    public BaseAttributes put(@Nonnull AttributeType type, double value) {
        set(type, value);
        return this;
    }
    
    public void reset() {
        for (AttributeType type : AttributeType.values()) {
            reset(type);
        }
    }
    
    public void reset(@Nonnull AttributeType type) {
        this.attributes.put(type, type.defaultValue());
    }
    
    public void zero() {
        this.attributes.replaceAll((type, value) -> 0.0);
    }
    
    public double getMaxHealth() {
        return get(AttributeType.MAX_HEALTH);
    }
    
    public void setMaxHealth(double value) {
        set(AttributeType.MAX_HEALTH, value);
    }
    
    public double getAttack() {
        return get(AttributeType.ATTACK);
    }
    
    public void setAttack(double value) {
        set(AttributeType.ATTACK, value);
    }
    
    public double getDefense() {
        return get(AttributeType.DEFENSE);
    }
    
    public void setDefense(double value) {
        set(AttributeType.DEFENSE, value);
    }
    
    public double getSpeed() {
        return get(AttributeType.SPEED);
    }
    
    public void setSpeed(double value) {
        set(AttributeType.SPEED, value);
    }
    
    public double getCritChance() {
        return get(AttributeType.CRIT_CHANCE);
    }
    
    public void setCritChance(double value) {
        set(AttributeType.CRIT_CHANCE, value);
    }
    
    public double getCritDamage() {
        return get(AttributeType.CRIT_DAMAGE);
    }
    
    public void setCritDamage(double value) {
        set(AttributeType.CRIT_DAMAGE, value);
    }
    
    public double getFerocity() {
        return get(AttributeType.FEROCITY);
    }
    
    public void setFerocity(double value) {
        set(AttributeType.FEROCITY, value);
    }
    
    public double getMending() {
        return get(AttributeType.MENDING);
    }
    
    public void setMending(double value) {
        set(AttributeType.MENDING, value);
    }
    
    public double getVitality() {
        return get(AttributeType.VITALITY);
    }
    
    public void setVitality(double value) {
        set(AttributeType.VITALITY, value);
    }
    
    public double getDodge() {
        return get(AttributeType.DODGE);
    }
    
    public void setDodge(double value) {
        set(AttributeType.DODGE, value);
    }
    
    public double getFatigue() {
        return get(AttributeType.FATIGUE);
    }
    
    public void setFatigue(double value) {
        set(AttributeType.FATIGUE, value);
    }
    
    public double getAttackSpeed() {
        return get(AttributeType.ATTACK_SPEED);
    }
    
    public void setAttackSpeed(double value) {
        set(AttributeType.ATTACK_SPEED, value);
    }
    
    public double getKnockbackResistance() {
        return get(AttributeType.KNOCKBACK_RESISTANCE);
    }
    
    public void setKnockbackResistance(double value) {
        set(AttributeType.KNOCKBACK_RESISTANCE, value);
    }
    
    public double getEffectResistance() {
        return get(AttributeType.EFFECT_RESISTANCE);
    }
    
    public void setEffectResistance(double value) {
        set(AttributeType.EFFECT_RESISTANCE, value);
    }
    
    public double getHeight() {
        return get(AttributeType.HEIGHT);
    }
    
    public void setHeight(double value) {
        set(AttributeType.HEIGHT, value);
    }
    
    public double getEnergyRecharge() {
        return get(AttributeType.ENERGY_RECHARGE);
    }
    
    public void setEnergyRecharge(double value) {
        set(AttributeType.ENERGY_RECHARGE, value);
    }
    
    public double getJumpStrength() {
        return get(AttributeType.JUMP_STRENGTH);
    }
    
    public void setJumpStrength(double value) {
        set(AttributeType.JUMP_STRENGTH, value);
    }
    
    public double getDirectDamageBonus() {
        return get(AttributeType.DIRECT_DAMAGE_BONUS);
    }
    
    public void setDirectDamageBonus(double value) {
        set(AttributeType.DIRECT_DAMAGE_BONUS, value);
    }
    
    public double getTalentDamageBonus() {
        return get(AttributeType.TALENT_DAMAGE_BONUS);
    }
    
    public void setTalentDamageBonus(double value) {
        set(AttributeType.TALENT_DAMAGE_BONUS, value);
    }
    
    public double getUltimateDamageBonus() {
        return get(AttributeType.ULTIMATE_DAMAGE_BONUS);
    }
    
    public void setUltimateDamageBonus(double value) {
        set(AttributeType.ULTIMATE_DAMAGE_BONUS, value);
    }
    
    @Nonnull
    public final AttributeCalculator calculate() {
        return new AttributeCalculator(this);
    }
    
    @Nonnull
    public String getLore(@Nonnull AttributeType type) {
        return " &7" + type.getName() + ": " + getStar(type);
    }
    
    @Nonnull
    public String getStar(@Nonnull AttributeType type) {
        double defaultValue = type.defaultValue();
        double value = get(type);
        double ratio = value / defaultValue;
        
        final int star = Math.max(1, Math.min(5, (int) Math.round(3 * ratio)));
        
        final String character = type.getCharacter();
        final Color color = type.getColor();
        
        return color + character.repeat(star) + Color.DARK_GRAY + character.repeat(5 - star);
    }
    
    @Nonnull
    @Override
    public Iterator<AttributeView> iterator() {
        return Stream.of(AttributeType.values())
                     .map(attributeType -> new AttributeView(attributeType, get(attributeType)))
                     .iterator();
    }
    
    @Nonnull
    @Override
    public String toDebugString() {
        return "BaseAttributes{" +
                "attributes=" + attributes.entrySet().stream().map(entry -> "%s=%.1f".formatted(entry.getKey().name(), entry.getValue())).toList() +
                '}';
    }
}
