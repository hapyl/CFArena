package me.hapyl.fight.game.talents.tamer;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.ModifierSource;
import me.hapyl.fight.game.attribute.ModifierType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.InputTalent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public class TamingTheTime extends InputTalent implements TamerTimed {
    
    @DisplayField private final double radius = 20;
    @DisplayField private final double attackSpeedIncrease = 100;
    @DisplayField private final double speedIncrease = 50;
    
    private final ModifierSource modifierSource = new ModifierSource(Key.ofString("taming_the_wind"));
    
    public TamingTheTime(@Nonnull Key key) {
        super(key, "Taming the Time");
        
        setDescription("""
                       Equip concentrated time.
                       """
        );
        
        leftData.setAction("Impair Enemies");
        leftData.setDescription("""
                                Hinder nearby enemies by &eimpairing&7 their movement, decreasing their %s and %s.
                                """.formatted(AttributeType.ATTACK_SPEED, AttributeType.SPEED)
        );
        leftData.setType(TalentType.IMPAIR);
        leftData.setDurationSec(3);
        leftData.setCooldownSec(30);
        
        rightData.setAction("Accelerate");
        rightData.setDescription("""
                                 Enhance yourself by increasing your %s and %s.
                                 """.formatted(AttributeType.ATTACK_SPEED, AttributeType.SPEED)
        );
        rightData.setType(TalentType.ENHANCE);
        rightData.copyDurationAndCooldownFrom(leftData);
        
        setMaterial(Material.CLOCK);
    }
    
    @Nonnull
    @Override
    public Response onLeftClick(@Nonnull GamePlayer player) {
        final int duration = getDuration(player);
        final Location location = player.getLocation();
        
        Collect.nearbyEntities(location, radius, player::isNotSelfOrTeammateOrHasEffectResistance)
               .forEach(enemy -> {
                   enemy.getAttributes().addModifier(
                           modifierSource, duration, player, modifier -> modifier
                                   .of(AttributeType.ATTACK_SPEED, ModifierType.FLAT, -attackSpeedIncrease)
                                   .of(AttributeType.SPEED, ModifierType.FLAT, -speedIncrease)
                   );
                   
                   // Enemy Fx
                   enemy.playSound(Sound.ENTITY_ELDER_GUARDIAN_CURSE, 1.25f);
               });
        
        
        // Self fx
        player.playWorldSound(location, Sound.ENTITY_ELDER_GUARDIAN_CURSE, 0.75f);
        player.playWorldSound(location, Sound.ENTITY_ELDER_GUARDIAN_HURT, 0.0f);
        
        return Response.OK;
    }
    
    @Nonnull
    @Override
    public Response onRightClick(@Nonnull GamePlayer player) {
        final int duration = getDuration(player);
        
        player.getAttributes().addModifier(
                modifierSource, duration, modifier -> modifier
                        .of(AttributeType.ATTACK_SPEED, ModifierType.FLAT, attackSpeedIncrease)
                        .of(AttributeType.SPEED, ModifierType.FLAT, speedIncrease)
        );
        
        // Fx
        player.playWorldSound(Sound.ENTITY_ELDER_GUARDIAN_CURSE, 0.75f);
        
        return Response.OK;
    }
}
