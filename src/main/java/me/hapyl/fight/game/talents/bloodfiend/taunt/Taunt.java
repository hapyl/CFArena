package me.hapyl.fight.game.talents.bloodfiend.taunt;

import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.locaiton.LocationHelper;
import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.fight.fx.SwiftTeleportAnimation;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.heroes.bloodfield.Bloodfiend;
import me.hapyl.fight.game.heroes.bloodfield.BloodfiendData;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.CFUtils;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;

public abstract class Taunt extends GameTask {

    protected final GamePlayer player;
    protected final Location location;
    protected final Bloodfiend bloodfiend;

    private final TauntTalent talent;
    private final SwiftTeleportAnimation animation;

    protected TauntParticle tauntParticle;

    private int tick;
    private boolean isAnimation;
    private double theta;

    public Taunt(TauntTalent talent, GamePlayer player, Location location) {
        this.talent = talent;
        this.player = player;
        this.location = location;
        this.bloodfiend = HeroRegistry.BLOODFIEND;

        animation = new SwiftTeleportAnimation(player.getLocationBehindFromEyes(1), this.location) {
            @Override
            public void onAnimationStep(Location location) {
                Taunt.this.onAnimationStep(location);
            }

            @Override
            public void onAnimationStop() {
                isAnimation = false;
                Taunt.this.onAnimationEnd();
            }
        };

        isAnimation = true;
        animation.setSlope(2.0d).start(0, 1);

        // Fx
        player.playWorldSound(location, Sound.ENTITY_IRON_GOLEM_DEATH, 0.0f);
    }

    @Nonnull
    public TauntTalent getTalent() {
        return talent;
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

        talent.startCd(player);
    }

    public int getTimeLeft() {
        return tick;
    }

    public abstract void tick(int tick);

    public abstract void tick(@Nonnull Collection<LivingGameEntity> entities);

    @Override
    public final void run() {
        if (player.isDeadOrRespawning()) {
            return;
        }

        // Tick normally
        tick(tick--);

        // Tick zone particle if exists
        if (tauntParticle != null) {
            for (int i = 0; i < tauntParticle.speed; i++) {
                tauntParticle.draw();
            }
        }

        // Tick SUCKED entities within range
        final int period = talent.getPeriod();
        if (period != -1 && tick % period == 0) {
            final Set<LivingGameEntity> suckedEntities = getSuckedEntitiesWithinRange();

            tick(suckedEntities);
        }

        // Fx
        if (tick % 10 == 0) {
            player.playSound(location, Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 0.0f);
        }

        if (tick <= 0) {
            remove();
        }
    }

    @Nonnull
    public Set<LivingGameEntity> getSuckedEntitiesWithinRange() {
        final Bloodfiend bloodfiend = getBloodfiend();
        final BloodfiendData data = bloodfiend.getData(player);
        final Set<LivingGameEntity> suckedEntities = data.getSuckedEntities();

        suckedEntities.removeIf(entity -> CFUtils.distance(entity.getLocation(), location) > talent.getRadius());
        return suckedEntities;
    }

    @Nonnull
    public String getName() {
        return talent.getName();
    }

    @Nonnull
    public abstract String getCharacter();

    @Nonnull
    public String getNameWithCharacter() {
        return getCharacter() + " " + getName();
    }

    @Override
    public void onTaskStop() {
        talent.removeTaunt(player);
    }

    @Nonnull
    public final World getWorld() {
        return CFUtils.getWorld(location);
    }

    @Nonnull
    public final Bloodfiend getBloodfiend() {
        return HeroRegistry.BLOODFIEND;
    }

    public boolean isSuckedEntityAndWithinRange(@Nonnull LivingGameEntity entity) {
        final BloodfiendData data = getBloodfiend().getData(player);

        return data.isSuckedEntity(entity) && CFUtils.distance(entity.getLocation(), location) <= talent.getRadius();
    }

    protected <T extends Entity> T spawnEntity(Entities<T> type, Location location, Consumer<T> consumer) {
        return type.spawn(location, consumer);
    }

    @Nonnull
    public static Location pickRandomLocation(Location location) {
        return BukkitUtils.findRandomLocationAround(location, 3.0d).subtract(0, 1.35d, 0);
    }

    protected abstract class TauntParticle {

        private final int speed;

        protected TauntParticle(int speed) {
            this.speed = speed;
        }

        public abstract void draw(@Nonnull Location location);

        public void draw() {
            final TauntTalent talent = getTalent();
            final double radius = talent.getRadius();

            final double x = Math.sin(theta) * radius;
            final double y = yOffset() + (Math.sin(Math.toRadians(tick * 20)) * slope());
            final double z = Math.cos(theta) * radius;

            CFUtils.offsetLocation(location, x, y, z, () -> draw(location));
            CFUtils.offsetLocation(location, z, y, x, () -> draw(location));

            theta += Math.PI / piIncrement();
        }

        protected double yOffset() {
            return 1;
        }

        protected double slope() {
            return 1.0d;
        }

        protected double piIncrement() {
            return 32;
        }
    }


}
