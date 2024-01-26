package me.hapyl.fight.game.talents.archive.dark_mage;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.archive.dark_mage.SpellButton;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.math.Geometry;
import me.hapyl.spigotutils.module.math.geometry.Draw;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlindingCurse extends DarkMageTalent {

    @DisplayField private final double maxDistance = 35.0d;
    @DisplayField private final double damage = 7.5d;
    @DisplayField private final int blindingDuration = 40;
    @DisplayField private final int slowingDuration = 40;

    private final Draw curseDraw = new Draw(null) {
        @Override
        public void draw(@Nonnull Location location) {
            PlayerLib.spawnParticle(location, Particle.SMOKE_LARGE, 1, 0.01d, 0.01d, 0.01d, 0.025f);
            PlayerLib.spawnParticle(location, Particle.SMOKE_NORMAL, 2, 0.02d, 0.02d, 0.02d, 0.025f);
        }
    };

    public BlindingCurse() {
        super("Darkness Curse", """
                Impair the &etarget&7 enemy, dealing &cdamage&7, &8blinding&7 and &3slowing&7 them.
                """);

        setType(Type.DAMAGE);
        setItem(Material.INK_SAC);
        setCooldownSec(10);
    }

    @Nonnull
    @Override
    public String getAssistDescription() {
        return "The curse &abounces&7 to two additional targets.";
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
        final LivingGameEntity target = Collect.targetEntityDot(
                player,
                maxDistance,
                0.9d,
                living -> !living.equals(player) && living.hasLineOfSight(player)
        );

        if (target == null) {
            return Response.error("No valid target!");
        }

        execute0(player, player, target);

        // Have to use assist here since it's based on ability casting
        if (hasWither(player)) {
            final LivingGameEntity bounce = bounce(player, target);
            bounce(player, bounce, target);
        }

        return Response.OK;
    }

    @Nullable
    private LivingGameEntity bounce(@Nonnull GamePlayer player, LivingGameEntity... bounced) {
        if (bounced == null || bounced.length == 0 || bounced[0] == null) {
            return null;
        }

        final LivingGameEntity bounce = Collect.nearestEntity(
                bounced[0].getLocation(),
                10.0d,
                living -> {
                    for (LivingGameEntity livingEntity : bounced) {
                        if (livingEntity.equals(living)) {
                            return false;
                        }
                    }

                    return !living.equals(player);
                }
        );

        if (bounce != null) {
            execute0(player, bounced[0], bounce);
            return bounce;
        }

        return null;
    }

    private void execute0(@Nonnull GamePlayer player, @Nonnull LivingGameEntity from, @Nonnull LivingGameEntity to) {
        final Location location = from.getLocation();

        Geometry.drawLine(
                location.add(0, 1, 0),
                to.getLocation().add(0, 1, 0),
                0.5, curseDraw
        );

        player.playWorldSound(location, Sound.ENTITY_GLOW_SQUID_SQUIRT, 1.8f);
        curseDraw.draw(location);

        to.sendTitle("&0&l☠", "&0&l☠", 0, blindingDuration - 10, 10);

        to.addEffect(Effects.BLINDNESS, 10, blindingDuration);
        to.addEffect(Effects.SLOW, 1, slowingDuration);

        to.damage(damage, player, EnumDamageCause.DARKNESS_CURSE);
    }

}
