package me.hapyl.fight.store;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.eterna.module.util.DependencyInjector;
import me.hapyl.fight.CF;
import me.hapyl.fight.Main;
import me.hapyl.fight.database.PlayerDatabase;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import javax.annotation.Nonnull;
import java.util.Map;

public class Store extends DependencyInjector<Main> implements Listener {

    private final Location[] storeLocations = {
            BukkitUtils.defLocation(27.5, 66.5, -3.5),
            BukkitUtils.defLocation(28.5, 66.5, -1.5),
            BukkitUtils.defLocation(28.5, 66.5, 2.5),
            BukkitUtils.defLocation(27.5, 66.5, 4.5),
    };

    private final Map<Player, PlayerStoreOffers> offers;

    public Store(@Nonnull Main plugin) {
        super(plugin);

        this.offers = Maps.newHashMap();

        CF.registerEvents(this);
    }

    @EventHandler
    public void handlePlayerJoinEvent(PlayerJoinEvent ev) {
        getOffers(ev.getPlayer());
    }

    @EventHandler
    public void handlePlayerQuitEvent(PlayerQuitEvent ev) {
        removeOffers(ev.getPlayer());
    }

    @Nonnull
    public PlayerStoreOffers getOffers(@Nonnull Player player) {
        return offers.computeIfAbsent(player, self -> {
            final PlayerDatabase database = CF.getDatabase(self);

            return new PlayerStoreOffers(this, player, database.storeEntry.getOffers());
        });
    }

    public void removeOffers(@Nonnull Player player) {
        final PlayerStoreOffers offers = this.offers.remove(player);

        if (offers != null) {
            offers.remove();
        }
    }

    @Nonnull
    public Location getStoreLocations(int i) {
        return BukkitUtils.newLocation(storeLocations[i]);
    }

    public void refreshOrders(@Nonnull Player player) {
        removeOffers(player);
        CF.getDatabase(player).storeEntry.generateOffers();
        getOffers(player);
    }

}
