package me.hapyl.fight.game.talents.tamer.pack;

import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.attribute.*;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.entity.Wolf;

import javax.annotation.Nonnull;

public class TheWolfPack extends TamerPack {
    
    @DisplayField(percentage = true) private final double wolfBaseAttackBoostPerWolf = 0.1d;
    @DisplayField private final int wolfAttackBoostDuration = 20;
    
    private final ModifierSource modifierSource = new ModifierSource(Key.ofString("the_wolf_pack"), true);
    
    public TheWolfPack() {
        super(
                "The Wolf Pack", """
                                 &nEach&a alive&7 wolf grants you an %s boost.
                                 """.formatted(AttributeType.ATTACK), TalentType.ENHANCE
        );
        
        attributes.setMaxHealth(25);
        setDurationSec(25);
    }
    
    @Override
    public void onSpawn(@Nonnull ActiveTamerPack pack, @Nonnull Location location) {
        pack.createEntity(location, Entities.WOLF, entity -> new TheWoldPackEntity(pack, entity));
    }
    
    @Override
    public int spawnAmount() {
        return 4;
    }
    
    @Nonnull
    @Override
    public String toString(ActiveTamerPack pack) {
        final int size = pack.getEntities().size();
        final Attribute attribute = AttributeType.ATTACK.attribute;
        
        return "%s %s".formatted(attribute.getColor().backingColor + attribute.getCharacter(), size);
    }
    
    private class TheWoldPackEntity extends TamerEntity {
        
        public TheWoldPackEntity(@Nonnull ActiveTamerPack pack, @Nonnull Wolf entity) {
            super(pack, entity);
        }
        
        @Override
        public void tick(int index) {
            super.tick(index);
            
            player.getAttributes().addModifier(
                    modifierSource, wolfAttackBoostDuration, modifier -> modifier.of(
                            AttributeType.ATTACK,
                            ModifierType.MULTIPLICATIVE,
                            scaleUltimateEffectiveness(player, (1 + index) * wolfBaseAttackBoostPerWolf)
                    )
            );
        }
        
    }
}
