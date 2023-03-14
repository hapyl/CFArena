package me.hapyl.fight.game.parkour;

import me.hapyl.fight.Main;
import me.hapyl.spigotutils.EternaPlugin;
import me.hapyl.spigotutils.module.parkour.Parkour;
import me.hapyl.spigotutils.module.parkour.ParkourManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class CFParkourManager implements Listener {

    public CFParkourManager(Main main) {
        main.getServer().getPluginManager().registerEvents(this, main);
        final ParkourManager parkourManager = EternaPlugin.getPlugin().getParkourManager();

        for (ParkourCourse value : ParkourCourse.values()) {
            parkourManager.registerParkour(value.getParkour());
        }
    }

    @EventHandler()
    public void handlePlayerJoinEvent(PlayerJoinEvent ev) {
        final Player player = ev.getPlayer();

        for (ParkourCourse value : ParkourCourse.values()) {
            value.getParkour().updateLeaderboardIfExists();
        }
    }

    public void saveAll() {
        for (Parkour parkour : EternaPlugin.getPlugin().getParkourManager().getRegisteredParkours()) {
            if (!(parkour instanceof CFParkour cfParkour)) {
                continue;
            }

            cfParkour.getDatabase().save();
        }
    }
}
