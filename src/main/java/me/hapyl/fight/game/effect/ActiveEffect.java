package me.hapyl.fight.game.effect;

import me.hapyl.eterna.module.util.Removable;
import me.hapyl.eterna.module.util.RomanNumber;
import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.fight.CF;
import me.hapyl.fight.game.Constants;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.util.CFUtils;
import org.bukkit.Location;
import org.bukkit.Particle;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.SimpleDateFormat;

public class ActiveEffect implements Ticking, Removable {
    
    private static final SimpleDateFormat DURATION_FORMAT = new SimpleDateFormat("mm:ss");
    
    private final LivingGameEntity entity;
    private final Effect effect;
    private final int duration;
    
    protected int amplifier;
    protected int tick;
    
    @Nullable private LivingGameEntity applier;
    
    public ActiveEffect(@Nonnull LivingGameEntity entity, @Nullable LivingGameEntity applier, @Nonnull Effect effect, int amplifier, int duration) {
        this.entity = entity;
        this.applier = applier;
        this.effect = effect;
        this.amplifier = amplifier;
        this.duration = duration;
        this.tick = duration;
        
        // Call onStart
        effect.onStart(this);
    }
    
    @Nonnull
    public LivingGameEntity entity() {
        return entity;
    }
    
    @Nullable
    public LivingGameEntity applier() {
        return applier;
    }
    
    @Nonnull
    public Effect effect() {
        return effect;
    }
    
    public int amplifier() {
        return amplifier;
    }
    
    public void amplifier(int amplifier) {
        this.amplifier = amplifier;
    }
    
    public int duration() {
        return duration;
    }
    
    public void tick(int tick) {
        this.tick = tick;
    }
    
    @Override
    public void tick() {
        effect.onTick(this);
        
        // Do not tick infinite effects
        if (isInfiniteDuration()) {
            return;
        }
        
        // Actually tick down
        tick--;
    }
    
    @Override
    public void remove() {
        effect.onStop(this);
    }
    
    @Override
    public boolean shouldRemove() {
        if (isInfiniteDuration()) {
            return false;
        }
        
        return tick < 0 || entity.isDead() || effect.shouldRemove(this);
    }
    
    public boolean isInfiniteDuration() {
        return duration == Constants.INFINITE_DURATION;
    }
    
    public void applier(@Nullable LivingGameEntity applier) {
        this.applier = applier;
    }
    
    @Override
    public String toString() {
        return "%s%s %s &f%s".formatted(
                effect.getType().getColor(),
                effect.getName(),
                RomanNumber.toRoman(amplifier + 1),
                isInfiniteDuration() ? CFUtils.INF_CHAR : DURATION_FORMAT.format(tick * 50L)
        );
    }
    
    public <T> void particle(@Nonnull Location location, @Nonnull Particle particle, int amount, double offsetX, double offsetY, double offsetZ, float speed, @Nullable T data) {
        CF.getPlayers().forEach(player -> {
            // Do not display particle for self because it's obstructing
            if (player.equals(entity)) {
                return;
            }
            
            player.spawnParticle(location, particle, amount, offsetX, offsetY, offsetZ, speed, data);
        });
    }
    
    public void particle(@Nonnull Location location, @Nonnull Particle particle, int amount, double offsetX, double offsetY, double offsetZ, float speed) {
        particle(location, particle, amount, offsetX, offsetY, offsetZ, speed, null);
    }
    
    public void particle(@Nonnull Location location, @Nonnull Particle particle, int amount) {
        particle(location, particle, amount, 0, 0, 0, 0f);
    }
}
