package me.hapyl.fight.game.talents.archive.tamer;

import me.hapyl.fight.fx.Riptide;
import me.hapyl.fight.game.Event;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.task.TickingGameTask;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.potion.PotionEffectType;

public class EntityLevitate<T extends LivingGameEntity> extends TickingGameTask {

    public final T entity;
    public final Location initialLocation;
    public final Riptide riptide;

    private final int duration;

    public EntityLevitate(T entity, int duration) {
        this.entity = entity;
        this.initialLocation = entity.getLocation().clone();
        this.riptide = new Riptide(entity.getLocation());
        this.duration = duration;

        entity.addPotionEffect(PotionEffectType.LEVITATION, duration, 6);

        onStart();
        runTaskTimer(0, 1);
    }

    @Override
    public final void onTaskStop() {
        if (!(entity instanceof GamePlayer)) {
            entity.removePotionEffect(PotionEffectType.LEVITATION);
        }
        riptide.remove();
    }

    @Event
    public void onStart() {
    }

    @Event
    public void onTick() {
    }

    @Override
    public void run(int tick) {
        if (tick > duration || entity.isDeadOrRespawning()) {
            cancel();
            return;
        }

        // Mojang haven't fixed this bug since like 2013, so have
        // to use different approach for player and entities
        if (entity instanceof GamePlayer player && tick == 10) {
            player.addPotionEffect(PotionEffectType.LEVITATION, duration - tick, 255);
        }
        else if (!(entity instanceof GamePlayer) && tick >= 10 && tick % 5 == 0) {
            entity.removePotionEffect(PotionEffectType.LEVITATION);
            entity.addPotionEffect(PotionEffectType.LEVITATION, 5, 0);
        }

        final Location location = entity.getLocation();

        // Fx
        entity.spawnWorldParticle(location, Particle.EXPLOSION_NORMAL, 1, 0.1d, 0.0d, 0.1d, 0.025f);
        entity.playWorldSound(initialLocation, Sound.ENTITY_EGG_THROW, 0.5f + (1.5f / duration * tick));

        onTick();
    }

}