package me.hapyl.fight.game.maps.features;

import me.hapyl.fight.CF;
import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.maps.GameMaps;
import me.hapyl.fight.game.maps.MapFeature;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.Direction;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import net.md_5.bungee.api.ChatColor;
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
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class JapanFeature extends MapFeature implements Listener {

    private final Location[] healingSakuraLocations = {
            BukkitUtils.defLocation(269.5d, 65.0d, -10.0d),
            BukkitUtils.defLocation(331.5d, 65.0d, -10.0d)
    };

    private final Map<Player, Long> lastMovedAt = new HashMap<>();

    public JapanFeature() {
        super("Healing Sakura", "Stand inside &eSakura's &7&orange to feel its healing petals!");
    }

    @Override
    public void tick(int tick) {
        if (tick != 0) {
            return;
        }

        for (final Location location : healingSakuraLocations) {
            Collect.nearbyPlayers(location, 6.5d).forEach(gamePlayer -> {
                final Player player = gamePlayer.getPlayer();

                if (!canBeHealed(player)) {
                    return;
                }

                gamePlayer.heal(1.0d); // 0.5 -> 1.0

                // fx
                gamePlayer.sendSubtitle(ChatColor.of("#ffccff") + "You feel sakura's petals on your head", 0, 20, 5);
                gamePlayer.spawnParticle(gamePlayer.getEyeLocation().add(0.0d, 0.5d, 0.0d), Particle.HEART, 1, 0, 0, 0, 0);
            });
        }

    }

    private long canBeHealedIn(Player player) {
        final long lastMoved = lastMovedAt.getOrDefault(player, System.currentTimeMillis());
        return System.currentTimeMillis() - lastMoved;
    }

    private boolean canBeHealed(Player player) {
        final long canBeHealedIn = canBeHealedIn(player);
        return canBeHealedIn >= 5000L;
    }

    @Override
    public void onStop() {
        lastMovedAt.clear();
    }

    @Override
    public void onStart() {
        new GameTask() {
            private final Location location = BukkitUtils.defLocation(300.5d, 68.25d, -22.5d);
            private double theta = 0;

            @Override
            public void run() {
                final double x = 0.8d * Math.sin(theta);
                final double z = 0.8d * Math.cos(theta);

                location.add(x, 0, z);
                PlayerLib.spawnParticle(location, Particle.FIREWORKS_SPARK, 1, 0, 0, 0, 0);
                PlayerLib.spawnParticle(location, Particle.FLAME, 1, 0, 0, 0, 0);
                location.subtract(x, 0, z);

                theta += Math.PI / 8;
                if (theta > Math.PI * 2) {
                    theta = 0;
                }
            }
        }.runTaskTimer(0, 2);
    }

    @EventHandler()
    public void handlePlayerMove(PlayerMoveEvent ev) {
        final Player player = ev.getPlayer();
        final GamePlayer gamePlayer = CF.getPlayer(player);

        if (!validateGameAndMap(GameMaps.JAPAN) || gamePlayer == null) {
            return;
        }


        final Location from = ev.getFrom();
        final Location to = ev.getTo();

        if (to == null || (from.getX() == to.getX() && from.getY() == to.getY() && from.getZ() == to.getZ())) {
            return;
        }

        lastMovedAt.put(player, System.currentTimeMillis());
    }

    @EventHandler()
    public void handlePlayerInteractEvent(PlayerInteractEvent ev) {
        if (!validateGameAndMap(GameMaps.JAPAN)) {
            return;
        }

        final Player player = ev.getPlayer();
        final Action action = ev.getAction();
        final Block block = ev.getClickedBlock();

        if (action == Action.PHYSICAL
                && block != null
                && block.getType() == Material.LIGHT_WEIGHTED_PRESSURE_PLATE) {

            final Direction direction = Direction.getDirection(player.getLocation());
            final Vector vector = new Vector(0.0d, 2.0d, 0.0d);
            final double trueDoubleValue = 1.15d;

            switch (direction) {
                case NORTH -> vector.setZ(-trueDoubleValue);
                case SOUTH -> vector.setZ(trueDoubleValue);
                case WEST -> vector.setX(-trueDoubleValue);
                case EAST -> vector.setX(trueDoubleValue);
            }

            player.setVelocity(vector);
            GamePlayer.getPlayer(player).addEffect(GameEffectType.FALL_DAMAGE_RESISTANCE, 60);
            PlayerLib.playSound(player.getLocation(), Sound.ENTITY_WITHER_SHOOT, 0.75f);
        }

    }

}
