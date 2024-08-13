package me.hapyl.fight.game.trial.objecitive;

import me.hapyl.eterna.module.math.Cuboid;
import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.fight.game.GameElement;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.trial.Trial;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TrialObjectivePath extends Cuboid implements GameElement, Ticking {

    private static final BlockData airData = Material.AIR.createBlockData();

    private final Trial trial;
    private final BlockData data;
    private final List<Location> locations;

    public TrialObjectivePath(Trial trial, Material material, double x, double y, double z, double x2, double y2, double z2) {
        super(x, y, z, x2, y2, z2);

        this.trial = trial;
        this.data = material.createBlockData();
        this.locations = bakeLocations();
    }

    @Override
    public void onStart() {
        tick();
    }

    @Override
    public void onStop() {
        // Just in case of server lag or something.
        GameTask.runLater(() -> {
            sendBlockChange(airData);

            // Fx
            final GamePlayer player = trial.getPlayer();
            final Location location = getCenter();

            player.playSound(location, data.getSoundGroup().getBreakSound(), 0.0f);
            player.spawnParticle(location, Particle.BLOCK, 5, 0.8d, 0.8d, 0.8d, 1, data);
        }, 5);
    }

    @Override
    public void tick() {
        sendBlockChange(data);
    }

    private void sendBlockChange(BlockData data) {
        final Player player = trial.getPlayer().getPlayer();

        for (Location location : locations) {
            player.sendBlockChange(location, data);
        }
    }

    private List<Location> bakeLocations() {
        final List<Location> locations = new ArrayList<>();
        final List<Block> blocks = getBlocks();

        blocks.forEach(block -> locations.add(block.getLocation()));
        blocks.clear();

        return locations;
    }

}
