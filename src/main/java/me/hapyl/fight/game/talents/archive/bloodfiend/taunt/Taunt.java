package me.hapyl.fight.game.talents.archive.bloodfiend.taunt;

import me.hapyl.fight.Main;
import me.hapyl.fight.fx.SwiftTeleportAnimation;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.archive.bloodfield.Bloodfiend;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.util.ThreadRandom;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public abstract class Taunt extends GameTask {

    public final GamePlayer target;
    protected final Player player;
    private final SwiftTeleportAnimation animation;
    protected Location initialLocation;
    private int tick;
    private boolean isAnimation;

    public Taunt(Player player, GamePlayer target) {
        this.player = player;
        this.target = target;
        this.initialLocation = pickRandomLocation(5);
        this.initialLocation.subtract(0.0d, 1.3d, 0.0d);

        final Location location = player.getLocation();
        animation = new SwiftTeleportAnimation(location, this.initialLocation) {
            @Override
            public void onAnimationStep(Location location) {
                Taunt.this.onAnimationStep(location);
            }

            @Override
            public void onAnimationStop() {
                isAnimation = false;
                Taunt.this.onAnimationEnd();
                target.sendWarning(getName() + " is taunting you!", 30);
                Chat.sendMessage(player, "%s&a is taunting &c%s&a!", getNameWithCharacter(), target.getName());
            }
        };

        isAnimation = true;
        animation.setSlope(2.0d).start(0, 1);

        // Fx
        PlayerLib.playSound(location, Sound.ENTITY_IRON_GOLEM_DEATH, 0.0f);
    }

    public boolean isAnimation() {
        return isAnimation;
    }

    public void onAnimationStep(Location location) {
    }

    public void onAnimationEnd() {
    }

    public void start(int duration) {
        tick = duration;
        runTaskTimer(0, 1);
    }

    public void remove() {
        animation.cancelIfActive();
        cancelIfActive();
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
            Chat.sendMessage(player, "%s %s &ewas removed because %s has died!", getCharacter(), getName(), target.getName());
            return;
        }

        run(tick--);

        // Fx
        if (tick % 10 == 0) {
            target.playPlayerSound(initialLocation, Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 0.0f);
        }

        // Warn about taunt!
        if (tick <= 100 && (tick % 2 == 0)) {
            target.sendWarning("&c%s is about to explode!".formatted(getName()), 20);
            target.playPlayerSound(Sound.BLOCK_NOTE_BLOCK_PLING, 2f - (1.5f / 100 * tick));
        }

        if (tick <= 0) {
            explode();
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

    public abstract double getDamage();

    @Nonnull
    public abstract EnumDamageCause getDamageCause();

    public void explode() {
        final double damage = getDamage();
        target.damage(damage, player, getDamageCause());

        // Fx
        target.sendMessage("%s &c%s's %s exploded on you! &7-%s ❤", getCharacter(), player.getName(), getName(), damage);
        target.playPlayerSound(Sound.ENTITY_PLAYER_DEATH, 1.0f);
        target.playPlayerSound(Sound.ENTITY_HUSK_DEATH, 0.0f);

        remove();
    }

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

    protected void asPlayers(@Nonnull Consumer<Player> consumer) {
        consumer.accept(player);
        consumer.accept(target.getPlayer());
    }

    // Spawns particle for both players
    protected void spawnParticle(Location location, Particle particle, int amount, double x, double y, double z, float speed) {
        asPlayers(player -> {
            PlayerLib.spawnParticle(player, location, particle, amount, x, y, z, speed);
        });
    }

    protected <T extends Entity> T spawnEntity(Entities<T> type, Location location, Consumer<T> consumer) {
        final Main plugin = Main.getPlugin();

        final T entity = type.spawn(location, self -> {
            self.setVisibleByDefault(false);
            consumer.accept(self);
        });

        player.showEntity(plugin, entity);
        target.showEntity(entity);

        return entity;
    }

    @Nonnull
    protected Location pickRandomLocation(int remainingTries) {
        final Location location = player.getLocation();

        if (remainingTries < 0) {
            return location;
        }

        final double x = ThreadRandom.nextDouble(-3.0d, 3.0d);
        final double z = ThreadRandom.nextDouble(-3.0d, 3.0d);

        location.add(x, 0, z);
        if (!location.getBlock().getType().isAir()) {
            return pickRandomLocation(--remainingTries);
        }

        // Center location to avoid animation artifacts
        location.setX(location.getBlockX() + 0.5d);
        location.setZ(location.getBlockZ() + 0.5d);

        return CFUtils.anchorLocation(location);
    }
}
