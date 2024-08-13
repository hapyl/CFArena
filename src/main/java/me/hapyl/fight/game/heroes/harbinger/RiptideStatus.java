package me.hapyl.fight.game.heroes.harbinger;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.eterna.module.util.Vectors;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.PlayerData;
import me.hapyl.fight.game.task.GameTask;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import java.util.Map;

public class RiptideStatus extends PlayerData implements Ticking {

    //protected final static long RIPTIDE_DURATION = 100; // dynamic
    private final static double RIPTIDE_DAMAGE = 5.0d;
    protected final static long RIPTIDE_PERIOD = 50;
    protected final static int RIPTIDE_AMOUNT = 3;

    private final Map<LivingGameEntity, RiptideData> riptideData;

    public RiptideStatus(@Nonnull GamePlayer owner) {
        super(owner);
        riptideData = Maps.newConcurrentMap();
    }

    @Override
    public void remove() {
        riptideData.clear();
    }

    public void tick() {
        riptideData.forEach((entity, data) -> {
            if (data.isExpired()) {
                riptideData.remove(entity);
                return;
            }

            // Tick down
            data.tick();

            // Effect
            final Location location = entity.getEyeLocation().add(0.0d, 0.2d, 0.0d);

            // Make sure to spawn particle to only player
            player.spawnParticle(location, Particle.SPLASH, 1, 0.15d, 0.5d, 0.15d, 0.01f);
            player.spawnParticle(location, Particle.GLOW, 1, 0.15d, 0.15d, 0.5d, 0.025f);

            // This is just for the effect
            entity.addPotionEffect(PotionEffectType.SPEED, 0, 20);
            entity.addPotionEffect(PotionEffectType.SLOWNESS, 0, 20);
        });
    }

    public void setRiptide(@Nonnull LivingGameEntity entity, long duration, boolean force) {
        final RiptideData data = riptideData.computeIfAbsent(entity, RiptideData::new);

        if (duration > data.getAffectTick() || force) {
            data.setAffectTick(duration);
        }
    }

    private boolean isValidForRiptideSlash(@Nonnull LivingGameEntity entity) {
        return !entity.isDead() && isAffected(entity) && !onCooldown(entity);
    }

    public void executeRiptideSlash(@Nonnull LivingGameEntity entity) {
        if (!isValidForRiptideSlash(entity)) {
            return;
        }

        // Mark cooldown
        riptideData.computeIfAbsent(entity, RiptideData::new).markLastHit();

        GameTask.runTaskTimerTimes((task, tick) -> {
            entity.setLastDamager(player);
            entity.damage(RIPTIDE_DAMAGE, EnumDamageCause.RIPTIDE);
            entity.setVelocity(Vectors.random(0.25d, 0.1d));

            // Fx
            final Location location = entity.getEyeLocation();

            entity.spawnWorldParticle(location, Particle.SWEEP_ATTACK, 1, 0, 0, 0, 0);
            entity.playWorldSound(location, Sound.ITEM_BUCKET_FILL, 1.75f);
        }, 0, 2, RIPTIDE_AMOUNT);
    }

    public boolean onCooldown(@Nonnull LivingGameEntity entity) {
        final RiptideData data = riptideData.get(entity);
        return data != null && data.isOnCooldown();
    }

    public boolean isAffected(@Nonnull LivingGameEntity entity) {
        final RiptideData data = riptideData.get(entity);
        return data != null && !data.isExpired();
    }

    public void stop(@Nonnull LivingGameEntity killer) {
        riptideData.remove(killer);
    }
}
