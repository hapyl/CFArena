package me.hapyl.fight.game.talents.dark_mage;

import me.hapyl.eterna.module.locaiton.LocationHelper;
import me.hapyl.eterna.module.math.geometry.Drawable;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.effect.EffectType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.dark_mage.SpellButton;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.task.TimedGameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import javax.annotation.Nonnull;

import static org.bukkit.Sound.BLOCK_HONEY_BLOCK_SLIDE;

public class SlowingAura extends DarkMageTalent {

    @DisplayField(suffix = "blocks") private final short maxDistance = 20;
    @DisplayField private final double radius = 5.0d;
    @DisplayField private final double cdIncrease = 0.5d;
    @DisplayField private final int interruptionPeriod = 30;
    @DisplayField private final int impairDuration = 60;

    private final Drawable particle = location -> {
        PlayerLib.spawnParticle(location, Particle.EFFECT, 2, 0.1d, 0.1d, 0.1d, 0);
        PlayerLib.spawnParticle(location, Particle.DUST_PLUME, 1, 0.1d, 0.1d, 0.1d, 0);
    };

    public SlowingAura(@Nonnull Key key) {
        super(key, "Slowing Aura", """
                Creates a &fslowness pool&7 at your &etarget&7 block that &3slows&7, increases %s and &4interrupts&7 &cenemies&7 actions.
                """.formatted(AttributeType.COOLDOWN_MODIFIER)
        );

        setType(TalentType.IMPAIR);
        setItem(Material.BONE_MEAL);
        setDurationSec(4);
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
        return SpellButton.LEFT;
    }

    @Override
    public Response executeSpell(@Nonnull GamePlayer player) {
        final Block targetBlock = player.getTargetBlockExact(maxDistance);

        if (targetBlock == null) {
            return Response.error("No valid block in sight!");
        }

        final Location location = targetBlock.getRelative(BlockFace.UP).getLocation();

        new TimedGameTask(this) {

            private double d;

            @Override
            public void run(int tick) {
                // Affect
                Collect.nearbyEntities(location, radius).forEach(entity -> {
                    if (player.isSelfOrTeammate(entity)) {
                        return;
                    }

                    entity.addEffect(EffectType.SLOW, 3, 10);

                    final EntityAttributes attributes = entity.getAttributes();
                    attributes.increaseTemporary(Temper.SLOWING_AURA, AttributeType.COOLDOWN_MODIFIER, cdIncrease, impairDuration);

                    // Interrupt
                    if (modulo(interruptionPeriod) && entity instanceof GamePlayer playerEntity) {
                        playerEntity.interrupt();
                    }
                });

                // Fx
                player.playWorldSound(location, BLOCK_HONEY_BLOCK_SLIDE, 0.0f);

                final double x = Math.sin(d) * radius;
                final double y = Math.sin(Math.toRadians(tick) * 8) * 0.3d;
                final double z = Math.cos(d) * radius;

                LocationHelper.offset(location, x, y, z, particle::draw);
                LocationHelper.offset(location, z, -y, x, particle::draw);

                d += Math.PI / 32;
            }

        }.runTaskTimer(0, 1);

        return Response.OK;
    }

}
