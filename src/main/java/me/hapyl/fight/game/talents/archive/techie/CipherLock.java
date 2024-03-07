package me.hapyl.fight.game.talents.archive.techie;

import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.attribute.temper.TemperInstance;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.loadout.HotbarSlots;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.DirectionalMatrix;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.math.Tick;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CipherLock extends TechieTalent {

    @DisplayField private final double maxFlightTime = Tick.fromSecond(3);
    @DisplayField private final int impairDuration = Tick.fromSecond(20);

    private final double step = 3;
    private final TemperInstance temperInstance = Temper.CIPHER_LOCK.newInstance()
            .decrease(AttributeType.ATTACK, 0.2d)  //
            .decrease(AttributeType.SPEED, 0.04d); // 20%

    public CipherLock() {
        super("Cipher Lock");

        setType(Type.IMPAIR);
        setItem(Material.SPECTRAL_ARROW);
        setCooldownSec(16);
        setCastingTime(15);
    }

    @Nonnull
    @Override
    public String getHackDescription() {
        return """
                launch an &benergy blast&7 in front of you.
                                
                The energy &btravels forward&7 until it hits an &cenemy&7 or a &8block&7.
                                
                Upon hitting an &cenemy&7, &eimpair&7 them and &dlock&7 a random &dtalent&7 for &b{impairDuration}&7.
                """;
    }

    @Override
    public void onHack(@Nonnull GamePlayer player) {
        final DirectionalMatrix matrix = player.getLookAlongMatrix();
        final Location location = player.getEyeLocation();
        final Vector direction = location.getDirection().normalize();

        new TickingGameTask() {
            private final double maxFlightTimeScaled = maxFlightTime * step;
            private double d;
            private double flownDistance;

            @Override
            public void run(int tick) {
                for (int i = 0; i < step; i++) {
                    if (next(i)) {
                        cancel();
                        return;
                    }
                }
            }

            private void hit(@Nullable LivingGameEntity entity) {
                player.spawnWorldParticle(location, Particle.ELECTRIC_SPARK, 20, 0.1d, 0.1d, 0.1d, 0.5f);
                player.playWorldSound(location, Sound.BLOCK_GLASS_BREAK, 0.0f);

                if (entity == null) {
                    return;
                }

                entity.playWorldSound(location, Sound.ENTITY_ENDERMAN_HURT, 0.75f);

                final EntityAttributes attributes = entity.getAttributes();
                temperInstance.temper(attributes, impairDuration);

                if (!(entity instanceof GamePlayer hitPlayer)) {
                    return;
                }

                final HotbarSlots lockedSlot = hitPlayer.getTalentLock().setLockRandomly(impairDuration);

                if (lockedSlot == null) {
                    return;
                }

                final Talent lockedTalent = hitPlayer.getHero().getTalent(lockedSlot);

                if (lockedTalent == null) {
                    return;
                }

                final String talentName = lockedTalent.getName();

                player.sendMessage("&5🔒 &dYou locked %s's &l%s&d!", entity.getName(), talentName);
                entity.sendMessage("&5🔒 &d%s's locked your &l%s&d!", player.getName(), talentName);
            }

            private boolean next(int i) {
                if (flownDistance > maxFlightTimeScaled) {
                    return true;
                }

                // Block hit
                if (location.getBlock().getType().isOccluding()) {
                    hit(null);
                    return true;
                }

                final LivingGameEntity hitEntity = Collect.nearestEntity(location, 1, player);

                if (hitEntity != null) {
                    hit(hitEntity);
                    return true;
                }

                final double x = Math.sin(d) * 0.25d;
                final double y = Math.cos(d) * 0.25d;
                final double z = d / 64;

                final Vector vector = matrix.transform(x, y, z);
                location.add(vector);

                // Travel Fx
                // Only display on first iteration
                if (i == 0) {
                    player.spawnWorldParticle(location, Particle.SPELL_WITCH, 1);
                    player.spawnWorldParticle(location, Particle.CRIT_MAGIC, 1);

                    player.playWorldSound(
                            location,
                            Sound.ENTITY_ENDERMAN_AMBIENT,
                            (float) (1.5f + (0.5f / maxFlightTimeScaled * flownDistance))
                    );
                }

                location.subtract(vector);

                d += Math.PI / 20;
                flownDistance++;

                // Traverse
                location.add(direction);

                return false;
            }

        }.runTaskTimer(0, 1);
    }
}
