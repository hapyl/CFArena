package me.hapyl.fight.game.maps.features;

import me.hapyl.fight.CF;
import me.hapyl.fight.Main;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.util.BlockLocation;
import me.hapyl.fight.util.collection.player.PlayerMap;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class BoosterController implements Listener {

    private final PlayerMap<Entity> boosterMap = PlayerMap.newConcurrentMap();

    public BoosterController(Main main) {
        main.getServer().getPluginManager().registerEvents(this, main);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (boosterMap.isEmpty()) {
                    return;
                }

                boosterMap.forEach((player, entity) -> {
                    if (entity.isDead() || entity.isOnGround()) {
                        boosterMap.remove(player);
                        entity.getPassengers().forEach(Entity::eject);
                        entity.remove();
                    }
                });
            }
        }.runTaskTimer(Main.getPlugin(), 0, 2);
    }

    @EventHandler()
    public void handleBoosterLaunch(PlayerInteractEvent ev) {
        final GamePlayer player = CF.getPlayer(ev.getPlayer());

        if (player == null) {
            return;
        }

        final Action action = ev.getAction();
        final Block block = ev.getClickedBlock();

        if (action != Action.PHYSICAL
                || block == null
                || block.getType() != Material.HEAVY_WEIGHTED_PRESSURE_PLATE
                || boosterMap.containsKey(player)) {
            return;
        }

        final Booster booster = Booster.byLocation(new BlockLocation(block.getLocation()));
        if (booster == null || isOnBooster(player)) {
            return;
        }

        ev.setUseInteractedBlock(Event.Result.DENY);

        final Entity entity = booster.launchAndRide(player, false);
        boosterMap.put(player, entity);
    }

    public boolean isOnBooster(GamePlayer player) {
        return boosterMap.containsKey(player);
    }

}
