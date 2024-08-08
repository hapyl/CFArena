package me.hapyl.fight.protocol;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.eterna.module.entity.Entities;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;

import javax.annotation.Nullable;

public class PlayerMount {

    // I'm to lazy so just static
    protected static final PlayerMap<PlayerMount> MOUNTS = PlayerMap.newMap();

    public final GamePlayer player;
    public final Location location;
    private ArmorStand stand;

    private PlayerMount(GamePlayer player, Location location) {
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
            self.addPassenger(player.getPlayer());
        });
    }

    private void dismount() {
        player.eject();
        stand.remove();
        MOUNTS.remove(player);
    }

    @Nullable
    public static PlayerMount getMount(GamePlayer player) {
        return MOUNTS.get(player);
    }

    /**
     * Mounts player to a PlayerMount, preventing them from dismounting.
     *
     * @param player   - Player to mount.
     * @param location - Location.
     */
    public static PlayerMount mount(GamePlayer player, Location location) {
        final PlayerMount playerMount = new PlayerMount(player, location);
        playerMount.mount();

        return playerMount;
    }

    /**
     * Dismounts player from the current mount if present, does nothing otherwise.
     */
    public static void dismount(GamePlayer player) {
        final PlayerMount playerMount = MOUNTS.get(player);

        if (playerMount != null) {
            playerMount.dismount();
        }
    }
}
