package me.hapyl.fight.game.talents.heavy_knight;

import me.hapyl.eterna.module.math.Geometry;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.math.geometry.WorldParticle;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.effect.EffectType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.RomanInt;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Uppercut extends Talent {
    
    @DisplayField(suffix = " blocks") private final double range = 5.0d;
    @DisplayField private final double height = 3.0d;
    @DisplayField private final double damage = 2.5d;
    
    @DisplayField private final int impairDuration = Tick.fromSecond(5);
    @DisplayField private final RomanInt dazeAmplifier = RomanInt.of(2);
    
    public Uppercut(@Nonnull Key key) {
        super(key, "Uppercut");
        
        setDescription("""
                       Perform an uppercut attack that launches &ayou&7 and &cenemies&7 into the air, dealing &cdamage&7 and applying %s.
                       
                       Launched enemies fall down slowly, &eimpairing&7 their movement.
                       """.formatted(EffectType.DAZE)
        );
        
        setType(TalentType.IMPAIR);
        setMaterial(Material.RABBIT_FOOT);
        
        setCooldownSec(6);
    }
    
    @Override
    public @Nullable Response execute(@Nonnull GamePlayer player) {
        final Location location = player.getLocation();
        final Vector vector = location.getDirection().normalize().setY(0.0d);
        final Vector upVelocity = BukkitUtils.vector3Y(height);
        
        location.add(vector.multiply(3.0d));
        
        Collect.nearbyEntities(location, range, player::isNotSelfOrTeammateOrHasEffectResistance)
               .forEach(entity -> {
                   HeroRegistry.SWORD_MASTER.addSuccessfulTalent(player, this);
                   
                   entity.damageNoKnockback(damage, player, DamageCause.UPPERCUT);
                   entity.setVelocity(upVelocity);
                   
                   entity.addEffect(EffectType.LINGER, 5, impairDuration, player);
                   entity.addEffect(EffectType.DAZE, dazeAmplifier.toInt(), impairDuration, player);
               });
        
        player.setVelocity(upVelocity);
        player.addEffect(EffectType.SLOW_FALLING, 5, getDuration());
        
        location.add(0, 0.2d, 0);
        
        // Fx
        player.playWorldSound(location, Sound.ENTITY_IRON_GOLEM_HURT, 0.75f);
        player.playWorldSound(location, Sound.ENTITY_ENDER_DRAGON_FLAP, 0.0f);
        
        Geometry.drawPolygon(location, 6, range, new WorldParticle(Particle.CRIT));
        
        return Response.OK;
    }
}
