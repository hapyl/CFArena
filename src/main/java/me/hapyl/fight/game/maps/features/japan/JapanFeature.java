package me.hapyl.fight.game.maps.features.japan;

import com.google.common.collect.Sets;
import me.hapyl.fight.CF;
import me.hapyl.fight.game.Debug;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.MoveType;
import me.hapyl.fight.game.entity.cooldown.Cooldown;
import me.hapyl.fight.game.maps.GameMaps;
import me.hapyl.fight.game.maps.MapFeature;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.Collect;
import me.hapyl.spigotutils.module.chat.Gradient;
import me.hapyl.spigotutils.module.chat.gradient.Interpolators;
import me.hapyl.spigotutils.module.locaiton.LocationHelper;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.Set;

public class JapanFeature extends MapFeature implements Listener {

    private final Location[] healingSakuraLocations = {
            BukkitUtils.defLocation(972, 66, -12),
            BukkitUtils.defLocation(1028, 66, -12)
    };

    private final String healingMessage = new Gradient("You feel sakura's petals on your head").rgb(
            new java.awt.Color(217, 100, 213),
            new java.awt.Color(191, 40, 186),
            Interpolators.LINEAR
    );

    private final Set<JapanBooster> boosters = Sets.newHashSet();

    private final double healingPerPeriod = 1.0d;
    private final int healingPeriod = 20;

    public JapanFeature() {
        super("Healing Sakura", """
                Stand inside &eSakura's &7&orange to feel its healing petals!
                """);

        // Add boosters
        boosters.add(new JapanBooster(
                1000, 68.0, -26,
                1.9d, 1.0d,
                20
        ));

        boosters.add(new JapanLightBooster(954, 80, -26));
        boosters.add(new JapanLightBooster(1046, 80, -26));
    }

    @Override
    public void tick(int tick) {
        if (tick % healingPeriod == 0) {
            for (final Location location : healingSakuraLocations) {
                Collect.nearbyPlayers(location, 8.0d).forEach(gamePlayer -> {
                    if (!canBeHealed(gamePlayer)) {
                        return;
                    }

                    gamePlayer.heal(healingPerPeriod);
                    gamePlayer.sendSubtitle(healingMessage, 0, 30, 5);
                });
            }
        }

    }

    @Override
    public void onStart() {
        new GameTask() {
            private int tick = 0;
            private double theta = 0;

            @Override
            public void run() {
                final double x = 0.8d * Math.sin(theta);
                final double y = Math.sin(Math.toRadians(tick++ * 2)) * 0.2d;
                final double z = 0.8d * Math.cos(theta);

                boosters.forEach(booster -> {
                    final Location location = booster.getLocation();

                    location.add(x, y, z);
                    booster.tick();
                    location.subtract(x, y, z);
                });

                theta += Math.PI / 8;
            }
        }.runTaskTimer(0, 2);
    }

    @EventHandler()
    public void handlePlayerInteractEvent(PlayerInteractEvent ev) {
        final Player player = ev.getPlayer();
        final Action action = ev.getAction();
        final Block block = ev.getClickedBlock();

        if (action != Action.PHYSICAL || !validateCurrentMap(GameMaps.JAPAN)) {
            return;
        }

        final GamePlayer gamePlayer = CF.getPlayer(player);

        if (gamePlayer == null || block == null) {
            return;
        }

        if (gamePlayer.hasCooldown(Cooldown.JAPAN_BOOSTER)) {
            return;
        }

        final Material type = block.getType();

        if (type != Material.HEAVY_WEIGHTED_PRESSURE_PLATE && type != Material.LIGHT_WEIGHTED_PRESSURE_PLATE) {
            return;
        }

        for (JapanBooster booster : boosters) {
            if (LocationHelper.blockLocationEquals(booster.getLocation(), block.getLocation())) {
                gamePlayer.startCooldown(Cooldown.JAPAN_BOOSTER);
                booster.boost(gamePlayer);
                return;
            }
        }
    }

    private boolean canBeHealed(GamePlayer player) {
        return !player.hasMovedInLast(MoveType.KEYBOARD, 5000);
    }

}
