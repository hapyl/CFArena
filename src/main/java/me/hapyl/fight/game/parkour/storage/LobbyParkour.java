package me.hapyl.fight.game.parkour.storage;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.fight.game.parkour.CFParkour;
import me.hapyl.fight.game.parkour.ParkourLeaderboard;
import org.bukkit.Location;

public class LobbyParkour extends CFParkour {
    public LobbyParkour() {
        super(
                Key.ofString("lobby_parkour"),
                "Lobby Parkour",
                4, 63, -3, 180f, 0.0f,
                22, 65, 10
        );

        addCheckpoint(7, 67, 2, 36.4f, 0.0f);
        addCheckpoint(8, 66, -23, 135.0f, 0.0f);
        addCheckpoint(-14, 67, -21, 55.0f, 23.0f);
        addCheckpoint(-27, 65, 8, -31.0f, 0.0f);
        addCheckpoint(-11, 73, 11, -135.0f, 0.0f);
        addCheckpoint(18, 68, 18, -153.0f, 27.0f);

        setLeaderboard(new ParkourLeaderboard(this, 25.5, 66.0, 11.5));

        final Location quitLocation = BukkitUtils.defLocation(4.5d, 62.0d, 0.5d);
        quitLocation.setYaw(-180.0f);

        setQuitLocation(quitLocation);
    }

}
