package me.hapyl.fight.game.talents.nyx;

import me.hapyl.eterna.module.block.display.BlockStudioParser;
import me.hapyl.eterna.module.block.display.DisplayData;
import me.hapyl.eterna.module.block.display.DisplayEntity;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.entity.EntityRandom;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.task.TickingStepGameTask;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class WitherRosePath extends Talent {

    private final DisplayData spike = BlockStudioParser.parse(
            "/summon block_display ~-0.5 ~-0.5 ~-0.5 {Passengers:[{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:obsidian\",Properties:{}},transformation:[0.8750f,0.0000f,0.0000f,-0.4375f,0.0000f,1.0000f,0.0000f,-0.5625f,0.0000f,0.0000f,0.8750f,-0.4375f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:obsidian\",Properties:{}},transformation:[0.6250f,0.0000f,0.0000f,-0.3125f,0.0000f,0.7500f,0.0000f,0.2500f,0.0000f,0.0000f,0.6250f,-0.3125f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:obsidian\",Properties:{}},transformation:[0.5000f,0.0000f,0.0000f,-0.2500f,0.0000f,0.6875f,0.0000f,0.8750f,0.0000f,0.0000f,0.5000f,-0.2500f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:obsidian\",Properties:{}},transformation:[0.1768f,0.0000f,0.1768f,-0.1875f,0.0000f,0.3750f,0.0000f,1.5625f,-0.1768f,0.0000f,0.1768f,0.0000f,0.0000f,0.0000f,0.0000f,1.0000f]}]}"
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
        final Vector direction = player.getDirection().setY(0);
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
        final EntityRandom random = player.random;

        location.add(random.nextDouble(), random.nextDouble() * 0.5d, random.nextDouble());

        location.setYaw(random.nextFloat() * 180);
        location.setPitch(random.nextFloat() * 60);

        final DisplayEntity displayEntity = spike.spawn(location, self -> {
        });

        // Damage and debuff
        Collect.nearbyEntities(location, 1, player::isNotSelfOrTeammate)
                .forEach(entity -> {
                    entity.damage(5, player, EnumDamageCause.NYX_SPIKE);
                });

        player.schedule(() -> {
            displayEntity.remove();
        }, 20);
    }

}
