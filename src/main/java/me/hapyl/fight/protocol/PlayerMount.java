package me.hapyl.fight.protocol;

import com.google.common.collect.Maps;
import me.hapyl.spigotutils.module.entity.Entities;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.Map;

public class PlayerMount {

    // I'm to lazy so just static
    protected static final Map<Player, PlayerMount> MOUNTS = Maps.newHashMap();

    public final Player player;
    public final Location location;
    private ArmorStand stand;

    private PlayerMount(Player player, Location location) {
        this.player = player;
        this.location = location;
    }

    private void mount() {
        MOUNTS.put(player, this);

        stand = Entities.ARMOR_STAND.spawn(location, self -> {
            self.setMarker(true);
            self.setSmall(true);
            self.setSilent(true);
            self.setInvisible(true);
            self.addPassenger(player);
        });
    }

    private void dismount() {
        player.eject();
        stand.remove();
        MOUNTS.remove(player);
    }

    @Nullable
    public static PlayerMount getMount(Player player) {
        return MOUNTS.get(player);
    }

    /**
     * Mounts player to a PlayerMount, preventing them from dismounting.
     *
     * @param player   - Player to mount.
     * @param location - Location.
     */
    public static PlayerMount mount(Player player, Location location) {
        final PlayerMount playerMount = new PlayerMount(player, location);
        playerMount.mount();

        return playerMount;
    }

    /**
     * Dismounts player from the current mount if present, does nothing otherwise.
     */
    public static void dismount(Player player) {
        final PlayerMount playerMount = MOUNTS.get(player);

        if (playerMount != null) {
            playerMount.dismount();
        }
    }
}
