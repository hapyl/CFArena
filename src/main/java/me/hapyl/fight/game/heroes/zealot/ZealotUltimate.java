package me.hapyl.fight.game.heroes.zealot;

import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.UltimateResponse;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.task.player.PlayerTickingGameTask;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.eterna.module.block.display.BlockStudioParser;
import me.hapyl.eterna.module.block.display.DisplayData;
import me.hapyl.eterna.module.block.display.DisplayEntity;
import me.hapyl.eterna.module.math.Geometry;
import me.hapyl.eterna.module.math.geometry.WorldParticle;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class ZealotUltimate extends UltimateTalent {

    private final DisplayData giantSword = BlockStudioParser.parse(
            "/summon block_display ~-0.5 ~-0.5 ~-0.5 {Passengers:[{id:\"minecraft:item_display\",item:{id:\"minecraft:golden_sword\",Count:1},item_display:\"none\",transformation:[2.6043f,3.5194f,-2.4148f,-0.2500f,3.4151f,-3.4151f,-1.2941f,1.2500f,-2.5602f,-0.9753f,-4.1826f,-0.5000f,0.0000f,0.0000f,0.0000f,1.0000f]}]}"
    );

    private final Zealot hero;

    @DisplayField public final double baseDamage = 4.0d;
    @DisplayField public final double landingOffset = 10.0d;
    @DisplayField public final double distance = 5.0d;
    @DisplayField public final int impactTime = 15;
    @DisplayField public final double directionOffset = 2.5d;
    @DisplayField public final double landingSpeed = Math.PI / 14;

    public ZealotUltimate(@Nonnull Zealot hero) {
        super("Maintain Order", """
                Command a &egiant sword&7 to &afall down&7 from the &bsky&7.

                Upon landing, &4explodes&7 violently, inflicting %s on nearby &cenemies&7 based on your %s stacks.
                """.formatted(AttributeType.FEROCITY, Named.FEROCIOUS_STRIKE), 60);

        this.hero = hero;

        setType(TalentType.DAMAGE);
        setItem(Material.GOLDEN_SWORD);
        setDurationSec(12);

        setSound(Sound.ENTITY_WITHER_HURT, 0.0f);
    }

    @Nonnull
    @Override
    public UltimateResponse useUltimate(@Nonnull GamePlayer player) {
        final ZealotData data = hero.getPlayerData(player);

        if (data.ferociousHits <= 0) {
            return UltimateResponse.error("No " + Named.FEROCIOUS_STRIKE + " &cstacks!");
        }

        final Location location = player.getLocation();
        location.setPitch(0.0f);

        final Vector direction = location.getDirection().setY(0.0d);

        location.add(direction.multiply(directionOffset));

        final Location landingLocation = CFUtils.anchorLocation(location);
        final DisplayEntity entity = giantSword.spawnInterpolated(landingLocation.clone().add(0, landingOffset, 0));

        new PlayerTickingGameTask(player) {
            private final double y = entity.getHead().getLocation().getY();
            private double traveled = 0.0d;
            private int landedAt = -1;

            @Override
            public void run(int tick) {
                // Fx
                Geometry.drawPolygon(landingLocation, 5, distance, new WorldParticle(Particle.CRIT));

                // Land
                if (traveled >= landingOffset) {
                    if (landedAt == -1) {
                        landedAt = tick;

                        // Fx
                        player.playWorldSound(landingLocation, Sound.ITEM_SHIELD_BREAK, 0.75f);
                    }

                    // Damage
                    if (tick - landedAt >= impactTime) {
                        cancel();

                        final int ferociousHits = data.ferociousHits;

                        data.ferociousHits = 0;

                        Collect.nearbyEntities(landingLocation, distance).forEach(entity -> {
                            if (player.isSelfOrTeammate(entity)) {
                                return;
                            }

                            entity.executeFerocity(baseDamage, player, ferociousHits, true);
                        });

                        // Fx
                        player.playWorldSound(landingLocation, Sound.ENTITY_GENERIC_EXPLODE, 0.75f);
                        player.spawnWorldParticle(landingLocation.add(0, 2.5, 0), Particle.CRIT, 20, 0.1d, 0.5d, 0.1, 1.0f);
                    }
                    return;
                }

                final double cos = Math.cos(landingSpeed);
                final Location location = entity.getHead().getLocation();

                traveled = Math.min(traveled + cos, landingOffset);

                location.setY(y - traveled);
                entity.teleport(location);
            }

            @Override
            public void onTaskStop() {
                entity.remove();
            }
        }.runTaskTimer(0, 1);

        return UltimateResponse.OK;
    }
}
