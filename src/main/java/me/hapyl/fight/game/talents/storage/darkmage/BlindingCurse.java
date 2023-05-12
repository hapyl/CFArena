package me.hapyl.fight.game.talents.storage.darkmage;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.storage.extra.DarkMageSpell;
import me.hapyl.fight.util.Utils;
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

    public BlindingCurse() {
        super("Blinding Curse", "Applies blinding curse to target player.", Material.INK_SAC);

        setAssistDescription("The curse bounces to two additional targets.");

        setCd(100);
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
    public Response execute(Player player) {
        final LivingEntity target = Utils.getTargetEntity(
                player,
                maxDistance,
                0.9d,
                living -> living != player && player.hasLineOfSight(living)
        );

        if (target == null) {
            return Response.error("No valid target!");
        }

        execute0(player, player, target);

        // Have to use assist here since it's based on ability casting
        if (!Heroes.DARK_MAGE.getHero().isUsingUltimate(player)) {
            return Response.OK;
        }

        // Bounce
        final LivingEntity bounce = bounce(player, target);
        bounce(player, bounce, target);

        return Response.OK;
    }

    @Nullable
    private LivingEntity bounce(@Nonnull Player player, LivingEntity... bounced) {
        if (bounced == null || bounced.length == 0) {
            return null;
        }

        final LivingEntity bounce = Utils.getNearestLivingEntity(
                bounced[0].getLocation(),
                10.0d,
                living -> {
                    for (LivingEntity livingEntity : bounced) {
                        if (livingEntity == living) {
                            return false;
                        }
                    }

                    return living != player;
                }
        );

        if (bounce != null) {
            execute0(player, bounced[0], bounce);
            return bounce;
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

        to.addPotionEffect(PotionEffectType.BLINDNESS.createEffect(60, 10));
        to.addPotionEffect(PotionEffectType.SLOW.createEffect(40, 1));

        Chat.sendMessage(to, "&c%s has cursed you with the Dark Magic!", player.getName());
        Chat.sendMessage(player, "&aYou have cursed %s with Dark Magic!", to.getName());
    }

}
