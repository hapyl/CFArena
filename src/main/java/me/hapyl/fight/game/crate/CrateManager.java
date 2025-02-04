package me.hapyl.fight.game.crate;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.hologram.StringArray;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.eterna.module.util.DependencyInjector;
import me.hapyl.fight.CF;
import me.hapyl.fight.Main;
import me.hapyl.fight.event.ProfileDeinitializationEvent;
import me.hapyl.fight.event.ProfileInitializationEvent;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.task.GeometryTask;
import me.hapyl.fight.game.task.ShutdownAction;
import me.hapyl.fight.gui.CrateGUI;
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

import javax.annotation.Nullable;
import java.util.List;

public class CrateManager extends DependencyInjector<Main> implements Listener {

    private static final double NEARBY_DISTANCE = 3.0d;
    private static final boolean ARE_CRATES_HAS_BEEN_DISCONTINUED = true;

    private final List<CrateLocation> crateLocations;

    public CrateManager(Main plugin) {
        super(plugin);

        crateLocations = Lists.newArrayList();

        if (ARE_CRATES_HAS_BEEN_DISCONTINUED) {
            return;
        }

        crateLocations.add(new CrateLocation(15, 63, -4).setYawPitch(90, 0));
        crateLocations.add(new CrateLocation(15, 63, 4).setYawPitch(90, 0));

        // Create Holograms
        crateLocations.forEach(location -> {
            Bukkit.getOnlinePlayers().forEach(location.hologram::create);
        });

        new GeometryTask() {
            @Override
            public void run(double theta) {
                final int tick = getTick();

                // Fx
                final double x = Math.sin(theta);
                final double y = Math.sin(Math.toRadians(tick)) * 0.25;
                final double z = Math.cos(theta);

                crateLocations.forEach(location -> {
                    location.add(0, 0.5, 0);

                    super.offsetLocation(location, x, y, z, loc -> {
                        PlayerLib.spawnParticle(loc, Particle.FLAME, 1, 0, 0, 0, 0.05f);
                    });

                    super.offsetLocation(location, z, -y, x, loc -> {
                        PlayerLib.spawnParticle(loc, Particle.CRIT, 1, 0, 0, 0, 0.05f);
                    });

                    location.subtract(0, 0.5, 0);
                });

                // Update holograms
                if (tick % 20 == 0) {
                    crateLocations.forEach(location -> {
                        location.hologram.setLines(player -> {
                            final long totalCrates = CF.getDatabase(player).crateEntry.getTotalCratesCount();

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
        crateLocations.forEach(location -> location.hologram.create(player));
    }

    public void removeHologram(Player player) {
        crateLocations.forEach(crate -> crate.hologram.destroy(player));
    }

    @EventHandler
    public void handleProfileDeinitializationEvent(ProfileDeinitializationEvent ev) {
        final Player player = ev.getPlayer();

        removeHologram(player);
    }

    @EventHandler
    public void handleProfileInitializationEvent(ProfileInitializationEvent ev) {
        final Player player = ev.getPlayer();

        createHologram(player);
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

        for (CrateLocation location : crateLocations) {
            if (clickedBlock.getLocation().distance(location) <= NEARBY_DISTANCE) {
                ev.setCancelled(true);
                openCrate(player, location);
                return;
            }
        }
    }

    public void openCrate(Player player, CrateLocation chest) {
        if (true) { // fixme
            return;
        }

        if (chest.checkOccupiedAndSendError(player)) {
            return;
        }

        new CrateGUI(player, chest);
    }

    @Nullable
    public CrateLocation getClosest(Location location) {
        for (CrateLocation crateLocation : crateLocations) {
            if (crateLocation.distance(location) <= NEARBY_DISTANCE) {
                return crateLocation;
            }
        }

        return null;
    }

}
