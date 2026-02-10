package me.hapyl.fight.game.attribute;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.math.Numbers;
import me.hapyl.fight.game.Constants;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public enum AttributeType implements IAttribute {
    
    MAX_HEALTH(
            new Attribute("Max Health", "Increases the maximum health.") {
                @Override
                public void update(@Nonnull LivingGameEntity entity, double value) {
                    final double health = entity.getHealth();
                    
                    if (health > value) {
                        entity.setHealth(value);
                    }
                    
                    // Players also need to update their Vanilla hearts
                    if (entity instanceof GamePlayer player) {
                        player.updateHealth();
                    }
                }
                
                @Override
                public double defaultValue() {
                    return 100;
                }
                
                @Override
                public double minValue() {
                    // Since the update method tempers with vanilla health, and we all know how bugged vanilla is,
                    // the actual minimum value is 0.5, so the player's don't die and get stuck in "limbo" state.
                    return 0.5;
                }
                
                @Override
                public double maxValue() {
                    return 1_000_000_000;
                }
            }.character("❤")
             .color(Color.RED)
    ),
    
    ATTACK(
            new Attribute("Attack", "Increases the outgoing damage.") {
                @Override
                public double defaultValue() {
                    return 100;
                }
                
                @Override
                public double maxValue() {
                    return 10_000;
                }
            }.character("🗡")
             .color(Color.DARK_RED)
    ),
    
    DEFENSE(
            new Attribute("Defense", "Decreases the incoming damage.") {
                @Override
                public double defaultValue() {
                    return 100;
                }
            }.character("🛡")
             .color(Color.DARK_GREEN)
    ),
    
    SPEED(
            new Attribute("Speed", "Increases the movement speed.") {
                
                private static final double VANILLA_SCALE = 0.002;
                
                @Override
                public void update(@Nonnull LivingGameEntity entity, double value) {
                    final double vanillaValue = value * VANILLA_SCALE;
                    
                    if (entity.getWalkSpeed() == vanillaValue) {
                        return;
                    }
                    
                    entity.setWalkSpeed(Numbers.clamp1neg1((float) vanillaValue));
                }
                
                @Override
                public double defaultValue() {
                    return 100;
                }
                
                @Override
                public double minValue() {
                    // Minimum speed values make the player controls backwards, could be implemented for something funny.
                    return -100;
                }
                
                @Override
                public double maxValue() {
                    return 500;
                }
            }.character("🌊")
             .color(Color.AQUA)
             .format(AttributeFormat.PERCENTAGE)
    ),
    
    CRIT_CHANCE(
            new Attribute("Crit Chance", "Increases the change to score a critical hit.") {
                @Override
                public double defaultValue() {
                    return 15;
                }
                
                @Override
                public double minValue() {
                    return -100;
                }
                
                @Override
                public double maxValue() {
                    return 200;
                }
            }.character("☣")
             .color(Color.BLUE)
             .format(AttributeFormat.PERCENTAGE)
    ),
    
    CRIT_DAMAGE(
            new Attribute("Crit Damage", "Increases the damage of a critical hit.") {
                @Override
                public double defaultValue() {
                    return 50;
                }
            }.character("☠")
             .color(Color.BLUE)
             .format(AttributeFormat.PERCENTAGE)
    ),
    
    FEROCITY(
            new Attribute("Ferocity", "Increases the chance to perform an additional attack.") {
                @Override
                public double defaultValue() {
                    return 0;
                }
                
                @Override
                public double maxValue() {
                    // Divide by 100 to get the maximum number of strikes.
                    return 500;
                }
            }.character("\uD83C\uDF00")
             .color(Color.RED)
             .format(AttributeFormat.PERCENTAGE)
    ),
    
    MENDING(
            new Attribute("Mending", "Increases outgoing healing.") {
                @Override
                public double defaultValue() {
                    return 100;
                }
            }.character("🌿")
             .color(Color.GREEN)
             .format(AttributeFormat.PERCENTAGE)
    ),
    
    VITALITY(
            new Attribute("Vitality", "Increases incoming healing.") {
                @Override
                public double defaultValue() {
                    return 100;
                }
            }.character("&l\uD83E\uDE78")
             .color(Color.DARK_RED)
             .format(AttributeFormat.PERCENTAGE)
    ),
    
    DODGE(
            new Attribute("Dodge", "Increases the chance to dodge the attack.") {
                @Override
                public double defaultValue() {
                    return 0;
                }
                
                @Override
                public double maxValue() {
                    return 80;
                }
            }.character("\uD83D\uDC65")
             .color(Color.GOLD)
             .format(AttributeFormat.PERCENTAGE)
    ),
    
    FATIGUE(
            new Attribute("Fatigue", "Increases the cooldown of all talents and abilities, making them take longer to recharge.") {
                @Override
                public double defaultValue() {
                    return 100;
                }
            }.character("\uD83D\uDCA4")
             .color(Color.DARK_GREEN)
             .format(AttributeFormat.PERCENTAGE)
    ),
    
    ATTACK_SPEED(
            new Attribute("Attack Speed", "Decreases the cooldown between your attacks.") {
                
                private static final NavigableMap<Double, PotionEffect> effectMap;
                
                static {
                    effectMap = new TreeMap<>();
                    
                    effectMap.put(125d, makeEffect(PotionEffectType.HASTE, 0));
                    effectMap.put(175d, makeEffect(PotionEffectType.HASTE, 1));
                    effectMap.put(200d, makeEffect(PotionEffectType.HASTE, 2));
                    effectMap.put(0d, makeEffect(PotionEffectType.MINING_FATIGUE, 8));
                    effectMap.put(12.5d, makeEffect(PotionEffectType.MINING_FATIGUE, 7));
                    effectMap.put(25d, makeEffect(PotionEffectType.MINING_FATIGUE, 6));
                    effectMap.put(37.5d, makeEffect(PotionEffectType.MINING_FATIGUE, 5));
                    effectMap.put(50d, makeEffect(PotionEffectType.MINING_FATIGUE, 4));
                    effectMap.put(62.5d, makeEffect(PotionEffectType.MINING_FATIGUE, 3));
                    effectMap.put(75d, makeEffect(PotionEffectType.MINING_FATIGUE, 2));
                    effectMap.put(87.5d, makeEffect(PotionEffectType.MINING_FATIGUE, 1));
                    effectMap.put(100d, makeEffect(PotionEffectType.MINING_FATIGUE, 0));
                }
                
                @Override
                @SuppressWarnings("deprecation")
                public void update(@Nonnull LivingGameEntity entity, double value) {
                    entity.removePotionEffect(PotionEffectType.HASTE);
                    entity.removePotionEffect(PotionEffectType.MINING_FATIGUE);
                    
                    // Apply either haste or mining fatigue for effect only
                    final Map.Entry<Double, PotionEffect> entry = effectMap.floorEntry(value);
                    
                    if (entry != null) {
                        entity.addPotionEffect(entry.getValue());
                    }
                }
                
                @Override
                public double defaultValue() {
                    return 100;
                }
                
                @Override
                public double minValue() {
                    return 10;
                }
                
                @Override
                public double maxValue() {
                    return 200;
                }
                
                private static PotionEffect makeEffect(PotionEffectType type, int amplifier) {
                    return new PotionEffect(type, Constants.INFINITE_DURATION, amplifier, false, false, false);
                }
            }.character("⚔")
             .color(Color.YELLOW)
             .format(AttributeFormat.PERCENTAGE)
    ),
    
    KNOCKBACK_RESISTANCE(
            new Attribute("Knockback Resistance", "Increases the resistance to knockback.") {
                @Override
                public void update(@Nonnull LivingGameEntity entity, double value) {
                    // Fixme -> doesn't seem to work for some reason even though the attribute is 1.0 in game @ 1.21.4
                    entity.setAttributeValue(org.bukkit.attribute.Attribute.KNOCKBACK_RESISTANCE, value / 100);
                }
                
                @Override
                public double defaultValue() {
                    return 0;
                }
                
                @Override
                public double maxValue() {
                    return 100;
                }
            }.character("🦏")
             .color(Color.DARK_AQUA)
    ),
    
    EFFECT_RESISTANCE(
            new Attribute("Effect Resistance", "Increases the chance to resist negative effects.") {
                @Override
                public double defaultValue() {
                    return 0;
                }
                
                @Override
                public double maxValue() {
                    return 100;
                }
            }.character("&l&5\uD83D\uDC1A")
             .color(Color.DARK_PURPLE)
    ),
    HEIGHT(
            new Attribute("Height", "Increases the height.") {
                
                private static final double VANILLA_SCALE = 180;
                
                @Override
                public void update(@Nonnull LivingGameEntity entity, double value) {
                    entity.setAttributeValue(org.bukkit.attribute.Attribute.SCALE, value / VANILLA_SCALE);
                }
                
                @Override
                public double defaultValue() {
                    return 180;
                }
            }.character("\uD83D\uDCCF")
             .color(Color.GREEN)
             .format("%.0f cb"::formatted)
    ),
    
    ENERGY_RECHARGE(
            new Attribute("Energy Recharge", "Increases how fast your energy is generated.") {
                @Override
                public double defaultValue() {
                    return 100;
                }
            }.character("♻")
             .color(Color.DARK_AQUA)
    ),
    
    JUMP_STRENGTH(
            new Attribute("Jump Strength", "Increases how high you can jump.") {
                
                private static final double VANILLA_SCALE = 0.0045;
                
                @Override
                public void update(@Nonnull LivingGameEntity entity, double value) {
                    entity.setAttributeValue(org.bukkit.attribute.Attribute.JUMP_STRENGTH, value * VANILLA_SCALE);
                }
                
                @Override
                public double defaultValue() {
                    return 100;
                }
                
            }.character("🐇")
             .color(Color.AQUA)
             .format(AttributeFormat.PERCENTAGE)
    ),
    
    DIRECT_DAMAGE_BONUS(
            new Attribute("Direct Damage Bonus", "Increases the damage of your direct attacks.") {
                @Override
                public double defaultValue() {
                    return 0;
                }
            }.character("&l⋏")
             .color(Color.WHITE)
             .format(AttributeFormat.PERCENTAGE)
    ),
    
    TALENT_DAMAGE_BONUS(
            new Attribute("Talent Damage Bonus", "Increases the damage of your talents.") {
                @Override
                public double defaultValue() {
                    return 0;
                }
            }.character("&l⋏")
             .color(Color.YELLOW)
             .format(AttributeFormat.PERCENTAGE)
    ),
    
    ULTIMATE_DAMAGE_BONUS(
            new Attribute("Ultimate Damage Bonus", "Increases the damage of your ultimate.") {
                @Override
                public double defaultValue() {
                    return 0;
                }
            }.character("&l⋏")
             .color(Color.AQUA)
             .format(AttributeFormat.PERCENTAGE)
    ),
    
    EFFECT_HIT_RATE(
            new Attribute("Effect Hit Rate", "Increases the chance for negative effects or crowd control to land.") {
                @Override
                public double defaultValue() {
                    return 0;
                }
                
                @Override
                public double maxValue() {
                    return 1.0;
                }
            }.character("\uD83D\uDD78")
             .color(Color.DIAMOND)
             .format(AttributeFormat.PERCENTAGE)
    ),
    
    ;
    
    private static final List<String> NAMES;
    
    static {
        NAMES = Lists.newArrayList();
        
        for (AttributeType value : values()) {
            NAMES.add(value.name());
        }
    }
    
    public final Attribute attribute;
    
    AttributeType(@Nonnull Attribute attribute) {
        this.attribute = attribute;
    }
    
    public final boolean isMandatory() {
        return switch (this) {
            case MAX_HEALTH, ATTACK, DEFENSE, SPEED, CRIT_CHANCE, CRIT_DAMAGE, ATTACK_SPEED -> true;
            default -> false;
        };
    }
    
    @Override
    public double defaultValue() {
        return attribute.defaultValue();
    }
    
    public double minValue() {
        return attribute.minValue();
    }
    
    public double maxValue() {
        return attribute.maxValue();
    }
    
    @Nonnull
    @Override
    public String getName() {
        return attribute.getName();
    }
    
    @Nonnull
    @Override
    public String getDescription() {
        return attribute.getDescription();
    }
    
    @Nonnull
    public String getCharacter() {
        return attribute.getCharacter();
    }
    
    @Nonnull
    @Override
    public Color getColor() {
        return attribute.getColor();
    }
    
    @Nonnull
    @Override
    public String toString() {
        return attribute.toString();
    }
    
    @Nonnull
    public String toString(double value) {
        return attribute.toString(value);
    }
    
    @Nonnull
    public String getFormatted(@Nonnull BaseAttributes attributes) {
        return "%s%s %s".formatted(attribute.getColor(), attribute.getCharacter(), attribute.toString(attributes.get(this)));
    }
    
    @Nonnull
    public String getFormattedWithAttributeName(@Nonnull BaseAttributes attributes) {
        return "%s &f%s".formatted(attribute.toString(), attribute.toString(attributes.get(this)));
    }
    
    @Nonnull
    public static List<String> listNames() {
        return Lists.newArrayList(NAMES);
    }
    
}

