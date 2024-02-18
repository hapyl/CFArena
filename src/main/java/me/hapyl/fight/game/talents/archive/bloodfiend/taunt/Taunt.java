package me.hapyl.fight.game.talents.archive.bloodfiend.taunt;

import me.hapyl.fight.Main;
import me.hapyl.fight.fx.SwiftTeleportAnimation;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.archive.bloodfield.Bloodfiend;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.spigotutils.module.entity.Entities;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public abstract class Taunt extends GameTask {

    public final GamePlayer target;

    protected final GamePlayer player;
    private final SwiftTeleportAnimation animation;

    protected Location initialLocation;

    private int tick;
    private boolean isAnimation;

    public Taunt(GamePlayer player, GamePlayer target, Location location) {
        this.player = player;
        this.target = target;
        this.initialLocation = location;

        animation = new SwiftTeleportAnimation(player.getLocationBehindFromEyes(1), this.initialLocation) {
            @Override
            public void onAnimationStep(Location location) {
                Taunt.this.onAnimationStep(location);
            }

            @Override
            public void onAnimationStop() {
                isAnimation = false;
                Taunt.this.onAnimationEnd();

                target.sendWarning(getName() + " is taunting you!", 30);
                player.sendMessage("%s&a is taunting &c%s&a!", getNameWithCharacter(), target.getName());
            }
        };

        isAnimation = true;
        animation.setSlope(2.0d).start(0, 1);

        // Fx
        player.playWorldSound(location, Sound.ENTITY_IRON_GOLEM_DEATH, 0.0f);
    }

    public boolean isAnimation() {
        return isAnimation;
    }

    public void onAnimationStep(@Nonnull Location location) {
    }

    public void onAnimationEnd() {
    }

    public void start(int duration) {
        tick = duration;
        runTaskTimer(0, 1);
    }

    public void remove() {
        animation.cancel();
        cancel();
    }

    public int getTimeLeft() {
        return tick;
    }

    public abstract void run(int tick);

    @Override
    public final void run() {
        // Remove taunt if taunt has died
        if (target.isDeadOrRespawning()) {
            remove();
            player.sendMessage("%s %s &ewas removed because %s has died!", getCharacter(), getName(), target.getName());
            return;
        }

        run(tick--);

        // Fx
        if (tick % 10 == 0) {
            target.playSound(initialLocation, Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 0.0f);
        }

        if (tick <= 0) {
            remove();
        }
    }

    @Nonnull
    public abstract String getName();

    @Nonnull
    public abstract String getCharacter();

    @Nonnull
    public String getNameWithCharacter() {
        return getCharacter() + " " + getName();
    }

    @Nonnull
    public abstract EnumDamageCause getDamageCause();

    @Nonnull
    public final World getWorld() {
        final World world = initialLocation.getWorld();

        if (world == null) {
            throw new IllegalArgumentException("unloaded world");
        }

        return world;
    }

    @Nonnull
    public final Bloodfiend getBloodfiend() {
        return Heroes.BLOODFIEND.getHero(Bloodfiend.class);
    }

    protected void asPlayers(@Nonnull Consumer<GamePlayer> consumer) {
        consumer.accept(player);
        consumer.accept(target);
    }

    // Spawns particle for both players
    protected void spawnParticle(Location location, Particle particle, int amount, double x, double y, double z, float speed) {
        asPlayers(player -> {
            player.spawnParticle(location, particle, amount, x, y, z, speed);
        });
    }

    protected <T extends Entity> T spawnEntity(Entities<T> type, Location location, Consumer<T> consumer) {
        final Main plugin = Main.getPlugin();

        final T entity = type.spawn(location, self -> {
            self.setVisibleByDefault(false);
            consumer.accept(self);
        });

        player.showEntity(entity);
        target.showEntity(entity);

        return entity;
    }

    @Nonnull
    public static Location pickRandomLocation(Location location) {
        return CFUtils.findRandomLocationAround(location).subtract(0, 1.35d, 0);
    }


}
