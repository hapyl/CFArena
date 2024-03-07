package me.hapyl.fight.game.talents.archive.vortex;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.archive.vortex.Vortex;
import me.hapyl.fight.game.talents.InputTalent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;

public class StarAligner extends InputTalent {

    @DisplayField private final double implodeDistance = 5.0d;
    @DisplayField private final int implodeDelay = 20;
    @DisplayField private final double damageHealthMultiplier = 2.0d;

    public StarAligner() {
        super("Astral Vision");

        setDescription("Enter astral vision state.");

        leftData.setAction("Implode");
        leftData.setDescription("""
                Implode the &a&ntarget&e Astral Star&7, dealing &cAoE damage&7 based on the stars current &chealth&7.
                                
                &c;;Imploding a star will &nnot&c return the sacrificed ❤!
                """);
        leftData.setType(Type.DAMAGE);
        leftData.setCooldownSec(6);

        rightData.setAction("Link");
        rightData.setDescription("""
                Link with the &a&ntarget&7 &eAstral Star&7, launching yourself towards it.
                                
                While &ntraveling&7, leave an &6Astral Trail&7 that rapidly deals damage.
                &8;;If the star is destroyed while traveling, stop traveling.
                                
                Upon reaching the &eAstral Star&7, collect it, regain the &4sacrificed&c ❤&7 and &aheal&7 for its &nremaining&7 health.
                """);
        rightData.setType(Type.MOVEMENT);
        rightData.setCooldownSec(1);

        setItem(Material.BEETROOT_SEEDS);
    }

    @Nonnull
    @Override
    public Response onEquip(@Nonnull GamePlayer player) {
        player.addPotionEffectIndefinitely(PotionEffectType.SLOW, 3);

        return Response.OK;
    }

    @Override
    public void onUse(@Nonnull GamePlayer player) {
        player.removePotionEffect(PotionEffectType.SLOW);
    }

    @Override
    public void onCancel(@Nonnull GamePlayer player) {
        onUse(player);
    }

    @Nonnull
    @Override
    public Response onLeftClick(@Nonnull GamePlayer player) {
        final AstralStar targetStar = getTargetStar(player);

        if (targetStar == null) {
            return Response.error("Not targeting any astral stars!");
        }

        final Vortex hero = Heroes.VORTEX.getHero(Vortex.class);
        final AstralStarList stars = hero.getFirstTalent().getStars(player);
        final double damage = hero.calculateAstralDamage(player, targetStar.getHealth() * damageHealthMultiplier);
        final Location location = targetStar.getLocation();

        targetStar.setState(StarState.EXPLODING);

        new AstralTask(player, targetStar) {
            @Override
            public void run(@Nonnull GamePlayer player, @Nonnull AstralStar star, int tick) {
                // Explode
                if (tick >= implodeDelay) {
                    Collect.nearbyEntities(location, implodeDistance).forEach(entity -> {
                        if (player.isSelfOrTeammate(entity)) {
                            return;
                        }

                        entity.damageNoKnockback(damage, player, EnumDamageCause.SOTS);
                    });

                    // Fx
                    player.spawnWorldParticle(location, Particle.EXPLOSION_LARGE, 5, 0.5, 0.5, 0.5, 1);
                    player.spawnWorldParticle(location, Particle.CRIT, 10, 0.5d, 0.5d, 0.5d, 0.025f);
                    player.spawnWorldParticle(
                            location,
                            Particle.SWEEP_ATTACK,
                            10,
                            0.5d,
                            0.5d,
                            0.5d,
                            2f/* I think speed is size for SWEEP_ATTACK */
                    );

                    player.playWorldSound(location, Sound.ENTITY_BLAZE_HURT, 0.0f);
                    player.playWorldSound(location, Sound.BLOCK_BELL_USE, 0.75f);

                    stars.removeStar(targetStar);
                    cancel();
                    return;
                }

                // Charge Fx
                player.playWorldSound(location, Sound.BLOCK_NOTE_BLOCK_HAT, 0.75f + (0.5f / implodeDelay * tick));
                player.playWorldSound(location, Sound.ENTITY_ENDERMAN_HURT, 0.75f + (0.5f / implodeDelay * tick));
            }
        };

        return Response.OK;
    }

    @Nonnull
    @Override
    public Response onRightClick(@Nonnull GamePlayer player) {
        final AstralStar targetStar = getTargetStar(player);

        if (targetStar == null) {
            return Response.error("Not targeting any astral stars!");
        }

        final Vortex hero = Heroes.VORTEX.getHero(Vortex.class);

        hero.performStarBlink(player, targetStar);
        hero.addDreamStack(player);

        player.playWorldSound(Sound.ENTITY_ENDERMAN_TELEPORT, 1.75f);
        return Response.OK;
    }

    private AstralStar getTargetStar(GamePlayer player) {
        final AstralStarList stars = Talents.VORTEX_STAR.getTalent(VortexStarTalent.class).getStars(player);

        return stars.getTargetStar();
    }

}
