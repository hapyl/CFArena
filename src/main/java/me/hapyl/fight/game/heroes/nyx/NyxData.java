package me.hapyl.fight.game.heroes.nyx;

import com.google.common.collect.Sets;
import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.eterna.module.util.CollectionUtils;
import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.fight.CF;
import me.hapyl.fight.game.Debug;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.PlayerData;
import me.hapyl.fight.util.Mth;
import org.bukkit.Location;
import org.bukkit.Particle;

import javax.annotation.Nonnull;
import java.util.Set;

public class NyxData extends PlayerData implements Ticking {

    public static final int MAX_CHAOS_STACKS = 6;
    public static final int INITIAL_CHAOS_STACKS = 1;

    private static final double DROPLET_COLLISION_THRESHOLD = 0.5d;

    protected final Set<ChaosDroplet> droplets;
    protected int chaosStacks;

    public NyxData(GamePlayer player) {
        super(player);

        this.droplets = Sets.newHashSet();
        this.chaosStacks = INITIAL_CHAOS_STACKS;
    }

    public int getChaosStacks() {
        return chaosStacks;
    }

    public void incrementChaosStacks(int chaosRegen) {
        chaosStacks = Math.min(this.chaosStacks + chaosRegen, MAX_CHAOS_STACKS);
    }

    public void decrementChaosStacks() {
        chaosStacks = Mth.decrementMax(chaosStacks, 0);
    }

    @Override
    public void remove() {
        droplets.forEach(droplet -> {
            player.spawnWorldParticle(droplet.getLocation().add(0, 0.5, 0), Particle.ASH, 10, 0.15f, 0.15f, 0.15f, 0);
            droplet.remove();
        });

        droplets.clear();
    }

    public void createDroplet(@Nonnull Location location) {
        droplets.add(new ChaosDroplet(player, BukkitUtils.anchorLocation(location)));
    }

    public int dropletCount() {
        return droplets.size();
    }

    @Override
    public void tick() {
        // Tick droplets
        CollectionUtils.iterate(droplets, (iterator, droplet) -> {
            final Location location = droplet.getLocation().add(0.0d, 0.25d, 0.0d);

            // Animate
            final int tick = droplet.entity.getTicksLived();
            final double y = Math.sin(Math.toRadians(tick * 10)) * 0.01d;

            final Location dropletLocation = droplet.entity.getLocation();
            dropletLocation.add(0, y, 0);
            dropletLocation.setYaw(dropletLocation.getYaw() + 15);

            droplet.entity.teleport(dropletLocation);

            CF.getAlivePlayers().forEach(player -> {
                final boolean isTeammate = this.player.isSelfOrTeammate(player);

                // Test collision and effect
                final double distanceSquared = player.getLocation().distanceSquared(location);

                if (distanceSquared <= DROPLET_COLLISION_THRESHOLD) {
                    // Check if the droplet can affect the player
                    if (droplet.affect(player, this)) {
                        iterator.remove();
                        return;
                    }
                }

                dropletLocation.add(0, 1, 0);

                // Particles effect
                if (isTeammate) {
                    player.spawnWorldParticle(dropletLocation, Particle.WITCH, 5, 0.25f, 0.25f, 0.25, 0.05f);
                }
                else {
                    player.spawnWorldParticle(dropletLocation, Particle.ASH, 5, 0.25f, 0.25f, 0.25f, 0.025f);
                }
            });
        });
    }

}
