package me.hapyl.fight.game.talents.dark_mage;

import com.google.common.collect.Sets;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.dark_mage.SpellButton;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.task.ShutdownAction;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.DirectionalMatrix;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.math.geometry.Draw;
import me.hapyl.eterna.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Wither;

import javax.annotation.Nonnull;
import java.util.Set;

public class BlindingCurse extends DarkMageTalent {

    @DisplayField private final double maxDistance = 35.0d;
    @DisplayField private final double damage = 7.5d;
    @DisplayField private final int impairDuration = 40;

    @DisplayField(attribute = AttributeType.SPEED) private final double speedReduction = 0.1d;

    private final Draw curseDraw = new Draw(null) {
        @Override
        public void draw(@Nonnull Location location) {
            PlayerLib.spawnParticle(location, Particle.LARGE_SMOKE, 1, 0.01d, 0.01d, 0.01d, 0.025f);
            PlayerLib.spawnParticle(location, Particle.SMOKE, 2, 0.02d, 0.02d, 0.02d, 0.025f);
        }
    };

    public BlindingCurse() {
        super("Darkness Curse", """
                Launch a &8darkness curse&7 forward that deals &cdamage&7 and &eimpairs&7 hit &cenemies&7.
                """);

        setType(TalentType.DAMAGE);
        setItem(Material.INK_SAC);
        setCooldownSec(10);
    }

    @Nonnull
    @Override
    public SpellButton first() {
        return SpellButton.RIGHT;
    }

    @Nonnull
    @Override
    public SpellButton second() {
        return SpellButton.RIGHT;
    }

    @Override
    public Response executeSpell(@Nonnull GamePlayer player) {
        final Location location = player.getEyeLocation();
        final DirectionalMatrix matrix = player.getLookAlongMatrix();

        new GameTask() {
            private final Set<LivingGameEntity> hitEntities = Sets.newHashSet();
            private final int fxDuration = 10;

            private double distanceTravelled = 0.0d;
            private int count = 0;

            @Override
            public void run() {
                for (int i = 6; i > 0; i--) {
                    if (next()) {
                        cancel();
                        hitEntities.clear();
                        return;
                    }

                    ++count;
                }
            }

            private boolean next() {
                if ((distanceTravelled += 0.25d) >= maxDistance) {
                    return true;
                }

                final double x = Math.sin(distanceTravelled * 4) * 0.5d;
                final double y = Math.cos(distanceTravelled * 4) * 0.5d;
                final double z = count * 0.5;

                matrix.transformLocation(location, x, y, z, then -> {
                    curseDraw.draw(location);

                    // Hit detection
                    checkHit(location);
                });

                matrix.transformLocation(location, y, x, z, then -> {
                    curseDraw.draw(location);
                });

                return false;
            }

            private void checkHit(Location location) {
                Collect.nearbyEntities(location, 1.0d).forEach(entity -> {
                    if (player.isSelfOrTeammate(entity) || hitEntities.contains(entity)) {
                        return;
                    }

                    hitEntities.add(entity);

                    entity.damageNoKnockback(damage, player, EnumDamageCause.DARKNESS_CURSE);

                    final EntityAttributes attributes = entity.getAttributes();
                    attributes.decreaseTemporary(Temper.DARKNESS, AttributeType.SPEED, speedReduction, impairDuration);

                    // Scary wither in my face, AHHHHH
                    if (entity instanceof GamePlayer playerEntity) {
                        final Location witherLocation = playerEntity.getLocationInFront(3.8d);

                        final Wither wither = Entities.WITHER.spawn(witherLocation, self -> {
                            self.setSilent(true);
                            self.setVisibleByDefault(false);
                            self.setInvulnerable(true);
                            self.setAI(false);
                        });

                        CFUtils.lookAt(wither, playerEntity.getLocation());

                        playerEntity.showEntity(wither);
                        playerEntity.addEffect(Effects.BLINDNESS, 3, fxDuration + 20);
                        playerEntity.playSound(Sound.ENTITY_WITHER_SPAWN, 1.75f);

                        GameTask.runLater(wither::remove, fxDuration).setShutdownAction(ShutdownAction.IGNORE);
                    }
                });
            }

        }.runTaskTimer(0, 1);

        // Fx
        player.playWorldSound(Sound.ENTITY_SQUID_SQUIRT, 1.25f);

        return Response.OK;
    }

}
