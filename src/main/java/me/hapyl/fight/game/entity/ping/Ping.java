package me.hapyl.fight.game.entity.ping;

import me.hapyl.eterna.module.block.display.BDEngine;
import me.hapyl.eterna.module.block.display.DisplayData;
import me.hapyl.eterna.module.block.display.DisplayEntity;
import me.hapyl.eterna.module.util.Removable;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.team.GameTeam;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.scoreboard.Team;

import javax.annotation.Nonnull;

public class Ping extends GameTask implements Removable {

    private static final DisplayData DATA = BDEngine.parse(
            "{Passengers:[{id:\"minecraft:item_display\",item:{id:\"minecraft:smooth_quartz_stairs\",Count:1},item_display:\"none\",transformation:[0.3536f,-0.3536f,0.0000f,-0.0215f,0.3536f,0.3536f,0.0000f,0.3536f,0.0000f,0.0000f,0.2500f,0.0000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:smooth_quartz\",Count:1},item_display:\"none\",transformation:[0.2500f,0.0000f,0.0000f,-0.0215f,0.0000f,0.2500f,0.0000f,0.6563f,0.0000f,0.0000f,0.2500f,0.0000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:smooth_quartz\",Count:1},item_display:\"none\",transformation:[0.2500f,0.0000f,0.0000f,-0.0215f,0.0000f,0.2500f,0.0000f,0.9063f,0.0000f,0.0000f,0.2500f,0.0000f,0.0000f,0.0000f,0.0000f,1.0000f]}]}"
    );

    private final PlayerPing ping;
    private final GamePlayer player;
    private final PingType type;
    private final DisplayEntity entity;

    protected Ping(PlayerPing ping, PingType type) {
        this.ping = ping;
        this.player = ping.player;
        this.type = type;

        // Setup entity
        final Location location = getPingLocation();

        entity = DATA.spawn(location, self -> {
            self.setVisibleByDefault(false);
            self.setGlowing(true);
        });

        //entity = Entities.ARMOR_STAND_MARKER.spawn(location, self -> {
        //    self.setVisible(false);
        //    self.setSilent(true);
        //    self.setVisibleByDefault(false);
        //    self.setHelmet(new ItemStack(Material.SPECTRAL_ARROW));
        //    self.setHeadPose(new EulerAngle(0, 0, Math.toRadians(135.0d)));
        //    self.setGlowing(true);
        //});

        // Setup team and show entity
        final GameTeam gameTeam = player.getTeam();

        gameTeam.getPlayers().forEach(teammate -> {
            final Team team = teammate.getOrCreateScoreboardTeam("!ping-" + type);

            team.setColor(type.getColor());

            entity.forEach(display -> {
                team.addEntry(display.getUniqueId().toString());
                teammate.showEntity(display);
            });
        });

        // Play sound
        type.getSound().play(gameTeam.getBukkitPlayers(), location);

        // Schedule task
        runTaskLater(type.getDuration());
    }

    @Override
    public void run() {
        entity.remove();
        ping.buffer.remove(this);
    }

    @Override
    public void remove() {
        entity.remove();
        cancel();
    }

    @Nonnull
    private Location getPingLocation() {
        final Block block = player.getTargetBlockExact(50);
        final Location location = block != null ? block.getLocation().add(0.5d, 0.0d, 0.5d) : player.getLocation();

        location.setYaw(0.0f);
        location.setPitch(0.0f);

        return location.add(0.0d, 1.0d, 0.0d);
        //return location.add(-0.5d, -0.8d, 0.25d);
    }
}
