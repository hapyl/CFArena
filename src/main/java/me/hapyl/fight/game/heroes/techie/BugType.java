package me.hapyl.fight.game.heroes.techie;

import com.google.common.collect.Sets;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.CollectionUtils;
import me.hapyl.eterna.module.util.Described;
import me.hapyl.fight.game.Constants;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.ModifierSource;
import me.hapyl.fight.game.attribute.ModifierType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashSet;

public enum BugType implements Described {
    
    TYPE_A("&4ᚨ", AttributeType.ATTACK, -0.3d),
    TYPE_D("&2ᛞ", AttributeType.DEFENSE, -0.3d),
    TYPE_S("&3ᛊ", AttributeType.ENERGY_RECHARGE, -0.5d);
    
    private final ModifierSource modifierSource;
    private final String name;
    private final String description;
    private final AttributeType type;
    private final double value;
    
    BugType(@Nonnull String name, @Nonnull AttributeType type, double value) {
        this.modifierSource = new ModifierSource(Key.ofString("bug_" + name().toLowerCase()), true);
        this.name = name;
        this.description = "Decreases %s.".formatted(type);
        this.type = type;
        this.value = value;
    }
    
    @Nonnull
    @Override
    public String getName() {
        return name;
    }
    
    @Nonnull
    @Override
    public String getDescription() {
        return description;
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    public void temper(@Nonnull LivingGameEntity entity, @Nonnull GamePlayer player) {
        entity.getAttributes().addModifier(modifierSource, Constants.INFINITE_DURATION, player, modifier -> modifier.of(type, ModifierType.ADDITIVE, value));
    }
    
    @Nullable
    public static BugType random(@Nonnull Collection<BugType> existingBugs) {
        final HashSet<BugType> hashSet = Sets.newHashSet(values());
        hashSet.removeAll(existingBugs);
        
        if (hashSet.isEmpty()) {
            return null;
        }
        
        return CollectionUtils.randomElement(hashSet);
    }
}
