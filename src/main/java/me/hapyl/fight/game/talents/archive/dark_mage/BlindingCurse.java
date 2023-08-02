package me.hapyl.fight.game.talents.archive.dark_mage;

import me.hapyl.fight.CF;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.archive.dark_mage.DarkMageSpell;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.math.Geometry;
import me.hapyl.spigotutils.module.math.geometry.WorldParticle;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlindingCurse extends DarkMageTalent {

    @DisplayField private final double maxDistance = 35.0d;
    @DisplayField private final double damage = 7.5d;
    @DisplayField private final int blindingDuration = 40;
    @DisplayField private final int slowingDuration = 40;

    public BlindingCurse() {
        super("Darkness Curse", """
                Damages, slows and applies blinding curse to the target player.
                """, Material.INK_SAC);

        setCooldownSec(10);
    }

    @Nonnull
    @Override
    public String getAssistDescription() {
        return "The curse bounces to two additional targets.";
    }

    @Nonnull
    @Override
    public DarkMageSpell.SpellButton first() {
        return DarkMageSpell.SpellButton.RIGHT;
    }

    @Nonnull
    @Override
    public DarkMageSpell.SpellButton second() {
        return DarkMageSpell.SpellButton.RIGHT;
    }

    @Override
    public Response executeSpell(Player player) {
        final LivingGameEntity target = Collect.targetEntity(
                player,
                maxDistance,
                0.9d,
                living -> living.isNot(player) && living.hasLineOfSight(player)
        );

        if (target == null) {
            return Response.error("No valid target!");
        }

        execute0(player, player, target.getEntity());

        // Have to use assist here since it's based on ability casting
        if (!Heroes.DARK_MAGE.getHero().isUsingUltimate(player)) {
            return Response.OK;
        }

        // Bounce
        final LivingEntity bounce = bounce(player, target.getEntity());
        bounce(player, bounce, target.getEntity());

        return Response.OK;
    }

    @Nullable
    private LivingEntity bounce(@Nonnull Player player, LivingEntity... bounced) {
        if (bounced == null || bounced.length == 0 || bounced[0] == null) {
            return null;
        }

        final LivingGameEntity bounce = Collect.nearestEntity(
                bounced[0].getLocation(),
                10.0d,
                living -> {
                    for (LivingEntity livingEntity : bounced) {
                        if (livingEntity == living) {
                            return false;
                        }
                    }

                    return living.isNot(player);
                }
        );

        if (bounce != null) {
            final LivingEntity entity = bounce.getEntity();

            execute0(player, bounced[0], entity);
            return entity;
        }

        return null;
    }

    private void execute0(@Nonnull Player player, @Nonnull LivingEntity from, @Nonnull LivingEntity to) {
        final Location location = from.getLocation();

        Geometry.drawLine(
                location.add(0, 1, 0),
                to.getLocation().add(0, 1, 0),
                0.5, new WorldParticle(Particle.SQUID_INK)
        );

        PlayerLib.playSound(location, Sound.ENTITY_GLOW_SQUID_SQUIRT, 1.8f);
        PlayerLib.spawnParticle(location, Particle.SQUID_INK, 1, 0.3d, 0.3d, 0.3, 3f);

        if (to instanceof Player toPlayer) {
            Chat.sendTitle(toPlayer, "&0&l☠", "&0&l☠", 0, blindingDuration - 10, 10);
        }

        to.addPotionEffect(PotionEffectType.BLINDNESS.createEffect(blindingDuration, 10));
        to.addPotionEffect(PotionEffectType.SLOW.createEffect(slowingDuration, 1));

        CF.getEntityOptional(to).ifPresent(entity -> {
            entity.damage(damage, player, EnumDamageCause.DARKNESS_CURSE);
        });

        Chat.sendMessage(to, "&c%s has cursed you with the Dark Magic!", player.getName());
        Chat.sendMessage(player, "&aYou have cursed %s with Dark Magic!", to.getName());
    }

}
