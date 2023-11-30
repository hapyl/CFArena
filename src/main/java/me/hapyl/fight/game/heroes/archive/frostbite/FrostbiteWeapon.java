package me.hapyl.fight.game.heroes.archive.frostbite;

import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.task.RaycastTask;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.game.weapons.ability.Ability;
import me.hapyl.fight.game.weapons.ability.AbilityType;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FrostbiteWeapon extends Weapon {

    @DisplayField private final int slowingAuraDuration = 60;
    @DisplayField private final double weaponDamage = 5.0d;

    public FrostbiteWeapon() {
        super(Material.IRON_SHOVEL);

        setName("Snow Shovel");
        setId("FrostbiteWeapon");

        setDescription("""
                An ordinary shovel used for shoveling the snow.
                """);

        setDamage(1.0d);
        setAbility(AbilityType.RIGHT_CLICK, new FrostbiteAbility());
    }

    public class FrostbiteAbility extends Ability {

        public FrostbiteAbility() {
            super("Shoot!", """
                    Shoot a slow projectile that deals damage.
                    """);

            setCooldown(15);
        }

        @Nullable
        @Override
        public Response execute(@Nonnull GamePlayer player, @Nonnull ItemStack item) {
            final Location location = player.getEyeLocation();
            final FrostbiteBullet bullet = new FrostbiteBullet(player) {
                @Override
                public void onContact(@Nonnull ArmorStand armorStand, @Nonnull LivingGameEntity entity, @Nonnull Location location) {
                    entity.damage(weaponDamage, player, EnumDamageCause.FROSTBITE);
                    entity.addEffect(GameEffectType.SLOWING_AURA, slowingAuraDuration, true);
                    remove();
                }
            };

            new RaycastTask(location) {
                @Override
                public boolean step(@Nonnull Location location) {
                    bullet.teleport(location);

                    PlayerLib.spawnParticle(location, Particle.SNOWFLAKE, 1);
                    PlayerLib.spawnParticle(location, Particle.SNOWBALL, 1, 0.05d, 0.05d, 0.05d, 0.025f);
                    return false;
                }

                @Override
                public boolean predicate(@Nonnull Location location) {
                    final Block block = location.getBlock();
                    final Material type = block.getType();

                    if (type.isOccluding()) {
                        return type == Material.ICE || type == Material.PACKED_ICE || type == Material.BLUE_ICE;
                    }

                    return true;
                }

                @Override
                public void onTaskStop() {
                    bullet.remove();
                }
            }.setMax(25).setIterations(2).runTaskTimer(0, 1);

            return Response.OK;
        }
    }

}
