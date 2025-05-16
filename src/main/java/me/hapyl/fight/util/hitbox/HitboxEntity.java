package me.hapyl.fight.util.hitbox;

import me.hapyl.fight.game.attribute.BaseAttributes;
import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Slime;

import javax.annotation.Nonnull;

public abstract class HitboxEntity extends LivingGameEntity implements Hitbox {
    
    /// @see Hitbox#create(Location, double, double, Hitbox)
    protected HitboxEntity(@Nonnull Slime slime, double scale) {
        super(slime, defaultAttributes());
        
        // Have to rescale the entity because of attributes
        rescale(scale);
    }
    
    public void rescale(double newScale) {
        setAttributeValue(Attribute.SCALE, Math.clamp(newScale, 1, 10));
    }
    
    @Nonnull
    @Override
    public String getName() {
        return "Hitbox";
    }
    
    @Override
    public final void onRemove() {
        super.onRemove();
        this.onDespawn();
    }
    
    private static BaseAttributes defaultAttributes() {
        final BaseAttributes attributes = new BaseAttributes();
        
        attributes.setEffectResistance(100);
        
        return attributes;
    }
    
}
