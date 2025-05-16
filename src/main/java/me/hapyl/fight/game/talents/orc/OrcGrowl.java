package me.hapyl.fight.game.talents.orc;


import me.hapyl.eterna.module.math.Geometry;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.math.geometry.Quality;
import me.hapyl.eterna.module.math.geometry.WorldParticle;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.ModifierSource;
import me.hapyl.fight.game.attribute.ModifierType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class OrcGrowl extends Talent {
    
    @DisplayField private final int debuffDuration = Tick.fromSecond(6);
    @DisplayField private final double distance = 8.0;
    @DisplayField(scale = 100) private final double attackDecrease = -0.2d;
    @DisplayField private final double speedDecrease = -50;
    
    private final ModifierSource modifierSource = new ModifierSource(Key.ofString("scared"));
    
    public OrcGrowl(@Nonnull Key key) {
        super(key, "Growl of a Beast");
        
        setDescription("""
                       Growl with your &a&lbeautiful&7 and &4dealy&7 voice, scaring enemies in moderate range, &eimpairing&7 and &3slowing&7 them down.
                       """
        );
        
        setType(TalentType.IMPAIR);
        setMaterial(Material.GOAT_HORN);
        setDurationSec(5);
        setCooldownSec(20);
    }
    
    @Override
    public @Nullable Response execute(@Nonnull GamePlayer player) {
        final Location location = player.getLocation();
        
        // This for zoom Fx
        player.addPotionEffect(PotionEffectType.SLOWNESS, 3, 10);
        
        Collect.nearbyEntities(location, distance).forEach(entity -> {
            if (player.isSelfOrTeammate(entity)) {
                return;
            }
            
            entity.getAttributes().addModifier(
                    modifierSource, debuffDuration, player, modifier -> modifier
                            .of(AttributeType.ATTACK, ModifierType.ADDITIVE, attackDecrease)
                            .of(AttributeType.SPEED, ModifierType.FLAT, speedDecrease)
            );
        });
        
        // Fx
        // Todo -> You lazy piece of shit add an actual effect instead of circle
        Geometry.drawCircleAnchored(location, distance, Quality.HIGH, new WorldParticle(Particle.ENCHANTED_HIT, 3, 0, 0, 0, 0.025f), 1.0d);
        player.playWorldSound(location, Sound.ENTITY_ENDER_DRAGON_GROWL, 2.0f);
        
        return Response.OK;
    }
}
