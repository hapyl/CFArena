package me.hapyl.fight.game.talents.swooper;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.talents.techie.Talent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.RetainSet;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Item;

import javax.annotation.Nonnull;

public class SmokeBomb extends Talent {

    @DisplayField private final double radius = 3.0d;
    @DisplayField private final double speedIncrease = 0.02d;

    private final double radiusScaled = (radius * radius) / 8.0d;

    public SmokeBomb() {
        super("Smoke Bomb");

        setDescription("""
                Throw a smoke bomb in front of you.
                                
                Upon &nlanding&7, create a &8smoke field&7 that &8blinds&7 and &b&nhighlights&7 &cenemies&7.
                                
                &8;;Also gain a small speed increase.
                """);

        setType(TalentType.SUPPORT);
        setItem(Material.ENDERMITE_SPAWN_EGG);
        setCooldownSec(20);
        setDurationSec(5);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final Item item = player.throwItem(getMaterial(), player.getEyeDirection());

        player.getAttributes().increaseTemporary(Temper.SMOKE_BOMB, AttributeType.SPEED, speedIncrease, getDuration());

        new GameTask() {
            private final RetainSet<LivingGameEntity> retainSet = new RetainSet<>() {
                @Override
                public void onAdd(@Nonnull LivingGameEntity entity) {
                    entity.setGlowing(player, ChatColor.DARK_GRAY);
                }

                @Override
                public void onRemove(@Nonnull LivingGameEntity entity) {
                    entity.stopGlowing(player);
                }
            };

            private int tick;

            @Override
            public void run() {
                if (item.isDead() || tick >= getDuration()) {
                    retainSet.clear();
                    cancel();
                    return;
                }

                if (!item.isOnGround()) {
                    return;
                }

                final Location location = item.getLocation();

                Collect.nearbyEntities(location, radius).forEach(entity -> {
                    entity.addEffect(Effects.BLINDNESS, 1, 25);

                    if (player.isSelfOrTeammate(entity)) {
                        return;
                    }

                    retainSet.add(entity);
                });

                retainSet.retain();

                // Fx
                player.spawnWorldParticle(
                        location,
                        Particle.SMOKE_LARGE,
                        20,
                        radiusScaled,
                        radiusScaled,
                        radiusScaled,
                        0.01f
                );

                player.spawnWorldParticle(
                        location,
                        Particle.SMOKE_NORMAL,
                        20,
                        radiusScaled,
                        radiusScaled,
                        radiusScaled,
                        0.01f
                );

                tick++;
            }

            @Override
            public void onTaskStop() {
                item.remove();
            }
        }.runTaskTimer(0, 1);

        return Response.OK;
    }
}
