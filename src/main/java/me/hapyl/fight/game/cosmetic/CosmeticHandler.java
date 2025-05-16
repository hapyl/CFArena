package me.hapyl.fight.game.cosmetic;

import com.google.common.collect.Maps;
import me.hapyl.fight.CF;
import me.hapyl.fight.game.IGameInstance;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.State;
import me.hapyl.fight.game.cosmetic.contrail.ContrailCosmetic;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;

public class CosmeticHandler extends BukkitRunnable implements Listener {

    private final Map<UUID, Long> lastMovedAt;
    private final long moveThreshold = 250L;

    private int tick;

    public CosmeticHandler() {
        this.lastMovedAt = Maps.newHashMap();

        // Start runnable
        runTaskTimer(CF.getPlugin(), 0, 1);
    }

    @Override
    public void run() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            // Check if player has a contrail
            final Cosmetic selected = CF.getDatabase(player).cosmeticEntry.getSelected(Type.CONTRAIL);

            if (!(selected instanceof ContrailCosmetic contrail) || player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                return;
            }

            // Don't display if game is not started but exists
            final IGameInstance gameInstance = Manager.current().currentInstanceOrNull();

            if (gameInstance != null && gameInstance.getGameState() != State.IN_GAME) {
                return;
            }

            // Check the last time player has moved
            final UUID uuid = player.getUniqueId();
            final long currentTimeMillis = System.currentTimeMillis();

            final long lastMovedAt = this.lastMovedAt.getOrDefault(uuid, 0L);
            final long timeSinceLastMoved = currentTimeMillis - lastMovedAt;

            final Display display = new Display(player, player.getLocation());

            // Contrails are special
            if (timeSinceLastMoved >= moveThreshold) {
                contrail.onStandingStill(display, tick);
            }
            else {
                contrail.onMove(display, tick);
            }

        });

        ++tick;
    }

    @EventHandler()
    public void handlePlayerMoveEvent(PlayerMoveEvent ev) {
        final Player player = ev.getPlayer();
        final Location from = ev.getFrom();
        final Location to = ev.getTo();

        // Check if player moves a full block
        if (from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ()) {
            return;
        }

        // Mark last moved
        lastMovedAt.put(player.getUniqueId(), System.currentTimeMillis());
    }
}
