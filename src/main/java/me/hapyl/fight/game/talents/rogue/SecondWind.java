package me.hapyl.fight.game.talents.rogue;


import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.ModifierSource;
import me.hapyl.fight.game.attribute.ModifierType;
import me.hapyl.fight.game.effect.Type;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.Outline;
import me.hapyl.fight.game.entity.Shield;
import me.hapyl.fight.game.talents.PassiveTalent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public class SecondWind extends PassiveTalent {
    
    @DisplayField(percentage = true) public final double passiveHealing = 1.5d;
    @DisplayField private final double shieldCapacity = 30.0d;
    
    @DisplayField private final double attackIncrease = 1.32;
    @DisplayField private final double fatigueDecrease = -0.5;
    
    private final ModifierSource modifierSource = new ModifierSource(Key.ofString("second_wind"));
    
    public SecondWind(@Nonnull Key key) {
        super(key, "Second Wind");
        
        setDescription("""
                       When taking &4lethal damage&7, instead of dying, gain %s for a short duration.
                       
                       &6%s
                       • Increases %s.
                       • Decreases %s.
                       • Clears all &4negative&7 effects.
                       • Creates a &eshield&7.
                       
                       If the &eshield&7 &cbreaks&7 before duration ends, you &cdie&7.
                       
                       If the &eshield&7 has &nnot&7 expired after the duration ends, convert &b{passiveHealing}&7 of remaining &eshield&7 into &ahealing&7.
                       """.formatted(Named.SECOND_WIND, Named.SECOND_WIND.getName(), AttributeType.ATTACK, AttributeType.FATIGUE)
        );
        
        setMaterial(Material.TOTEM_OF_UNDYING);
        setType(TalentType.ENHANCE);
        
        setDurationSec(6);
    }
    
    @Override
    public boolean isDisplayAttributes() {
        return true;
    }
    
    public void enter(@Nonnull GamePlayer player) {
        final int duration = getDuration();
        
        final Shield shield = new Shield(player, shieldCapacity) {
        };
        
        player.setShield(shield);
        player.setOutline(Outline.RED);
        
        player.getAttributes().addModifier(
                modifierSource, duration, modifier -> modifier
                        .of(AttributeType.ATTACK, ModifierType.MULTIPLICATIVE, attackIncrease)
                        .of(AttributeType.FATIGUE, ModifierType.MULTIPLICATIVE, fatigueDecrease)
        );
        
        // Clear negative effects
        player.removeEffectsByType(Type.NEGATIVE);
        
        player.schedule(
                () -> {
                    // If the state ended and still have shield, heal.
                    if (player.getShield() == shield) {
                        final double capacity = shield.getCapacity();
                        
                        player.heal(capacity * passiveHealing);
                        player.setOutline(Outline.CLEAR);
                        player.setShield(null);
                        
                        // Fx
                        player.playWorldSound(Sound.ENTITY_PLAYER_LEVELUP, 2.0f);
                    }
                }, duration
        );
        
        // Fx
        player.playWorldSound(Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1.25f);
        player.playWorldSound(Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 1.75f);
        
        player.spawnWorldParticle(Particle.TOTEM_OF_UNDYING, 15, 0.1, 0.3, 0.1, 0.75f);
    }
}
