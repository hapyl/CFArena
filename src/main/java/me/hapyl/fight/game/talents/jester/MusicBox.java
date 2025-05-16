package me.hapyl.fight.game.talents.jester;

import me.hapyl.eterna.module.block.display.DisplayData;
import me.hapyl.eterna.module.block.display.DisplayEntity;
import me.hapyl.eterna.module.block.display.animation.AnimationFrame;
import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.eterna.module.util.Removable;
import me.hapyl.fight.CF;
import me.hapyl.fight.game.entity.GamePlayer;
import org.bukkit.Location;

import javax.annotation.Nonnull;

public class MusicBox implements Removable {

    private final GamePlayer player;
    private final Location location;
    private final DisplayEntity entity;

    public MusicBox(GamePlayer player, Location location, DisplayData displayData) {
        this.player = player;
        this.location = location;
        this.entity = displayData.spawnInterpolated(location);

        entity.newAnimation(CF.getPlugin())
                .addFrame(new AnimationFrame(Math.PI * 2, Math.PI / 16, 2) {
                    @Override
                    public void tick(@Nonnull DisplayEntity entity, double theta) {
                        final Location location = entity.getLocation();
                        location.add(0, Math.sin(theta) * BukkitUtils.GRAVITY, 0);
                        location.setYaw((float) (360 * theta / threshold));

                        entity.teleport(location);
                    }
                })
                .addFrame(new AnimationFrame(Math.PI / 2, Math.PI / 16, 4) {
                    @Override
                    public void tick(@Nonnull DisplayEntity entity, double theta) {
                        entity.setRotation(player.random.nextFloat() * 3, player.random.nextFloat() * 5);
                    }
                })
                .addFrame(new AnimationFrame() {
                    @Override
                    public void tick(@Nonnull DisplayEntity entity, double theta) {
                        entity.teleport(location);
                    }
                })
                .start();
    }

    @Override
    public void remove() {
        entity.remove();
    }

}
