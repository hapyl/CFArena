package me.hapyl.fight.game.heroes.harbinger;

import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.talents.TalentRegistry;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.potion.PotionEffectType;

public class Riptide implements Ticking {
    
    private final GamePlayer player;
    private final LivingGameEntity entity;
    
    protected int remainingTicks;
    protected long lastHit;
    
    Riptide(GamePlayer player, LivingGameEntity entity) {
        this.player = player;
        this.entity = entity;
    }
    
    public boolean isExpired() {
        return remainingTicks <= 0;
    }
    
    @Override
    public void tick() {
        remainingTicks--;
        
        // Fx
        final Location location = entity.getEyeLocation().add(0.0d, 0.2d, 0.0d);
        
        // Make sure to spawn particle to only player
        player.spawnParticle(location, Particle.SPLASH, 1, 0.15d, 0.5d, 0.15d, 0.01f);
        player.spawnParticle(location, Particle.GLOW, 1, 0.15d, 0.15d, 0.5d, 0.025f);
        
        // This is just for the effect
        entity.addPotionEffect(PotionEffectType.SPEED, 0, 20);
        entity.addPotionEffect(PotionEffectType.SLOWNESS, 0, 20);
    }
    
    public boolean isOnCooldown() {
        return System.currentTimeMillis() - lastHit < TalentRegistry.RIPTIDE.cooldown * 50L;
    }
}
