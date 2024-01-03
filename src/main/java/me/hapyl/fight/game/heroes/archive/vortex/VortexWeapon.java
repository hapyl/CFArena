package me.hapyl.fight.game.heroes.archive.vortex;

import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.HeroReference;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.archive.vortex.AstralStar;
import me.hapyl.fight.game.talents.archive.vortex.AstralStars;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.game.weapons.ability.Ability;
import me.hapyl.fight.game.weapons.ability.AbilityType;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class VortexWeapon extends Weapon implements HeroReference<Vortex> {

    @DisplayField private final double explosionDistance = 5.0d;
    @DisplayField private final double explosionDamage = 10.0d;

    private final Vortex hero;

    public VortexWeapon(Vortex vortex) {
        super(Material.STONE_SWORD);

        this.hero = vortex;

        setName("Sword of Thousands Stars");
        setDescription("A sword with an astral link to the stars.");
        setId("sots_weapon");
        setDamage(6.5d);

        setAbility(AbilityType.RIGHT_CLICK, new SotsAbility());
    }

    @Nonnull
    @Override
    public Vortex getHero() {
        return hero;
    }

    public class SotsAbility extends Ability {
        public SotsAbility() {
            super(
                    "Astral Despair", """
                            Focus on the target &eAstral Star&7 and explode it, dealing &cAoE damage&7.
                            """
            );

            setCooldownSec(6);
        }

        @Nullable
        @Override
        public Response execute(@Nonnull GamePlayer player, @Nonnull ItemStack item) {
            final AstralStars stars = hero.getSecondTalent().getStars(player);
            final AstralStar targetStar = stars.getTargetStar();

            if (targetStar == null) {
                return Response.error("Not targeting a star!");
            }

            final Location location = targetStar.getLocation();

            stars.removeStar(targetStar);
            Collect.nearbyEntities(location, explosionDistance).forEach(entity -> {
                entity.damage(explosionDamage, player, EnumDamageCause.SOTS);
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

            return Response.OK;
        }
    }
}
