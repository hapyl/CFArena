package me.hapyl.fight.game.heroes.archive.harbinger;

import com.google.common.collect.Maps;
import me.hapyl.fight.CF;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.reflect.Ticking;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.Random;

public class RiptideStatus implements Ticking {

    //protected final static long RIPTIDE_DURATION = 100; // dynamic
    private final static double RIPTIDE_DAMAGE = 3.0d;
    protected final static long RIPTIDE_PERIOD = 50;
    protected final static int RIPTIDE_AMOUNT = 5;

    private final Player player;
    private final Map<LivingEntity, RiptideData> riptideData;

    public RiptideStatus(Player owner) {
        this.player = owner;
        this.riptideData = Maps.newConcurrentMap();
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
            PlayerLib.spawnParticle(player, location, Particle.WATER_SPLASH, 1, 0.15d, 0.5d, 0.15d, 0.01f);
            PlayerLib.spawnParticle(player, location, Particle.GLOW, 1, 0.15d, 0.15d, 0.5d, 0.025f);

            entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20, 0));
            entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 0));
        });
    }

    public void setRiptide(LivingEntity entity, long duration, boolean force) {
        final RiptideData data = riptideData.computeIfAbsent(entity, RiptideData::new);

        if (duration > data.getAffectTick() || force) {
            data.setAffectTick(duration);
        }
    }

    private boolean isValidForRiptideSlash(LivingEntity entity) {
        boolean dead = entity.isDead();

        if (entity instanceof Player player) {
            dead = GamePlayer.getPlayer(player).isDead();
        }

        return !dead && isAffected(entity) && !onCooldown(entity);
    }

    public void executeRiptideSlash(LivingEntity entity) {
        if (!isValidForRiptideSlash(entity)) {
            return;
        }

        // Mark cooldown
        riptideData.computeIfAbsent(entity, RiptideData::new).markLastHit();

        final int maxTicks = entity.getMaximumNoDamageTicks();
        entity.setMaximumNoDamageTicks(0);

        GameTask.runTaskTimerTimes((task, tick) -> {
            CF.getEntityOptional(entity).ifPresent(gameEntity -> {
                gameEntity.damage(RIPTIDE_DAMAGE, player, EnumDamageCause.RIPTIDE);
                gameEntity.setVelocity(new Vector(new Random().nextDouble() * 0.5d, 0.25d, new Random().nextDouble() * 0.5d));

                if (tick == 0) {
                    entity.setMaximumNoDamageTicks(maxTicks);
                }

                final Location location = entity.getEyeLocation();
                PlayerLib.spawnParticle(location, Particle.SWEEP_ATTACK, 1, 0, 0, 0, 0);
                PlayerLib.playSound(location, Sound.ITEM_BUCKET_FILL, 1.75f);
            });
        }, 0, 2, RIPTIDE_AMOUNT);
    }

    public boolean onCooldown(LivingEntity entity) {
        final RiptideData data = riptideData.get(entity);
        return data != null && data.isOnCooldown();
    }

    public boolean isAffected(LivingEntity entity) {
        final RiptideData data = riptideData.get(entity);
        return data != null && !data.isExpired();
    }

    public void stop(LivingEntity killer) {
        final RiptideData data = riptideData.remove(killer);
    }
}
