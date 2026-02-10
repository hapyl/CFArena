package me.hapyl.fight.game.talents.rogue;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.effect.EffectType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Swayblade extends Talent {
    
    @DisplayField(suffix = " blocks") private final double radius = 2.5d;
    @DisplayField private final float maxYawShift = 60.0f;
    @DisplayField private final float maxPitchShift = 40.0f;
    
    public Swayblade(@Nonnull Key key) {
        super(key, "Swayblade");
        
        setDescription("""
                       Hit all &cenemies&7 in front of you with the &bhilt&7 of your blade, &eimpairing&7 their vision.
                       """
        );
        
        setMaterial(Material.GOLD_NUGGET);
        setType(TalentType.IMPAIR);
        setCooldownSec(4);
    }
    
    public void affect(@Nonnull GamePlayer player, @Nonnull LivingGameEntity entity) {
        final Location entityLocation = entity.getLocation();
        entityLocation.setYaw(entityLocation.getYaw() + player.random.nextFloatBool(maxYawShift + 1));
        entityLocation.setPitch(entityLocation.getPitch() + player.random.nextFloatBool(maxPitchShift + 1));
        
        entity.addEffect(EffectType.BLINDNESS, 10);
        entity.addEffect(EffectType.NAUSEA, 20);
        
        entity.teleport(entityLocation);
        entity.triggerDebuff(player);
    }
    
    @Override
    public @Nullable Response execute(@Nonnull GamePlayer player) {
        final Location location = player.getLocationInFrontFromEyes(1);
        
        Collect.nearbyEntities(location, radius, player::isNotSelfOrTeammateOrHasEffectResistance).forEach(entity -> affect(player, entity));
        
        // Fx
        player.swingMainHand();
        
        player.playWorldSound(Sound.ENTITY_PLAYER_ATTACK_KNOCKBACK, 0.75f);
        player.playWorldSound(Sound.ENTITY_PLAYER_ATTACK_STRONG, 0.75f);
        
        player.spawnWorldParticle(location, Particle.SWEEP_ATTACK, 1);
        
        return Response.OK;
    }
    
}
