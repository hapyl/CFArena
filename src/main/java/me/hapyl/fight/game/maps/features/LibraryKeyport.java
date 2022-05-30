package me.hapyl.fight.game.maps.features;

import me.hapyl.fight.util.BlockLocation;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.util.ThreadRandom;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class LibraryKeyport {

    private final Map<BlockLocation, BlockLocation> portals;

    public LibraryKeyport() {
        this.portals = new HashMap<>();
        this.addAll();
    }

    private void addAll() {

        // Entrances
        final BlockLocation entrance0 = new BlockLocation(-9, 75, -77);
        final BlockLocation entrance1 = new BlockLocation(-9, 75, -125);
        final BlockLocation entrance2 = new BlockLocation(-22, 75, -107);
        final BlockLocation entrance3 = new BlockLocation(9, 75, -126);
        final BlockLocation entrance4 = new BlockLocation(25, 75, -107);
        final BlockLocation entrance5 = new BlockLocation(25, 65, -107);
        final BlockLocation entrance6 = new BlockLocation(9, 65, -127);
        final BlockLocation entrance7 = new BlockLocation(-22, 65, -107);

        // Exits
        this.portals.put(entrance0, new BlockLocation(-9, 74, -80, -180, 0));
        this.portals.put(entrance1, new BlockLocation(-9, 74, -122));
        this.portals.put(entrance2, new BlockLocation(-18, 74, -107, -90, 0));
        this.portals.put(entrance3, new BlockLocation(9, 74, -122));
        this.portals.put(entrance4, new BlockLocation(21, 74, -107, 90, 0));
        this.portals.put(entrance5, new BlockLocation(21, 64, -107, 90, 0));
        this.portals.put(entrance6, new BlockLocation(9, 64, -123));
        this.portals.put(entrance7, new BlockLocation(-18, 64, -107, -90, 0));
    }

    public boolean testPlayer(Player player) {
        final Location location = player.getLocation();
        for (final BlockLocation entrance : this.portals.keySet()) {
            double distance = 3.0d;
            if (entrance.toLocation().distance(location) <= distance) {
                final Location exit = getRandomExitAndMergePitch(entrance, player);
                player.teleport(exit);
                PlayerLib.addEffect(player, PotionEffectType.BLINDNESS, 20, 1);
                PlayerLib.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.25f);
                return true;
            }
        }
        return false;
    }

    private BlockLocation getRandomButSelf(BlockLocation enter) {
        final BlockLocation element = portals.values().toArray(new BlockLocation[] {})[ThreadRandom.nextInt(portals.values().size())];
        return portals.get(enter) == element ? getRandomButSelf(enter) : element;
    }

    private Location getRandomExitAndMergePitch(BlockLocation enter, Player player) {
        final Location location = getRandomButSelf(enter).toLocation(true);
        location.setPitch(player.getLocation().getPitch());
        return location;
    }

    public Set<BlockLocation> getEntrances() {
        return this.portals.keySet();
    }
}
