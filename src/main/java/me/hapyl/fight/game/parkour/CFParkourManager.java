package me.hapyl.fight.game.parkour;

import me.hapyl.eterna.Eterna;
import me.hapyl.eterna.builtin.manager.ParkourManager;
import me.hapyl.fight.Main;
import me.hapyl.fight.gui.ParkourLeaderboardGUI;
import me.hapyl.fight.util.CFUtils;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class CFParkourManager implements Listener {

    public CFParkourManager(Main main) {
        main.getServer().getPluginManager().registerEvents(this, main);
        final ParkourManager parkourManager = Eterna.getManagers().parkour;

        for (ParkourCourse value : ParkourCourse.values()) {
            parkourManager.register(value.getParkour());
        }
    }

    @EventHandler
    public void handlePlayerClick(PlayerInteractEvent ev) {
        final Player player = ev.getPlayer();
        final Block clickedBlock = ev.getClickedBlock();
        final Action action = ev.getAction();

        final ItemStack item = player.getInventory().getItemInMainHand();

        if (!item.getType().isAir() || clickedBlock == null || ev.getHand() == EquipmentSlot.OFF_HAND) {
            return;
        }

        // Only do block clicks
        if (action != Action.LEFT_CLICK_BLOCK && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        for (ParkourCourse parkour : ParkourCourse.values()) {
            final ParkourLeaderboard leaderboard = parkour.getParkour().getLeaderboard();

            if (leaderboard == null) {
                continue;
            }

            if (CFUtils.distance(leaderboard.getLocation(), clickedBlock.getLocation()) < 2.50d) {
                new ParkourLeaderboardGUI(player, parkour);
                return;
            }
        }

    }

    @EventHandler
    public void handlePlayerJoinEvent(PlayerJoinEvent ev) {
        // Update holograms
        for (ParkourCourse parkour : ParkourCourse.values()) {
            parkour.getParkour().reload();
        }
    }

}
