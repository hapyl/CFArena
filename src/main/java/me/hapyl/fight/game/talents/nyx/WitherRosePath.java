package me.hapyl.fight.game.talents.nyx;

import me.hapyl.eterna.module.block.display.BlockStudioParser;
import me.hapyl.eterna.module.block.display.DisplayData;
import me.hapyl.eterna.module.block.display.DisplayEntity;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.task.TickingStepGameTask;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class WitherRosePath extends Talent {

    private final DisplayData spike = BlockStudioParser.parse(
            "{Passengers:[{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:obsidian\",Properties:{}},transformation:[0.8125f,0.0000f,0.0000f,-0.3750f,0.0000f,0.8125f,0.0000f,0.0000f,0.0000f,0.0000f,0.8125f,-0.3750f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:obsidian\",Properties:{}},transformation:[0.5625f,0.0000f,0.0000f,-0.3750f,0.0000f,0.6250f,0.0000f,0.8125f,0.0000f,0.0000f,0.5625f,-0.3750f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:obsidian\",Properties:{}},transformation:[0.3750f,0.0000f,0.0000f,-0.3750f,0.0000f,0.4375f,0.0000f,1.4375f,0.0000f,0.0000f,0.3750f,-0.3750f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:obsidian\",Properties:{}},transformation:[0.2500f,0.0000f,0.0000f,-0.3750f,0.0000f,0.3750f,0.0000f,1.8750f,0.0000f,0.0000f,0.2500f,-0.3750f,0.0000f,0.0000f,0.0000f,1.0000f]}]}"
    );

    @DisplayField
    private final double maxDistance = 30;

    public WitherRosePath() {
        super("Wither Path");

        setDescription("""
                Launch a path of wither roses in front of you.
                """);

        setItem(Material.WITHER_ROSE);
        setType(TalentType.IMPAIR);

        setCooldownSec(8);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final Vector direction = player.getDirection();
        final Location location = player.getLocation();

        new TickingStepGameTask(3) {
            private double d = 0.0d;

            @Override
            public boolean tick(int tick) {
                if (d >= maxDistance) {
                    cancel();
                    return true;
                }

                final double x = direction.getX() * d;
                final double y = direction.getY() * d;
                final double z = direction.getZ() * d;

                location.add(x, y, z);
                createRose(player, CFUtils.anchorLocation(location));
                location.subtract(x, y, z);

                d += 1.0d;
                return false;
            }
        }.runTaskTimer(0, 1);

        return Response.OK;
    }

    private void createRose(GamePlayer player, Location location) {
        location.setYaw(player.random.nextFloat() * 180);
        location.setYaw(player.random.nextFloat() * 30);

        final DisplayEntity displayEntity = spike.spawn(location, self -> {
        });

        player.schedule(() -> {
            displayEntity.remove();
        }, 20);
    }

}
