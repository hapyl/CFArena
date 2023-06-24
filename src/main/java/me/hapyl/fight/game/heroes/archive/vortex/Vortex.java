package me.hapyl.fight.game.heroes.archive.vortex;

import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.HeroEquipment;
import me.hapyl.fight.game.heroes.Role;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.archive.vortex.VortexStar;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.ui.UIComponent;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.Utils;
import me.hapyl.spigotutils.module.math.Geometry;
import me.hapyl.spigotutils.module.math.geometry.Quality;
import me.hapyl.spigotutils.module.math.geometry.WorldParticle;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;

public class Vortex extends Hero implements UIComponent {

    private final double sotsDamage = 1.0d;
    private final double starDamage = 20.0d;
    private final int sotsCooldown = 800;

    public Vortex() {
        super("Vortex");

        setRole(Role.STRATEGIST);

        setDescription("A young boy with power of speaking to starts...");
        setItem("2adc458dfabc20b8d587b0476280da2fb325fc616a5212784466a78b85fb7e4d");

        final HeroEquipment equipment = this.getEquipment();
        equipment.setChestplate(102, 51, 0);
        equipment.setLeggings(179, 89, 0);
        equipment.setBoots(255, 140, 26);

        setWeapon(new Weapon(Material.STONE_SWORD) {

            @Override
            public void onRightClick(Player player, ItemStack item) {
                if (player.hasCooldown(this.getMaterial())) {
                    return;
                }

                final Location location = player.getEyeLocation();

                new GameTask() {
                    private final double distanceShift = 0.5d;
                    private final double maxDistance = 100;
                    private double distanceFlew = 0.0d;

                    @Override
                    public void run() {

                        final Location nextLocation = location.add(player.getEyeLocation().getDirection().multiply(distanceShift));
                        PlayerLib.spawnParticle(nextLocation, Particle.SWEEP_ATTACK, 1, 0, 0, 0, 0);

                        if ((distanceFlew % 5) == 0) {
                            PlayerLib.playSound(nextLocation, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.25f);
                        }

                        Collect.nearbyLivingEntities(nextLocation, 2.0d).forEach(entity -> {
                            if (entity == player) {
                                return;
                            }

                            GamePlayer.damageEntityTick(entity, sotsDamage, player, EnumDamageCause.SOTS, 0);
                        });

                        if (((distanceFlew += distanceShift) >= maxDistance) || nextLocation.getBlock().getType().isOccluding()) {
                            GamePlayer.setCooldown(player, Material.STONE_SWORD, sotsCooldown);
                            cancel();
                        }

                    }
                }.runTaskTimer(0, 1);

                GamePlayer.setCooldown(player, getMaterial(), sotsCooldown);
            }
        }.setName("Sword of Thousands Stars")
                .setId("sots_weapon")
                .setDescription(String.format(
                        "A sword with the ability to summon thousands of stars.____&e&lRIGHT CLICK &7to launch vortex energy in front of you, that follows your crosshair and rapidly damages and knocks enemies back upon hit.____&aCooldown: &l%ss",
                        BukkitUtils.roundTick(sotsCooldown)
                )).setDamage(6.5d));


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
    public void onStart(Player player) {
        startInitWeaponCooldown(player);
    }

    @Override
    public void onRespawn(Player player) {
        startInitWeaponCooldown(player);
    }

    public void startInitWeaponCooldown(Player player) {
        GamePlayer.setCooldown(player, getWeapon().getMaterial(), sotsCooldown / 2);
    }

    private void performFinalSlash(Location location, Player player) {
        final World world = location.getWorld();
        if (world == null) {
            return;
        }

        for (double i = 0; i < Math.PI * 2; i += Math.PI / 8) {
            double x = (5.5 * Math.sin(i));
            double z = (5.5 * Math.cos(i));
            location.add(x, 0, z);

            // fx
            world.spawnParticle(Particle.SWEEP_ATTACK, location, 1, 0, 0, 0, 0);
            world.playSound(location, Sound.ITEM_FLINTANDSTEEL_USE, 10, 0.75f);

            // damage
            Collect.nearbyLivingEntities(location, 2.0d).forEach(entity -> {
                if (player == entity) {
                    return;
                }
                entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 80, 2));
                GamePlayer.damageEntity(entity, 1.0d, player, EnumDamageCause.ENTITY_ATTACK);
            });

            location.subtract(x, 0, z);
        }

    }

    @Override
    public void useUltimate(Player player) {
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

    public void performStarSlash(Location start, Location finish, Player player) {
        // ray-trace string
        Utils.rayTracePath(start, finish, 1.0d, 2.0d, living -> {
            if (living == player) {
                return;
            }

            GamePlayer.damageEntity(living, starDamage, player, EnumDamageCause.STAR_SLASH);
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
    public @Nonnull String getString(Player player) {
        return "&6⭐ &l" + getFirstTalent().getStarsAmount(player);
    }
}
