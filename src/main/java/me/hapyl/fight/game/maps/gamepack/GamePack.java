package me.hapyl.fight.game.maps.gamepack;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import me.hapyl.fight.game.GameElement;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

/**
 * Stores the location.
 */
public abstract class GamePack implements GameElement, Listener {

    private final List<Location> locations;
    private final Set<ActivePack> activePacks;

    private Particle particle;
    private int spawnPeriod;
    private ItemStack texture;

    public GamePack(int period, @Nonnull String texture) {
        this.locations = Lists.newArrayList();
        this.activePacks = Sets.newHashSet();
        this.spawnPeriod = period;

        setTexture(texture);
    }

    public Particle getParticle() {
        return particle;
    }

    public void setParticle(Particle particle) {
        this.particle = particle;
    }

    @Nonnull
    public ItemStack getTexture() {
        return texture;
    }

    public void setTexture(String texture64) {
        this.texture = ItemBuilder.playerHeadUrl(texture64).build();
    }

    @Nullable
    public ActivePack getCollisionPack(Player player) {
        final Location location = player.getLocation();

        for (ActivePack pack : activePacks) {
            final ArmorStand entity = pack.getEntity();
            if (entity == null) { // not active/respawning
                continue;
            }

            if (location.distance(entity.getLocation()) < 1.0d) {
                return pack;
            }
        }

        return null;
    }

    @Override
    public void onPlayersReveal() {
        // Activate packs
        activePacks.forEach(activePack -> activePack.next(true));
    }

    @Override
    public void onStart() {
        // Spawn health packs
        for (Location location : locations) {
            activePacks.add(new ActivePack(this, location));
        }
    }

    @Override
    public void onStop() {
        activePacks.clear();
    }

    public Set<ActivePack> getActivePacks() {
        return activePacks;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public int getSpawnPeriod() {
        return spawnPeriod;
    }

    public void setSpawnPeriod(int spawnPeriod) {
        this.spawnPeriod = spawnPeriod;
    }

    public void addLocation(Location location) {
        this.locations.add(location);
    }

    public abstract void onPickup(Player player);

    public abstract void displayParticle(Location location);

    public void accelerate() {
        activePacks.forEach(pack -> pack.nextSpawn = ActivePack.SPAWN_THRESHOLD + 1);
    }

    protected void sendMessage(Player player, String message) {
    }

}
