package me.hapyl.fight.game.heroes.archive.vortex;

import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.HeroReference;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.game.weapons.ability.Ability;
import me.hapyl.fight.game.weapons.ability.AbilityType;
import me.hapyl.fight.util.Collect;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class VortexWeapon extends Weapon implements HeroReference<Vortex> {

    private final Vortex hero;

    public VortexWeapon(Vortex vortex) {
        super(Material.STONE_SWORD);

        this.hero = vortex;

        setName("Sword of Thousands Stars");
        setId("sots_weapon");
        setDescription("""
                A sword with the ability to summon thousands of stars.
                """);
        setDamage(6.5d);

        setAbility(AbilityType.RIGHT_CLICK, new VortexSlash());
    }

    public class VortexSlash extends Ability {
        public VortexSlash() {
            super(
                    "Vortex Slash",
                    "Launch vortex energy forward that &bfollows your crosshair&7 and rapidly damages and knock enemies back."
            );
            setCooldown(hero.sotsCooldown);
        }

        @Nullable
        @Override
        public Response execute(@Nonnull GamePlayer player, @Nonnull ItemStack item) {
            final Location location = player.getEyeLocation();
            startCooldown(player, 100000);

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

                    Collect.nearbyEntities(nextLocation, 2.0d).forEach(entity -> {
                        if (entity.equals(player)) {
                            return;
                        }

                        entity.damageTick(hero.sotsDamage, player, EnumDamageCause.SOTS, 0);
                    });

                    if (((distanceFlew += distanceShift) >= maxDistance) || nextLocation.getBlock().getType().isOccluding()) {
                        startCooldown(player);
                        cancel();
                    }

                }
            }.runTaskTimer(0, 1);

            return Response.AWAIT;
        }
    }

    @Nonnull
    @Override
    public Vortex getHero() {
        return hero;
    }
}
