package me.hapyl.fight.game.entity.commission;

import me.hapyl.eterna.module.inventory.Equipment;
import me.hapyl.eterna.module.npc.ClickType;
import me.hapyl.eterna.module.npc.Npc;
import me.hapyl.eterna.module.npc.NpcAnimation;
import me.hapyl.eterna.module.npc.NpcProperties;
import me.hapyl.eterna.module.npc.appearance.AppearanceBuilder;
import me.hapyl.eterna.module.npc.appearance.AppearanceHumanoid;
import me.hapyl.fight.event.DamageInstance;
import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.bukkit.entity.Husk;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.OverridingMethodsMustInvokeSuper;

/**
 * Overlay entities are special entity type consisting of an actual entity (always a Husk) and
 * a {@link Overlay} that follows it the "mimics" the entity actions.
 */
public class CommissionOverlayEntity extends CommissionEntity {
    
    private final Overlay overlay;
    
    public CommissionOverlayEntity(@Nonnull CommissionOverlayEntityType type, @Nonnull Husk husk) {
        super(type, husk);
        
        this.overlay = new Overlay(this);
    }
    
    @Nonnull
    @Override
    public CommissionOverlayEntityType type() {
        return (CommissionOverlayEntityType) super.type();
    }
    
    @Override
    @OverridingMethodsMustInvokeSuper
    public void onTick(int tick) {
        // Sync the overlay to the entity
        overlay.setLocation(getLocation());
        
        // Sync equipment
        overlay.getAppearance(AppearanceHumanoid.class).setEquipment(Equipment.of(entity));
    }
    
    @OverridingMethodsMustInvokeSuper
    @Override
    public void onRemove() {
        super.onRemove();
        
        overlay.destroy();
    }
    
    @Override
    public void onDamageDealt(@Nonnull DamageInstance instance) {
        // Simulate swing
        overlay.playAnimation(NpcAnimation.SWING_MAIN_HAND);
    }
    
    @OverridingMethodsMustInvokeSuper
    @Override
    public void onDamageTaken(@Nonnull DamageInstance instance) {
        super.onDamageTaken(instance);
        
        // Play damage animation & sound
        overlay.playAnimation(NpcAnimation.TAKE_DAMAGE);
        
        playWorldSound(Sound.ENTITY_PLAYER_HURT, 1.0f);
    }
    
    @Nonnull
    public Overlay overlay() {
        return this.overlay;
    }
    
    public static class Overlay extends Npc {
        
        private final CommissionOverlayEntity entity;
        
        private Overlay(@Nonnull CommissionOverlayEntity entity) {
            super(
                    entity.getLocation(), Component.empty(),
                    AppearanceBuilder.ofMannequin(entity.type().skin())
            );
            
            this.entity = entity;
            
            // Make sure we can always damage the entity
            final NpcProperties properties = getProperties();
            properties.setInteractionDelay(0);
            
            // Show to everyone by default
            showAll();
        }
        
        @Override
        public void onClick(@Nonnull Player player, @Nonnull ClickType type) {
            if (type != ClickType.ATTACK) {
                return;
            }
            
            player.attack(entity.entity());
        }
    }
    
}
