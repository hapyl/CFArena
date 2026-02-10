package me.hapyl.fight.game.talents.zealot;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.fx.beam.Quadrant;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.attribute.ModifierSource;
import me.hapyl.fight.game.attribute.ModifierType;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BrokenHeartRadiation extends Talent {
    
    @DisplayField private final double beamLength = 5;
    @DisplayField private final double beamDamage = 5.0d;
    
    @DisplayField(percentage = true) private final double mendingReduction = -0.25d;
    @DisplayField(percentage = true) private final double defenseReduction = -0.33d;
    
    @DisplayField private final int effectDuration = 180;
    
    private final ModifierSource modifierSource = new ModifierSource(Key.ofString("broken_heart_radiation"));
    private final double yOffset = 0.75;
    
    public BrokenHeartRadiation(@Nonnull Key key) {
        super(key, "Broken Heart Radiation");
        
        setDescription("""
                       Create four radiation beams that spin around you for {duration}.
                       
                       If a beam touches an enemy, it deals &c{beamDamage} ❤&7 damage and reduces %s by &c{mendingReduction}&7 and %s by &c{defenseReduction}&7 for &b{effectDuration}.
                       """.formatted(AttributeType.VITALITY, AttributeType.DEFENSE)
        );
        
        setType(TalentType.IMPAIR);
        setMaterial(Material.TWISTING_VINES);
        setDurationSec(3);
        setCooldownSec(20);
    }
    
    @Override
    public @Nullable Response execute(@Nonnull GamePlayer player) {
        final Location location = player.getLocation();
        final Quadrant quadrant = new Quadrant(player.getLocation().subtract(0, yOffset, 0)) {
            @Override
            public void onTouch(@Nonnull LivingGameEntity entity) {
                if (player.isSelfOrTeammate(entity)) {
                    return;
                }
                
                final EntityAttributes attributes = entity.getAttributes();
                final boolean newTemper = !attributes.hasModifier(modifierSource);
                
                attributes.addModifier(
                        modifierSource, effectDuration, player, modifier -> modifier
                                .of(AttributeType.VITALITY, ModifierType.ADDITIVE, mendingReduction)
                                .of(AttributeType.DEFENSE, ModifierType.ADDITIVE, defenseReduction)
                );
                
                entity.damageNoKnockback(beamDamage, player, DamageCause.RADIATION);
                
                if (newTemper) {
                    entity.spawnParticle(entity.getLocation(), Particle.ELDER_GUARDIAN, 1, 0, 0, 0, 0);
                }
            }
            
            @Override
            public void onTick() {
                teleport(player.getLocation().subtract(0, yOffset, 0));
                
                if (player.isDeadOrRespawning() || getTick() >= getDuration()) {
                    cancel();
                }
            }
            
            @Override
            public void onTaskStop() {
                remove();
            }
        };
        
        quadrant.setDistance(beamLength);
        quadrant.runTaskTimer(0, 1);
        
        // Fx
        player.playWorldSound(location, Sound.ENTITY_ELDER_GUARDIAN_CURSE, 0.0f);
        
        return Response.OK;
    }
    
}
