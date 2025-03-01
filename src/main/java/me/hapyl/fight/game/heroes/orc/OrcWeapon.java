package me.hapyl.fight.game.heroes.orc;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.CF;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.effect.EffectType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.loadout.HotBarSlot;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.game.weapons.ability.Ability;
import me.hapyl.fight.game.weapons.ability.AbilityType;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class OrcWeapon extends Weapon {

    private final PlayerMap<OrcWeaponEntity> thrownAxe;

    @DisplayField private final double damage = 16;

    public OrcWeapon() {
        super(Material.IRON_AXE, Key.ofString("orc_weapon"));

        thrownAxe = PlayerMap.newMap();

        setName("Poleaxe");
        setDescription("A sharp poleaxe.");
        setDamage(8.0d);

        setAbility(AbilityType.RIGHT_CLICK, new Throw());
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

    public class Throw extends Ability {

        public Throw() {
            super("Throw", """
                    Throw your poleaxe at your enemies!
                    
                    Upon hitting an &cenemy&7, it drastically &3slows&7 them and deals &cdamage&7 before returning to you.
                                    
                    Upon hitting a &eblock&7, stay in a block for a while before returning to you.
                    """);

            setCooldownSec(15);
        }

        @Nullable
        @Override
        public Response execute(@Nonnull GamePlayer player, @Nonnull ItemStack item) {
            final Location location = player.getLocation();
            final Weapon weapon = HeroRegistry.ORC.getWeapon();

            if (thrownAxe.containsKey(player) || player.cooldownManager.hasCooldown(weapon)) {
                return null;
            }

            player.setItem(HotBarSlot.WEAPON, null);

            final OrcWeaponEntity entity = new OrcWeaponEntity(player) {

                @Override
                public void onHit(@Nonnull LivingEntity entity) {
                    CF.getEntityOptional(entity).ifPresent(gameEntity -> {
                        gameEntity.damage(damage, player, DamageCause.ORC_WEAPON);
                        gameEntity.addEffect(EffectType.SLOW, 4, 100);
                        gameEntity.setFreezeTicks(100);
                    });
                }

                @Override
                public void onReturn(@Nonnull GamePlayer player) {
                    thrownAxe.remove(player);

                    player.cooldownManager.setCooldown(weapon, getCooldown());
                    player.setItem(HotBarSlot.WEAPON, weapon.getItem());
                }

            };

            thrownAxe.put(player, entity);

            // Fx
            player.playWorldSound(location, Sound.ITEM_TRIDENT_RETURN, 0.0f);

            return Response.OK;
        }
    }
}
