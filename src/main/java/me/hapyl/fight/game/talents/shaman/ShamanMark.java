package me.hapyl.fight.game.talents.shaman;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Constants;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.ModifierSource;
import me.hapyl.fight.game.attribute.ModifierType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.heroes.shaman.ShamanData;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.util.CFUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;

public class ShamanMark extends TickingGameTask {
    
    private static final ModifierSource modifierSource = new ModifierSource(Key.ofString("shamans_mark"));
    private static final Particle.DustTransition dustTransition = new Particle.DustTransition(Color.fromRGB(48, 242, 86), Color.fromRGB(16, 122, 55), 1);;
    
    private final ShamanMarkTalent talent;
    private final ShamanData data;
    private final LivingGameEntity entity;
    
    private int noLosTicks;
    
    public ShamanMark(ShamanMarkTalent talent, ShamanData data, LivingGameEntity entity) {
        this.talent = talent;
        this.data = data;
        this.entity = entity;
        
        runTaskTimer(0, 1);
    }
    
    @Override
    public void onTaskStart() {
        entity.getAttributes().addModifier(
                modifierSource, Constants.INFINITE_DURATION, data.player, modifier -> modifier
                        .of(AttributeType.ATTACK, ModifierType.MULTIPLICATIVE, talent.attackIncrease)
                        .of(AttributeType.SPEED, ModifierType.FLAT, talent.speedIncrease)
                        .of(AttributeType.ATTACK_SPEED, ModifierType.FLAT, talent.attackSpeedIncrease)
        );
        
        // Fx
        entity.playWorldSound(Sound.ENTITY_FROG_HURT, 0.5f);
        entity.playWorldSound(Sound.ENTITY_FROG_DEATH, 0.0f);
    }
    
    @Override
    public void onTaskStop() {
        entity.getAttributes().removeModifier(modifierSource);
        
        // Fx
        entity.playWorldSound(Sound.ENTITY_GOAT_HORN_BREAK, 0.0f);
    }
    
    @Override
    public void run(int tick) {
        final GamePlayer player = data.player;
        
        // Los check
        noLosTicks++;
        
        if (player.hasLineOfSight(entity)) {
            noLosTicks = 0;
        }
        
        // Notify if los was broken
        if (noLosTicks == 1) {
            player.playSound(Sound.ENTITY_FROG_HURT, 1.25f);
        }
        
        // Break if entity is dead or out of los for too long
        if (entity.isDeadOrRespawning() || noLosTicks >= talent.outOfSightDuration) {
            HeroRegistry.SHAMAN.getPlayerData(player).mark = null;
            cancel();
            return;
        }
        
        // Fx
        final Location location = player.getMidpointLocation();
        final Location destination = entity.getMidpointLocation();
        final double distance = CFUtils.distance(location, destination);
        
        for (double d = 0; d < distance; d += 0.1) {
            final double progress = d / distance;
            final double x = location.getX() + (destination.getX() - location.getX()) * progress;
            final double y = location.getY() + (destination.getY() - location.getY()) * progress + Math.sin(Math.toRadians(tick) * 2) * 0.05;
            final double z = location.getZ() + (destination.getZ() - location.getZ()) * progress;
            
            location.set(x, y, z);
            player.spawnWorldParticle(location, Particle.DUST_COLOR_TRANSITION, 1, 0, 0, 0, 0, dustTransition);
        }
    }
    
}
