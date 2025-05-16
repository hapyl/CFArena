package me.hapyl.fight.game.cosmetic.death;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.Validate;
import me.hapyl.fight.game.cosmetic.Cosmetic;
import me.hapyl.fight.game.cosmetic.Display;
import me.hapyl.fight.game.cosmetic.Rarity;
import me.hapyl.fight.game.cosmetic.Type;
import me.hapyl.fight.game.task.TickingGameTask;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;

public class RitualCosmetic extends Cosmetic {
    
    public RitualCosmetic(@Nonnull Key key) {
        super(key, "Ritual", Type.DEATH);
        
        setDescription("What kind of ritual is that?");
        
        setRarity(Rarity.EPIC);
        setIcon(Material.BLAZE_POWDER);
    }
    
    @Override
    public void onDisplay(@Nonnull Display display) {
        new RitualCosmeticEffect(display, Particle.FLAME).runTaskTimer(0, 1);
    }
    
    public static class RitualCosmeticEffect extends TickingGameTask {
        
        private static final double RADIUS;
        private static final Set<Particle> SUPPORTED_PARTICLES;
        
        static {
            RADIUS = 3;
            SUPPORTED_PARTICLES = Set.of(
                    Particle.CAMPFIRE_COSY_SMOKE,
                    Particle.CAMPFIRE_SIGNAL_SMOKE,
                    Particle.CLOUD,
                    Particle.CRIT,
                    Particle.ENCHANTED_HIT,
                    Particle.DAMAGE_INDICATOR,
                    Particle.DRAGON_BREATH,
                    Particle.END_ROD,
                    Particle.FLAME,
                    Particle.REVERSE_PORTAL,
                    Particle.SCULK_CHARGE_POP,
                    Particle.SCULK_SOUL,
                    Particle.SMALL_FLAME,
                    Particle.LARGE_SMOKE,
                    Particle.SMOKE,
                    Particle.SOUL,
                    Particle.SOUL_FIRE_FLAME,
                    Particle.SPIT,
                    Particle.SQUID_INK,
                    Particle.TOTEM_OF_UNDYING
            );
        }
        
        private final Display display;
        private final Particle particle;
        private final Location centre;
        
        private double theta;
        
        public RitualCosmeticEffect(@Nonnull Display display, @Nonnull Particle particle) {
            this.display = display;
            this.particle = Validate.isTrue(particle, RitualCosmeticEffect::isSupportedParticle, "Unsupported particle: " + particle);
            this.centre = display.getLocation().clone();
        }
        
        public static boolean isSupportedParticle(@Nonnull Particle particle) {
            return SUPPORTED_PARTICLES.contains(particle);
        }
        
        @Nonnull
        public static List<Particle> supportedParticles() {
            return Lists.newArrayList(SUPPORTED_PARTICLES);
        }
        
        @Override
        public void run(int tick) {
            if (theta > Math.PI * 1.5) {
                cancel();
                return;
            }
            
            for (double d = -RADIUS; d <= RADIUS; d += 0.5) {
                final double p = d / RADIUS;
                final double x = Math.sin(Math.PI * p);
                
                // Don't draw particles too close to the centre because it looks ugly
                if (Math.abs(p) <= Math.PI * 0.1) {
                    continue;
                }
                
                drawParticle(x, d, true);
                drawParticle(x, -d, false);
                
                drawParticle(d, x, false);
                drawParticle(d, -x, true);
            }
            
            theta += Math.PI / 64;
        }
        
        private void drawParticle(double x, double z, boolean negate) {
            final Location location = display.getLocation();
            final Vector vector = new Vector(x, 0, z).rotateAroundY(negate ? -theta : theta);
            
            location.add(vector);
            final Vector towardsVector = centre.toVector().subtract(location.toVector()).normalize();
            
            display.particle(
                    location, particle, 0,
                    towardsVector.getX() * 0.5,
                    0,
                    towardsVector.getZ() * 0.5,
                    0.25f
            );
            
            location.subtract(vector);
        }
    }
    
    
}
