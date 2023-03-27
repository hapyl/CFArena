package me.hapyl.fight.game.talents.storage.tamer;

import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.util.Nulls;
import me.hapyl.fight.util.Utils;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.util.CollectionUtils;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public enum TamerPacks {

    ZOMBIE_HORDE(new Pack("Zombie Horde") {

        @Override
        public int spawnAmount() {
            return 3;
        }

        @Override
        public void spawnEntity(Player player, Location location, TamerPack pack) {
            pack.createEntity(location, Entities.ZOMBIE);
        }
    }),

    SKELETON_GANG(new Pack("Skeleton Gang") {

        @Override
        public void spawnEntity(Player player, Location location, TamerPack pack) {
            pack.createEntity(
                    location,
                    Entities.SKELETON,
                    self -> Nulls.runIfNotNull(self.getEquipment(), equipment -> {
                        equipment.setItemInMainHand(new ItemBuilder(Material.BOW)
                                .addEnchant(Enchantment.ARROW_DAMAGE, 1)
                                .addEnchant(Enchantment.ARROW_KNOCKBACK, 1)
                                .setUnbreakable()
                                .build());
                    })
            );
        }

        @Override
        public int spawnAmount() {
            return 3;
        }
    }),

    PIGLIN_BROTHERS(new Pack("Piglin Brothers") {

        @Override
        public int spawnAmount() {
            return 1;
        }

        @Override
        public void spawnEntity(Player player, Location location, TamerPack pack) {
            pack.createEntity(location, Entities.PIGLIN, self -> {
                Nulls.runIfNotNull(self.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED), at -> at.setBaseValue(0.5d));
                self.setImmuneToZombification(true);
                self.setIsAbleToHunt(true);
                self.setAdult();
            });

            pack.createEntity(location, Entities.PIGLIN, self -> {
                self.setImmuneToZombification(true);
                self.setIsAbleToHunt(true);
                self.setAdult();

                Nulls.runIfNotNull(self.getEquipment(), equipment -> {
                    equipment.setItemInMainHand(new ItemBuilder(Material.CROSSBOW)
                            .addEnchant(Enchantment.QUICK_CHARGE, 1)
                            .addEnchant(Enchantment.MULTISHOT, 1)
                            .setUnbreakable()
                            .build());
                });
            });
        }
    }),

    WOLVES(new Pack("Wolfies") {

        @Override
        public void spawnEntity(Player player, Location location, TamerPack pack) {
            pack.createEntity(location, Entities.WOLF, me -> {
                me.setOwner(player);
                me.setAngry(true);
                me.setCollarColor(DyeColor.CYAN);
                me.setAdult();
            });
        }

        @Override
        public int spawnAmount() {
            return 4;
        }

    }),

    LASER_ZOMBIE(new Pack("Laser Zombie") {
        @Override
        public void spawnEntity(Player player, Location location, TamerPack pack) {
            final Zombie zombie = pack.createEntity(location, Entities.ZOMBIE, self -> {
            });

            final Guardian guardian = pack.createEntity(location, Entities.GUARDIAN, self -> {
                self.setAI(false);
            });

            zombie.addPassenger(guardian);
        }

        @Override
        public void onTick(Player player, TamerPack pack) {
            final LivingEntity entity = pack.getEntity(EntityType.GUARDIAN);

            if (!(entity instanceof Guardian guardian)) {
                return;
            }

            final LivingEntity target = Utils.getNearestLivingEntity(guardian.getLocation(), 10.0d, living ->
                    living != player && !pack.isInPack(living) && guardian.hasLineOfSight(living));

            if (target == null) {
                return;
            }

            guardian.setTarget(target);
            guardian.setLaser(true);

            GamePlayer.damageEntity(target, 1d, player, EnumDamageCause.MINION);
            target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10, 1));
        }
    }),

    ;

    private final Pack pack;

    TamerPacks(Pack pack) {
        this.pack = pack;
    }

    public Pack getPack() {
        return pack;
    }

    public static TamerPack newRandom(Player player) {
        final TamerPacks randomPack = CollectionUtils.randomElement(values(), ZOMBIE_HORDE);
        return new TamerPack(randomPack.pack, player);
    }

}
