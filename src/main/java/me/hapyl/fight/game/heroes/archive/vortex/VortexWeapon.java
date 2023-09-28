package me.hapyl.fight.game.heroes.archive.vortex;

import me.hapyl.fight.CF;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.HeroReference;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.weapons.RightClickable;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.game.weapons.ability.Ability;
import me.hapyl.fight.game.weapons.ability.AbilityType;
import me.hapyl.fight.util.Collect;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public class VortexWeapon extends Weapon implements RightClickable, HeroReference<Vortex> {

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

        setAbility(AbilityType.RIGHT_CLICK, new Ability("Vortex Slash", """
                Launch vortex energy forward that &bfollows your crosshair&7 and rapidly damages and knock enemies back.
                """) {
            @Override
            public Response execute(@Nonnull Player player, @Nonnull ItemStack item) {
                onRightClick(player, item);
                return Response.OK;
            }
        }.setCooldown(hero.sotsCooldown));
    }

    @Override
    public void onRightClick(@Nonnull Player player, @Nonnull ItemStack item) {
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

                Collect.nearbyEntities(nextLocation, 2.0d).forEach(entity -> {
                    if (entity.is(player)) {
                        return;
                    }

                    entity.damageTick(hero.sotsDamage, CF.getPlayer(player), EnumDamageCause.SOTS, 0);
                });

                if (((distanceFlew += distanceShift) >= maxDistance) || nextLocation.getBlock().getType().isOccluding()) {
                    GamePlayer.setCooldown(player, Material.STONE_SWORD, hero.sotsCooldown);
                    cancel();
                }

            }
        }.runTaskTimer(0, 1);

        GamePlayer.setCooldown(player, getMaterial(), hero.sotsCooldown);
    }

    @Nonnull
    @Override
    public Vortex getHero() {
        return hero;
    }
}
