package me.hapyl.fight.game.maps.features.library;

import me.hapyl.eterna.module.util.CollectionUtils;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.util.BlockLocation;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class LibraryKeyport {

    private final Map<BlockLocation, BlockLocation> portals;

    public LibraryKeyport() {
        this.portals = new HashMap<>();
        this.addAll();
    }

    public boolean testPlayer(GamePlayer player) {
        final Location location = player.getLocation();

        for (BlockLocation entrance : portals.keySet()) {
            double distance = 3.0d;

            if (entrance.toLocation().distance(location) <= distance) {
                final Location exit = getRandomExitAndMergePitch(entrance, player);

                player.teleport(exit);
                player.addPotionEffect(PotionEffectType.BLINDNESS, 1, 20);
                player.playWorldSound(Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 0.75f);
                return true;
            }
        }
        return false;
    }

    @Nonnull
    public BlockLocation getRandomButSelf(BlockLocation enter) {
        final BlockLocation element = portals.values().toArray(new BlockLocation[] {})[ThreadLocalRandom.current().nextInt(portals.size())];
        return portals.get(enter) == element ? getRandomButSelf(enter) : element;
    }

    public BlockLocation getRandom() {
        return CollectionUtils.randomElement(portals.values().toArray(new BlockLocation[] {}));
    }

    @Nonnull
    public Set<BlockLocation> getEntrances() {
        return this.portals.keySet();
    }

    private void addAll() {
        // Entrances
        final BlockLocation entrance0 = new BlockLocation(3977, 65, -7);
        final BlockLocation entrance1 = new BlockLocation(4009, 65, -27);
        final BlockLocation entrance2 = new BlockLocation(4023, 65, -7);
        final BlockLocation entrance3 = new BlockLocation(3977, 75, -7);
        final BlockLocation entrance4 = new BlockLocation(3991, 75, -26);
        final BlockLocation entrance5 = new BlockLocation(4009, 75, -27);
        final BlockLocation entrance6 = new BlockLocation(4023, 75, -7);
        final BlockLocation entrance7 = new BlockLocation(3991, 75, 24);

        // Exits
        this.portals.put(entrance0, new BlockLocation(3982, 64, -7, -90f, 0f));
        this.portals.put(entrance1, new BlockLocation(4009, 64, -22));
        this.portals.put(entrance2, new BlockLocation(4019, 64, -7, 90f, 0f));
        this.portals.put(entrance3, new BlockLocation(3982, 74, -7, -90f, 0f));
        this.portals.put(entrance4, new BlockLocation(3991, 74, -21));
        this.portals.put(entrance5, new BlockLocation(4009, 74, -22));
        this.portals.put(entrance6, new BlockLocation(4018, 74, -7, 90f, 0f));
        this.portals.put(entrance7, new BlockLocation(3991, 74, 19, -180f, 0f));
    }

    private Location getRandomExitAndMergePitch(BlockLocation enter, GamePlayer player) {
        final Location location = getRandomButSelf(enter).toLocation(true);
        location.setPitch(player.getLocation().getPitch());
        return location;
    }
}
