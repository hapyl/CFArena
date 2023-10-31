package me.hapyl.fight.game.heroes.archive.vortex;

import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Archetype;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.archive.vortex.VortexStar;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.ui.UIComponent;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.Collect;
import me.hapyl.spigotutils.module.math.Geometry;
import me.hapyl.spigotutils.module.math.geometry.Quality;
import me.hapyl.spigotutils.module.math.geometry.WorldParticle;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;

public class Vortex extends Hero implements UIComponent {

    public final double sotsDamage = 1.0d;
    public final double starDamage = 20.0d;
    public final int sotsCooldown = 800;

    public Vortex() {
        super("Vortex");

        setArchetype(Archetype.STRATEGY);

        setDescription("A young boy with the power of speaking to starts...");
        setItem("2adc458dfabc20b8d587b0476280da2fb325fc616a5212784466a78b85fb7e4d");

        final Equipment equipment = this.getEquipment();
        equipment.setChestPlate(102, 51, 0);
        equipment.setLeggings(179, 89, 0);
        equipment.setBoots(255, 140, 26);

        setWeapon(new VortexWeapon(this));

        setUltimate(new UltimateTalent("All the Stars", """
                Instantly create &b10 &7Astral Stars around you.
                                
                Then, rapidly slash between them, dealing the normal star damage.
                After, perform the final blow with &b360° &7attack that slows opponents.
                                
                &6;;This will not affect already placed Astral Stars
                """, 70)
                .setItem(Material.QUARTZ)
                .setCooldownSec(30));
    }

    @Override
    public void onStart(@Nonnull GamePlayer player) {
        startInitWeaponCooldown(player);
    }

    @Override
    public void onRespawn(@Nonnull GamePlayer player) {
        startInitWeaponCooldown(player);
    }

    public void startInitWeaponCooldown(GamePlayer player) {
        player.setCooldown(getWeapon().getMaterial(), sotsCooldown / 2);
    }

    private void performFinalSlash(Location location, GamePlayer player) {
        final World world = location.getWorld();
        if (world == null) {
            return;
        }

        for (double i = 0; i < Math.PI * 2; i += Math.PI / 8) {
            double x = (5.5 * Math.sin(i));
            double z = (5.5 * Math.cos(i));
            location.add(x, 0, z);

            // Damage
            Collect.nearbyEntities(location, 2.0d).forEach(entity -> {
                if (entity.equals(player)) {
                    return;
                }

                entity.damage(1.0d, player, EnumDamageCause.ENTITY_ATTACK);
                entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 80, 2));
            });

            // Fx
            world.spawnParticle(Particle.SWEEP_ATTACK, location, 1, 0, 0, 0, 0);
            world.playSound(location, Sound.ITEM_FLINTANDSTEEL_USE, 10, 0.75f);

            location.subtract(x, 0, z);
        }

    }

    @Override
    public void useUltimate(@Nonnull GamePlayer player) {
        final double spreadDistance = 5.5d;
        final double halfSpreadDistance = spreadDistance / 2.0d;
        final Location location = player.getLocation();
        final Location[] allTheStars = {
                //up
                location.clone().add(0, spreadDistance, 0),
                //vert
                location.clone().add(spreadDistance, 0, 0),
                location.clone().add(-spreadDistance, 0, 0),
                location.clone().add(0, 0, spreadDistance),
                location.clone().add(0, 0, -spreadDistance),
                //cor
                location.clone().add(halfSpreadDistance, halfSpreadDistance, halfSpreadDistance),
                location.clone().add(-halfSpreadDistance, halfSpreadDistance, -halfSpreadDistance),
                location.clone().add(-halfSpreadDistance, halfSpreadDistance, halfSpreadDistance),
                location.clone().add(halfSpreadDistance, halfSpreadDistance, -halfSpreadDistance),
                //final
                location.clone()
        };

        new GameTask() {
            private int tick = 0;
            private int pos = 0;

            @Override
            public void run() {
                // draw circle
                if (tick % 10 == 0) {
                    Geometry.drawCircle(location, spreadDistance, Quality.NORMAL, new WorldParticle(Particle.FIREWORKS_SPARK));
                }
                if (tick++ % 5 == 0) {
                    // final slash
                    if (pos >= (allTheStars.length - 1)) {
                        performFinalSlash(location.add(0.0d, 0.5d, 0.0d), player);
                        cancel();
                        return;
                    }
                    performStarSlash(allTheStars[pos], allTheStars[pos + 1], player);
                    ++pos;
                }
            }
        }.runTaskTimer(0, 1);
    }

    public void performStarSlash(Location start, Location finish, GamePlayer player) {
        // ray-trace string
        CFUtils.rayTracePath(start, finish, 1.0d, 2.0d, living -> {
            if (living.equals(player)) {
                return;
            }

            living.damage(starDamage, player, EnumDamageCause.STAR_SLASH);
        }, loc -> {
            PlayerLib.spawnParticle(loc, Particle.SWEEP_ATTACK, 1, 0, 0, 0, 0);
            PlayerLib.playSound(loc, Sound.ITEM_FLINTANDSTEEL_USE, 0.75f);
        });
    }


    @Override
    public VortexStar getFirstTalent() {
        return (VortexStar) Talents.VORTEX_STAR.getTalent();
    }

    @Override
    public Talent getSecondTalent() {
        return Talents.STAR_ALIGNER.getTalent();
    }

    @Override
    public Talent getPassiveTalent() {
        return Talents.EYES_OF_THE_GALAXY.getTalent();
    }

    @Override
    public @Nonnull String getString(@Nonnull GamePlayer player) {
        return "&6⭐ &l" + getFirstTalent().getStarAmount(player);
    }
}
