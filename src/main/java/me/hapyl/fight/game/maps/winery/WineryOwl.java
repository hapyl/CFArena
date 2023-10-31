package me.hapyl.fight.game.maps.winery;

import com.google.common.collect.Maps;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.Ticking;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.Compute;
import me.hapyl.fight.util.Resettable;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;

import java.util.Map;
import java.util.UUID;

public class WineryOwl implements Resettable, Ticking {

    private static final int TICK_THRESHOLD = 30;

    private final Location location;
    private final Map<UUID, Integer> lookedAt;

    public WineryOwl(double x, double y, double z) {
        this(BukkitUtils.defLocation(x, y, z));
    }

    public WineryOwl(Location location) {
        this.location = location;
        this.lookedAt = Maps.newHashMap();
    }

    public Location getLocation() {
        return location;
    }

    public boolean hasLookedAt(GamePlayer player) {
        return lookedAt.getOrDefault(player.getUUID(), 0) >= TICK_THRESHOLD;
    }

    public boolean isLookingAt(GamePlayer player) {
        final double dot = CFUtils.dot(player.getLocation(), location);
        return dot >= 0.998d;
    }

    public void isLookingAtTheTick(GamePlayer player) {
        if (hasLookedAt(player) || !isLookingAt(player)) {
            return;
        }

        final UUID uuid = player.getUUID();
        final Integer newValue = lookedAt.compute(uuid, Compute.intAdd(5));

        // Fx
        player.playWorldSound(Sound.ENTITY_PLAYER_BREATH, 2.0f - (2.0f / TICK_THRESHOLD * newValue));

        // Complete Fx
        if (newValue >= TICK_THRESHOLD) {
            player.playWorldSound(Sound.ENTITY_ALLAY_HURT, 0.0f);
        }
    }

    @Override
    public void reset() {
        lookedAt.clear();
    }

    @Override
    public void tick() {
        PlayerLib.spawnParticle(location, Particle.CRIT, 5, 0.25, 0.25, 0.25, 0.0f);
    }
}