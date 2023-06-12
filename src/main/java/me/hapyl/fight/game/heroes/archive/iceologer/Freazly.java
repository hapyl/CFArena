package me.hapyl.fight.game.heroes.archive.iceologer;

import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.damage.EntityData;
import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.fight.game.heroes.DisabledHero;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.HeroEquipment;
import me.hapyl.fight.game.heroes.Role;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.task.RaycastTask;
import me.hapyl.fight.game.weapons.RangeWeapon;
import me.hapyl.fight.util.Collect;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class Freazly extends Hero implements DisabledHero {

    /**
     * <h1>REDESIGNED</h1>
     *
     * <b>Weapon</b>
     * Frozen Staff: A slow firing and slow projectile weapon that slows enemies upon hit.
     */

    public Freazly() {
        super("Frostbite");

        setRole(Role.RANGE);

        setInfo("");
        setItem("cad7486b5d20823d5c24cba1850a600a7744209899828b19ccf93f69f2187058");

        final HeroEquipment equipment = getEquipment();
        equipment.setChestplate(Color.fromRGB(139, 169, 214));
        equipment.setLeggings(Color.fromRGB(116, 141, 179));
        equipment.setBoots(Color.fromRGB(45, 54, 69));

        setWeapon(new RangeWeapon(Material.IRON_SHOVEL, "FrostStaff") {
            @Override
            public void onShoot(Player player) {
                final Location location = player.getLocation().add(0.0d, player.getEyeHeight(), 0.0d);

                // Fx
                PlayerLib.playSound(location, Sound.BLOCK_GLASS_BREAK, 1.5f);

                new RaycastTask(location) {

                    @Override
                    public boolean predicate(@Nonnull Location location) {
                        final Block block = location.getBlock();
                        final Material type = block.getType();

                        // If occluding, check for ICE, since it's passable
                        if (type.isOccluding()) {
                            return type == Material.ICE || type == Material.PACKED_ICE || type == Material.FROSTED_ICE;
                        }

                        return true;
                    }

                    public void onHit(LivingEntity entity) {
                        EntityData.damage(entity, 7.5d, player, EnumDamageCause.FROSTBITE);
                        EntityData.of(entity).addEffect(GameEffectType.SLOWING_AURA, 20, true);
                    }

                    @Override
                    public boolean step(@Nonnull Location location) {
                        PlayerLib.spawnParticle(location, Particle.SNOWFLAKE, 1);
                        PlayerLib.spawnParticle(location, Particle.SNOWBALL, 1, 0, 0, 0, 0.025f);

                        // Damage detection
                        final LivingEntity collision = Collect.nearestLivingEntity(location, 0.75d, entity -> entity != player);
                        if (collision != null) {
                            onHit(collision);
                            return true;
                        }

                        return false;
                    }
                }.setStep(0.5d).setIterations(3).setMax(25).runTaskTimer(0, 1);
            }

            @Override
            public double getMaxDistance(Player player) {
                return 0;
            }

            @Override
            public int getWeaponCooldown(Player player) {
                return 40;
            }
        }.setId("FrostStaff"));

    }

    @Override
    public String predicateMessage(Player player) {
        return "No valid block in sight!";
    }

    @Override
    public void useUltimate(Player player) {

    }

    @Override
    public Talent getFirstTalent() {
        return Talents.ICE_CONE.getTalent();
    }

    @Override
    public Talent getSecondTalent() {
        return Talents.ICE_BARRIER.getTalent();
    }

    @Override
    public Talent getPassiveTalent() {
        return null;
    }

}
