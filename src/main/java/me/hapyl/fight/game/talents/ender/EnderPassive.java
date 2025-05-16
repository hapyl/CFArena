package me.hapyl.fight.game.talents.ender;


import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.ModifierSource;
import me.hapyl.fight.game.attribute.ModifierType;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.PassiveTalent;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class EnderPassive extends PassiveTalent {
    
    @DisplayField(percentage = true) private final double healing = 0.06d;
    @DisplayField(percentage = true) private final double damage = 0.03d;
    @DisplayField(percentage = true) private final double attackBoost = 0.2d;
    
    @DisplayField private final int attackBoostDuration = 30;
    @DisplayField private final double damageRadius = 2.0d;
    
    private final ModifierSource modifierSource = new ModifierSource(Key.ofString("ender_skin"));
    
    public EnderPassive(@Nonnull Key key) {
        super(key, "Ender Skin");
        
        setDescription("""
                       With great power, comes great... strength!
                       Your skin is too weak for the &bwater&7, though on &dteleport&7:
                       
                       &8- &7Heal for &a{healing}&7 of %1$s.
                       &8- &7Deal damage equal to &c{damage}&7 of each &cenemy&7's %1$s.
                       &8- &7Gain &c{attackBoost} %2$s boost for a short duration.
                       """.formatted(AttributeType.MAX_HEALTH, AttributeType.ATTACK)
        );
        
        setMaterial(Material.ENDER_EYE);
    }
    
    // Handles the passive ability
    public void handleTeleport(@Nonnull GamePlayer gamePlayer) {
        gamePlayer.healRelativeToMaxHealth(healing);
        gamePlayer.getAttributes().addModifier(modifierSource, attackBoostDuration, modifier -> modifier.of(AttributeType.ATTACK, ModifierType.ADDITIVE, attackBoost));
        
        Collect.nearbyEntities(gamePlayer.getLocation(), damageRadius).forEach(entity -> {
            if (gamePlayer.isSelfOrTeammate(entity)) {
                return;
            }
            
            final double damageDealt = entity.getMaxHealth() * damage;
            
            entity.damage(damageDealt, gamePlayer, DamageCause.ENDER_TELEPORT);
        });
    }
    
    @Override
    public boolean isDisplayAttributes() {
        return true;
    }
    
}
