package me.hapyl.fight.game.parkour.storage;

import com.mongodb.client.model.Updates;
import me.hapyl.fight.game.parkour.CFParkour;
import me.hapyl.fight.game.parkour.ParkourLeaderboard;
import me.hapyl.fight.util.BoundingBoxCollector;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.BoundingBox;

public class SlimeParkour extends CFParkour {

    private final BoundingBox boundingBox;

    public SlimeParkour() {
        super("Slime Parkour",
                0, 63, 28, 0.0f, 0.0f,
                0, 65, 40
        );

        boundingBox = new BoundingBoxCollector(-4, 56, 25, 5, 61, 36);

        setLeaderboard(new ParkourLeaderboard(this, -2.5, 63.0, 22.5));
        setQuitLocation(BukkitUtils.defLocation(0.5, 62.0, 22.5));
    }

    public void addFails() {
        database.write(Updates.inc("fails", 1));
    }

    public int getFails() {
        return database.read("fails", 0);
    }

    @Override
    public void onDamage(Player player, EntityDamageEvent.DamageCause cause) {
        if (cause != EntityDamageEvent.DamageCause.FALL) {
            return;
        }

        final Location location = player.getLocation();

        if (!boundingBox.contains(location.getX(), location.getY(), location.getZ())) {
            return;
        }

        addFails();
    }
}
