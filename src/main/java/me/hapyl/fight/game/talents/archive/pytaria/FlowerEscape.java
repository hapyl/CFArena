package me.hapyl.fight.game.talents.archive.pytaria;

import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.archive.pytaria.Pytaria;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.task.TimedGameTask;
import me.hapyl.fight.game.term.Terminology;
import me.hapyl.fight.game.term.Terms;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.block.display.BlockStudioParser;
import me.hapyl.spigotutils.module.block.display.DisplayData;
import me.hapyl.spigotutils.module.block.display.DisplayEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class FlowerEscape extends Talent implements Terminology {

    private final DisplayData display = BlockStudioParser.parse(
            "{Passengers:[{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:tall_grass\",Properties:{half:\"lower\"}},transformation:[0.4918f,0.0000f,0.0000f,-0.2500f,0.0000f,0.4918f,0.0000f,-0.2500f,0.0000f,0.0000f,0.4918f,-0.1875f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:sunflower\",Properties:{half:\"lower\"}},transformation:[1.0000f,0.0000f,0.0000f,-0.4982f,0.0000f,1.0000f,0.0000f,-0.1875f,0.0000f,0.0000f,1.0000f,-0.3748f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:red_tulip\",Properties:{}},transformation:[0.7038f,-0.4041f,0.2232f,-0.4560f,0.4617f,0.6161f,-0.3403f,0.1875f,-0.0000f,0.4070f,0.7368f,-0.2350f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:red_tulip\",Properties:{}},transformation:[0.1739f,-0.0000f,0.5963f,-0.3816f,-0.3477f,0.5047f,0.1014f,0.7500f,-0.4845f,-0.3622f,0.1413f,0.3125f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:red_tulip\",Properties:{}},transformation:[1.0000f,0.0000f,0.0000f,-0.5000f,0.0000f,1.0000f,0.0000f,0.7500f,0.0000f,0.0000f,1.0000f,-0.3750f,0.0000f,0.0000f,0.0000f,1.0000f]}]}"
    );

    @DisplayField(suffix = "blocks") private final double flowerRadius = 2.5d;
    @DisplayField private final double flowerDamage = 5.0d;
    @DisplayField private final int pulsePeriod = 20;

    public FlowerEscape() {
        super("Flower Escape", """
                Throw a deadly flower at your current location and dash backwards.
                                
                The flower will continuously pulse and deal damage to nearby players.
                                        
                After the duration is over, it will explode dealing &bdouble&7 the damage.
                """);

        setItem(Material.RED_TULIP);
        setDurationSec(6);
        setCooldownSec(12);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final Vector vector = player.getLocation().getDirection().normalize().multiply(-1.5d);
        player.setVelocity(vector.setY(0.5d));

        final Location location = player.getLocation();
        location.setYaw(0.0f);
        location.setPitch(0.0f);

        final double snapshotDamage = Heroes.PYTARIA.getHero(Pytaria.class).calculateDamage(player, flowerDamage, EnumDamageCause.FLOWER);
        final DisplayEntity entity = display.spawn(location);

        new TimedGameTask(getDuration()) {
            @Override
            public void run(int tick) {
                final double y = Math.sin(Math.toRadians(tick) * 2) / 6;

                if (tick % pulsePeriod == 0) {
                    damage(false);
                }

                location.setYaw(location.getYaw() + 5);
                location.add(0, y, 0);

                entity.teleport(location);

                location.subtract(0, y, 0);
            }

            @Override
            public void onLastTick() {
                damage(true);
                entity.remove();
            }

            private void damage(boolean lastTick) {
                Collect.nearbyEntities(location, flowerRadius).forEach(entity -> {
                    if (entity.equals(player)) {
                        return;
                    }

                    entity.damage(lastTick ? snapshotDamage * 2 : snapshotDamage, player, EnumDamageCause.FLOWER);
                });

                // Fx
                new TimedGameTask(5) {
                    @Override
                    public void run(int tick) {
                        final double radius = flowerRadius / maxTick * (tick + 1);
                        final double increase = Math.PI / ((tick + 1) * 4);

                        for (double d = 0.0d; d < Math.PI * 2; d += increase) {
                            final double x = Math.sin(d) * radius;
                            final double z = Math.cos(d) * radius;

                            location.add(x, 1.0d, z);
                            player.spawnWorldParticle(location, Particle.TOTEM, 1, 0, 0, 0, 0.05f);
                            player.spawnWorldParticle(location, Particle.SMOKE_NORMAL, 1, 0, 0, 0, 0.05f);
                            location.subtract(x, 1.0d, z);
                        }
                    }
                }.runTaskTimer(0, 1);

                player.playWorldSound(location, Sound.BLOCK_NOTE_BLOCK_COW_BELL, 0.5f + (1.25f / maxTick * getTick()));
                player.playWorldSound(location, Sound.ENTITY_ENDER_DRAGON_FLAP, 1.75f);

                if (lastTick) {
                    player.playWorldSound(location, Sound.ITEM_TOTEM_USE, 2.0f);
                    player.spawnWorldParticle(location, Particle.SPELL_MOB, 20, 0.25d, 0.25d, 0.25d, 0.1f);
                }
            }
        }.runTaskTimer(1, 1);
        return Response.OK;
    }

    @Nonnull
    @Override
    public Terms getTerm() {
        return Terms.SNAPSHOT;
    }
}
