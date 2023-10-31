package me.hapyl.fight.game.heroes.archive.orc;

import me.hapyl.fight.CF;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.talents.Cooldown;
import me.hapyl.fight.game.weapons.RightClickable;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.game.weapons.ability.Ability;
import me.hapyl.fight.game.weapons.ability.AbilityType;
import me.hapyl.fight.util.ItemStacks;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;

public class OrcWeapon extends Weapon implements Cooldown, RightClickable {

    private final PlayerMap<OrcWeaponEntity> thrownAxe;

    public OrcWeapon() {
        super(Material.IRON_AXE);

        thrownAxe = PlayerMap.newMap();

        setName("Poleaxe");
        setDescription("A sharp poleaxe.");
        setDamage(12.0d);
        setAttackSpeed(-2.5d);
        setId("orc_axe");

        setAbility(AbilityType.RIGHT_CLICK, Ability.of("Throw", """
                Throw your poleaxe at your enemies!
                                
                Upon hitting an enemy, it drastically slows them and deals damage before returning to you.
                                
                Upon hitting a block, stay in a block for a while before returning to you.
                """, this));
    }

    @Override
    public void onRightClick(@Nonnull GamePlayer player, @Nonnull ItemStack item) {
        final Location location = player.getLocation();
        final Weapon weapon = Heroes.ORC.getHero().getWeapon();

        if (thrownAxe.containsKey(player) || player.hasCooldown(weapon.getMaterial())) {
            return;
        }

        player.getInventory().setItem(0, ItemStacks.AIR);

        final OrcWeaponEntity entity = new OrcWeaponEntity(player) {

            @Override
            public void onHit(@Nonnull LivingEntity entity) {
                CF.getEntityOptional(entity).ifPresent(gameEntity -> {
                    gameEntity.damage(16.0d, player, EnumDamageCause.ORC_WEAPON);
                    gameEntity.addPotionEffect(PotionEffectType.SLOW.createEffect(100, 4));
                    gameEntity.setFreezeTicks(100);
                });
            }

            @Override
            public void onReturn(@Nonnull GamePlayer player) {
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

    public void remove(GamePlayer player) {
        final OrcWeaponEntity weapon = thrownAxe.remove(player);

        if (weapon != null) {
            weapon.remove();
        }
    }

    public void removeAll() {
        thrownAxe.values().forEach(OrcWeaponEntity::remove);
        thrownAxe.clear();
    }
}
