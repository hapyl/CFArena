package me.hapyl.fight.game.maps.features;

import com.google.common.collect.Sets;
import me.hapyl.fight.annotate.DoNotMutate;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.Direction;
import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.eterna.module.util.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;

import javax.annotation.Nonnull;
import java.util.Random;
import java.util.Set;

public class Geyser {

    private final Location location;
    private final Location spawnLocation;
    private final Direction direction;

    private String texture = "2a643c5cca38d324bf81488db7e53f7785818cbfe20e9f732ae01fb2dac75ae6";
    private double range;
    private int minDelay;
    private int maxDelay;
    private int duration;
    private GameTask task;

    public Geyser(@Nonnull final Location location, @Nonnull final Direction direction) {
        this.location = location;
        this.direction = direction;
        this.range = 3.0d;
        this.minDelay = Tick.fromSecond(new Random().nextInt(6, 24));
        this.maxDelay = minDelay * 2 - 20;
        this.duration = Tick.fromSecond(2);

        // Calculate spawn location
        this.spawnLocation = BukkitUtils.newLocation(location);

        // offset location
        final int[] oppositeValues = direction.getOpposite().getValues();

        this.spawnLocation.subtract(0, 1.75d, 0);
        this.spawnLocation.add(oppositeValues[0] * 0.5d, oppositeValues[1] * 0.75d, oppositeValues[2] * 0.5d);

        // rotation up or down moves the head back and forth
        if (direction.isDown()) {
            this.spawnLocation.add(0, 0, 0.3d);
        }
        else if (direction.isUp()) {
            this.spawnLocation.add(0, 0.5d, -0.3d);
        }
    }

    public Geyser(int x, int y, int z, Direction direction) {
        this(new Location(Bukkit.getWorlds().get(0), x + 0.5d, y + 0.5d, z + 0.5d), direction);
    }

    public Geyser setTexture(String texture) {
        this.texture = texture;
        return this;
    }

    public Geyser setRange(double range) {
        Validate.isTrue(range > 0, "range cannot be negative");

        this.range = range;
        return this;
    }

    public Geyser setMinDelay(int minDelay) {
        Validate.isTrue(minDelay < maxDelay, "minDelay cannot be longer than maxDelay");

        this.minDelay = minDelay;
        return this;
    }

    public Geyser setMaxDelay(int maxDelay) {
        Validate.isTrue(maxDelay > minDelay, "maxDelay cannot be lower than minDelay");

        this.maxDelay = maxDelay;
        return this;
    }

    public Geyser setDuration(int duration) {
        Validate.isTrue(duration > 0, "duration cannot be negative");

        this.duration = duration;
        return this;
    }

    public boolean start() {
        if (task == null) {
            nextTask();
            return true;
        }

        return false;
    }

    public boolean stop() {
        if (task == null) {
            return false;
        }

        task.cancel();
        task = null;
        return true;
    }

    public void createEntities() {
        Entities.ARMOR_STAND_MARKER.spawn(spawnLocation, self -> {
            self.setHelmet(ItemBuilder.playerHeadUrl(texture).asIcon());
            self.setHeadPose(direction.toEulerAngle());
            self.setInvisible(true);
            self.setInvulnerable(true);
        });
    }

    public void affectEntityTick(@Nonnull LivingGameEntity entity, int tick) {
    }

    public void affectLocationTick(@Nonnull @DoNotMutate Location location, int tick) {
        PlayerLib.spawnParticle(location, Particle.POOF, 1, 0.1, 0.1, 0.1, 0.05f);
        PlayerLib.spawnParticle(location, Particle.SMOKE, 1, 0.1, 0.1, 0.1, 0.05f);

        if (tick % 30 == 0) {
            PlayerLib.playSound(location, Sound.BLOCK_REDSTONE_TORCH_BURNOUT, new Random().nextFloat(0.5f, 0.85f));
        }
    }

    private void affect(int tick) {
        final Set<LivingGameEntity> affectedEntities = Sets.newHashSet();

        for (double d = 0; d < range; d += 1) {
            direction.modifyLocation(location, d, loc -> {
                affectLocationTick(loc, tick);

                affectedEntities.addAll(Collect.nearbyEntities(loc, 1.0d));
            });
        }

        for (LivingGameEntity living : affectedEntities) {
            affectEntityTick(living, tick);
        }

        affectedEntities.clear();
    }

    private void nextTask() {
        if (task != null) {
            task.cancel();
        }

        int nextDelay = new Random().nextInt(minDelay, maxDelay);

        task = new TickingGameTask() {
            @Override
            public void run(int tick) {
                if (tick >= duration) {
                    nextTask();
                    return;
                }

                affect(tick);
            }

        }.runTaskTimer(nextDelay, 1);
    }
}
