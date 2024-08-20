package me.hapyl.fight.game.heroes.nyx;

import com.google.common.collect.Sets;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.PlayerData;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.Mth;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Item;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class NyxData extends PlayerData {

    public static final int MAX_CHAOS_STACKS = 6;
    public static final int INITIAL_CHAOS_STACKS = 1;

    private final Set<ChaosDroplet> droplets;

    private int chaosStacks;

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
        droplets.add(new ChaosDroplet(player, CFUtils.anchorLocation(location)));
    }

    @Nullable
    public ChaosDroplet getDroplet(@Nonnull Item item) {
        for (ChaosDroplet droplet : droplets) {
            if (droplet.isEquals(item)) {
                return droplet;
            }
        }

        return null;
    }

    public void removeDroplet(@Nonnull ChaosDroplet droplet) {
        droplets.remove(droplet);
        droplet.remove();
    }

    public int dropletCount() {
        return droplets.size();
    }
}
