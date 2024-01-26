package me.hapyl.fight.game.maps.features;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.hapyl.fight.CF;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.MoveType;
import me.hapyl.fight.game.maps.GameMaps;
import me.hapyl.fight.game.maps.MapFeature;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.spigotutils.module.chat.Gradient;
import me.hapyl.spigotutils.module.chat.gradient.Interpolators;
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

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Set;

public class JapanFeature extends MapFeature implements Listener {

    private final Location[] healingSakuraLocations = {
            BukkitUtils.defLocation(-464, 65, -6),
            BukkitUtils.defLocation(-520, 65, -6)
    };

    private final Location pressurePlateLocation = BukkitUtils.defLocation(-492, 67.8, -20);

    private final Map<Player, Integer> inWaterTickMap = Maps.newHashMap();
    private final Set<GamePlayer> sakuraBlessing = Sets.newConcurrentHashSet();

    private final Vector verticalVector = new Vector(0.0d, 1.9d, 0.0d);
    private final String healingMessage = new Gradient("You feel sakura's petals on your head").rgb(
            new java.awt.Color(217, 100, 213),
            new java.awt.Color(191, 40, 186),
            Interpolators.LINEAR
    );

    public JapanFeature() {
        super("Healing Sakura", """
                Stand inside &eSakura's &7&orange to feel its healing petals!
                """);
    }

    @Override
    public void tick(int tick) {
        sakuraBlessing.forEach(player -> {
            if (player == null) {
                return;
            }

            player.heal(1);
        });

        for (final Location location : healingSakuraLocations) {
            Collect.nearbyPlayers(location, 8.0d).forEach(gamePlayer -> {
                if (sakuraBlessing.contains(gamePlayer) || !canBeHealed(gamePlayer)) {
                    return;
                }

                sakuraBlessing.add(gamePlayer);
                gamePlayer.sendSubtitle(healingMessage, 0, 20, 5);
            });
        }
    }

    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        sakuraBlessing.remove(player);
    }

    @Override
    public void onStop() {
        inWaterTickMap.clear();
    }

    @Override
    public void onStart() {
        new GameTask() {
            private double theta = 0;

            @Override
            public void run() {
                final double x = 0.8d * Math.sin(theta);
                final double z = 0.8d * Math.cos(theta);

                pressurePlateLocation.add(x, 0, z);
                PlayerLib.spawnParticle(pressurePlateLocation, Particle.FIREWORKS_SPARK, 1, 0, 0, 0, 0);
                PlayerLib.spawnParticle(pressurePlateLocation, Particle.FLAME, 1, 0, 0, 0, 0.025f);
                pressurePlateLocation.subtract(x, 0, z);

                theta += Math.PI / 8;
                if (theta > Math.PI * 2) {
                    theta = 0;
                }
            }
        }.runTaskTimer(0, 2);
    }

    public void boostPlayer(GamePlayer player) {
        player.setVelocity(verticalVector);
        player.playWorldSound(Sound.ENTITY_WITHER_SHOOT, 0.75f);

        GameTask.runLater(() -> {
            final Vector vector = player.getLocation().getDirection().normalize().setY(0.0d).multiply(1.0d);

            player.setVelocity(vector);
            player.playWorldSound(Sound.ENTITY_WITHER_SHOOT, 1.5f);
        }, 20);

        player.addEffect(Effects.FALL_DAMAGE_RESISTANCE, 60);
    }

    @EventHandler()
    public void handlePlayerInteractEvent(PlayerInteractEvent ev) {
        final Player player = ev.getPlayer();
        final Action action = ev.getAction();
        final Block block = ev.getClickedBlock();

        if (!validateCurrentMap(GameMaps.JAPAN)) {
            return;
        }

        final GamePlayer gamePlayer = CF.getPlayer(player);

        if (gamePlayer == null) {
            return;
        }

        if (action == Action.PHYSICAL
                && block != null
                && block.getType() == Material.LIGHT_WEIGHTED_PRESSURE_PLATE) {
            boostPlayer(gamePlayer);
        }
    }

    private boolean canBeHealed(GamePlayer player) {
        return !player.hasMovedInLast(MoveType.KEYBOARD, 5000);
    }

}
