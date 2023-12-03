package me.hapyl.fight.game.cosmetic.crate;

import com.google.common.collect.Lists;
import me.hapyl.fight.Main;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.task.GeometryTask;
import me.hapyl.fight.game.task.ShutdownAction;
import me.hapyl.fight.globalconfig.Configurable;
import me.hapyl.fight.globalconfig.Configuration;
import me.hapyl.fight.gui.CrateGUI;
import me.hapyl.spigotutils.module.hologram.StringArray;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.util.DependencyInjector;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class CrateManager extends DependencyInjector<Main> implements Listener, Configurable {

    private static final double NEARBY_DISTANCE = 3.0d;
    private final List<CrateChest> crateChests;

    public CrateManager(Main plugin) {
        super(plugin);

        crateChests = Lists.newArrayList();
        crateChests.add(new CrateChest(15, 63, -4).setYawPitch(90, 0));
        crateChests.add(new CrateChest(15, 63, 4).setYawPitch(90, 0));

        // Create Holograms
        crateChests.forEach(location -> {
            Bukkit.getOnlinePlayers().forEach(location.hologram::create);
        });

        new GeometryTask() {
            @Override
            public void run(double theta) {
                final int tick = getTick();

                // Fx
                crateChests.forEach(location -> {
                    offsetXZ(location, 1.0d, 0.5d, loc -> {
                        PlayerLib.spawnParticle(loc, Particle.FLAME, 1, 0, 0, 0, 0.05f);
                        PlayerLib.spawnParticle(loc, Particle.CRIT, 1, 0, 0, 0, 0.05f);
                    });
                });

                // Update holograms
                if (tick % 20 == 0) {
                    crateChests.forEach(location -> {
                        location.hologram.setLines(player -> {
                            final long totalCrates = PlayerDatabase.getDatabase(player).crateEntry.getTotalCrates();

                            return StringArray.of(Color.BUTTON.bold() + "CRATES")
                                    .appendIf(
                                            totalCrates > 0,
                                            Color.SUCCESS.color("&a%,d unopened crates!".formatted(totalCrates)),
                                            "&7No crates!"
                                    );
                        });
                    });
                }
            }
        }.properties().infinite()
                .task().runTaskTimer(20, 1).setShutdownAction(ShutdownAction.IGNORE);

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void createHologram(Player player) {
        crateChests.forEach(location -> location.hologram.create(player));
    }

    public void removeHologram(Player player) {
        crateChests.forEach(crate -> crate.hologram.destroy(player));
    }

    @EventHandler()
    public void handlePlayerInteract(PlayerInteractEvent ev) {
        if (ev.getHand() == EquipmentSlot.OFF_HAND) {
            return;
        }

        final Action action = ev.getAction();

        if (action != Action.RIGHT_CLICK_BLOCK && action != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        final Player player = ev.getPlayer();
        final Block clickedBlock = ev.getClickedBlock();

        if (clickedBlock == null || clickedBlock.getType() != Material.TRAPPED_CHEST) {
            return;
        }

        for (CrateChest location : crateChests) {
            if (clickedBlock.getLocation().distance(location) <= NEARBY_DISTANCE) {
                ev.setCancelled(true);
                openCrate(player, location);
                return;
            }
        }
    }

    public void openCrate(Player player, CrateChest chest) {
        if (checkDisabledAndSendError(player)) {
            return;
        }

        if (chest.checkOccupiedAndSendError(player)) {
            return;
        }

        new CrateGUI(player, chest);
    }

    @Nullable
    public CrateChest getClosest(Location location) {
        for (CrateChest crateChest : crateChests) {
            if (crateChest.distance(location) <= NEARBY_DISTANCE) {
                return crateChest;
            }
        }

        return null;
    }

    @Nonnull
    @Override
    public Configuration getConfiguration() {
        return Configuration.CRATE;
    }
}
