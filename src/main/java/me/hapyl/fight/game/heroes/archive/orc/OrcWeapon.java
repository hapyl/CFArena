package me.hapyl.fight.game.heroes.archive.orc;

import com.google.common.collect.Maps;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.damage.EntityData;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.talents.Cooldown;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.util.ItemStacks;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import java.util.Map;

public class OrcWeapon extends Weapon implements Cooldown {

    private final Map<Player, OrcWeaponEntity> thrownAxe;

    public OrcWeapon() {
        super(Material.IRON_AXE);

        thrownAxe = Maps.newHashMap();

        setDamage(10.0d);
        setAttackSpeed(-2.5d);
        setId("orc_axe");
        setName("Poleaxe");
    }

    @Override
    public void onRightClick(Player player, ItemStack item) {
        final Location location = player.getLocation();
        final Weapon weapon = Heroes.ORC.getHero().getWeapon();

        if (thrownAxe.containsKey(player) || player.hasCooldown(weapon.getMaterial())) {
            return;
        }

        player.getInventory().setItem(0, ItemStacks.AIR);

        final OrcWeaponEntity entity = new OrcWeaponEntity(player) {

            @Override
            public void onHit(@Nonnull LivingEntity entity) {
                EntityData.damage(entity, 16.0d, player, EnumDamageCause.ORC_WEAPON);

                entity.addPotionEffect(PotionEffectType.SLOW.createEffect(100, 4));
                entity.setFreezeTicks(100);
            }

            @Override
            public void onReturn(@Nonnull Player player) {
                thrownAxe.remove(player);

                player.setCooldown(weapon.getMaterial(), getCooldown());
                player.getInventory().setItem(0, weapon.getItem());
            }

        };

        thrownAxe.put(player, entity);

        // Fx
        PlayerLib.playSound(location, Sound.ITEM_TRIDENT_RETURN, 0.0f);
    }

    @Override
    public int getCooldown() {
        return 15 * 20;
    }

    @Override
    public Cooldown setCooldown(int cooldown) {
        return this;
    }

    public void remove(Player player) {
        final OrcWeaponEntity weapon = thrownAxe.remove(player);

        if (weapon == null) {
            return;
        }

        weapon.remove();
    }
}
