package me.hapyl.fight.mini.lampgame;

import com.google.common.collect.Maps;
import me.hapyl.fight.Main;
import me.hapyl.spigotutils.module.math.Cuboid;
import me.hapyl.spigotutils.module.util.DependencyInjector;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;

import javax.annotation.Nonnull;
import java.util.Map;

public class LampGame extends DependencyInjector<Main> implements Listener {

    protected static final Cuboid BOUNDING_BOX = new Cuboid(-3, 63, -19, 3, 67, -19);
    private final Map<Player, Data> playerData;

    public LampGame(Main plugin) {
        super(plugin);

        Bukkit.getPluginManager().registerEvents(this, plugin);
        playerData = Maps.newHashMap();
    }

    @Nonnull
    public Data getData(Player player) {
        return playerData.computeIfAbsent(player, Data::new);
    }

    @EventHandler()
    public void handlePlayerJoin(PlayerJoinEvent ev) {
        playerData.computeIfAbsent(ev.getPlayer(), Data::new);
    }

    @EventHandler()
    public void handlePlayerLeave(PlayerQuitEvent ev) {
        final Data data = playerData.remove(ev.getPlayer());

        if (data == null) {
            return;
        }

        data.remove();
    }

    @EventHandler()
    public void handleBlockBreak(BlockBreakEvent ev) {
        final Player player = ev.getPlayer();
        final Block block = ev.getBlock();

        if (block.getType() != Material.REDSTONE_LAMP || !BOUNDING_BOX.isIn(block.getLocation())) {
            return;
        }

        getData(player).reset();
        ev.setCancelled(true);
    }

    @EventHandler()
    public void handlePlayerInteract(PlayerInteractEvent ev) {
        final Player player = ev.getPlayer();
        final Block clickedBlock = ev.getClickedBlock();

        final Action action = ev.getAction();

        if ((action != Action.RIGHT_CLICK_BLOCK && action != Action.LEFT_CLICK_BLOCK)
                || ev.getHand() == EquipmentSlot.OFF_HAND
                || clickedBlock == null
                || clickedBlock.getType() != Material.REDSTONE_LAMP) {
            return;
        }

        // Make sure we're still in the bounding box
        if (!BOUNDING_BOX.isIn(clickedBlock.getLocation())) {
            return;
        }

        getData(player).handleClick(clickedBlock);
    }

}
