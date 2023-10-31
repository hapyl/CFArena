package me.hapyl.fight.game.talents.archive.tamer;

import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.Nulls;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.math.Geometry;
import me.hapyl.spigotutils.module.math.geometry.Quality;
import me.hapyl.spigotutils.module.math.geometry.WorldParticle;
import me.hapyl.spigotutils.module.player.EffectType;
import me.hapyl.spigotutils.module.util.CollectionUtils;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public enum TamerPacks {

    ZOMBIE_HORDE(new Pack("Zombie Horde", "This is shown in the tooltip!", "This is shown in the ultimate description!") {

        @Override
        public int spawnAmount() {
            return 3;
        }

        @Override
        public void spawnEntity(GamePlayer player, Location location, TamerPack pack) {
            pack.createEntity(location, Entities.ZOMBIE, Ageable::setAdult/*Make sure the zombie is adult*/);
        }

        @Override
        public void onUltimate(GamePlayer player, TamerPack pack) {
            player.sendMessage(
                    "&2Unleash the horde! Your zombies swarm forward, tearing through everything in their string. No one can stop the undead army now!"
            );
            pack.removeAll();
            for (int i = 0; i < 9; i++) {
                pack.createEntity(pack.addRelativeOffset(player.getLocation(), i),
                        Entities.ZOMBIE, self -> {
                            self.setBaby();
                            self.addPotionEffect(PotionEffectType.SPEED.createEffect(Integer.MAX_VALUE, 0));
                        }
                );
            }
        }

    }),

    SKELETON_GANG(new Pack("Skeleton Gang") {

        // FIXME (hapyl): 001, Apr 1, 2023: Use Weapon
        private final ItemStack boneSword = new ItemBuilder(Material.IRON_SWORD)
                .addEnchant(Enchantment.FIRE_ASPECT, 1)
                .setUnbreakable()
                .build();

        @Override
        public void spawnEntity(GamePlayer player, Location location, TamerPack pack) {
            pack.createEntity(
                    location,
                    Entities.WITHER_SKELETON,
                    self -> {
                        Nulls.runIfNotNull(self.getEquipment(), equipment -> {
                            equipment.setItemInMainHand(new ItemBuilder(Material.BOW)
                                    .addEnchant(Enchantment.ARROW_DAMAGE, 1)
                                    .addEnchant(Enchantment.ARROW_KNOCKBACK, 1)
                                    .setUnbreakable()
                                    .build());
                        });
                    }
            );
        }

        @Override
        public void onUltimate(GamePlayer player, TamerPack pack) {
            player.sendMessage(
                    "&7The army of bones is at your command! Your skeletons march forward, raining arrows down on your enemies. No one can escape their deadly aim!"
            );
            final PlayerInventory inventory = player.getInventory();

            inventory.setItem(4, boneSword);
            inventory.setHeldItemSlot(4);

            player.addPotionEffect(PotionEffectType.SPEED, Heroes.TAMER.getHero().getUltimateDuration(), 1);

            GameTask.runDuration(Heroes.TAMER.getHero().getUltimate(), (task, i) -> {
                final Location location = player.getLocation();

                Collect.nearbyPlayers(location, 2.0d).forEach(target -> {
                    if (!target.equals(player)) {
                        target.damage(2.0d, player, EnumDamageCause.AURA_OF_CIRCUS);
                    }
                });
                Geometry.drawCircle(location.add(0d, 1d, 0d), 2d, Quality.NORMAL, new WorldParticle(Particle.CRIT));

                if (!player.isAlive()) {
                    task.cancel();
                }
            }, 0, 1);
        }

        @Override
        public void onUltimateEnd(GamePlayer player, TamerPack pack) {
            final PlayerInventory inventory = player.getInventory();
            inventory.setItem(4, new ItemStack(Material.AIR));
            inventory.setHeldItemSlot(0);
            //pack.remove(); impl in Tamer#onUltimateEnd
        }

        @Override
        public int spawnAmount() {
            return 3;
        }
    }),

    PIGLIN_BROTHERS(new Pack("Pigman") {

        // FIXME: 001, Apr 1, 2023 -> Use Weapon
        private final ItemStack goldenBow = new ItemBuilder(Material.BOW)
                .addEnchant(Enchantment.ARROW_INFINITE, 1)
                .setUnbreakable()
                .build();

        @Override
        public int spawnAmount() {
            return 1;
        }

        @Override
        public void spawnEntity(GamePlayer player, Location location, TamerPack pack) {
            pack.createEntity(location, Entities.ZOMBIFIED_PIGLIN, me -> {
                Nulls.runIfNotNull(me.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED), at -> at.setBaseValue(0.5d));
                me.setAngry(true);
                me.setAnger(Short.MAX_VALUE);
            });
        }

        @Override
        public void onUltimate(GamePlayer player, TamerPack pack) {
            player.sendMessage("&cSummon a pigman to crush your enemies!");
            final PlayerInventory inventory = player.getInventory();

            inventory.setItem(4, goldenBow);
            inventory.setItem(9, new ItemStack(Material.ARROW));
            inventory.setHeldItemSlot(4);

            player.addPotionEffect(EffectType.STRENGTH, Heroes.TAMER.getHero().getUltimateDuration(), 0);
            player.addPotionEffect(EffectType.SPEED, Heroes.TAMER.getHero().getUltimateDuration(), 0);
            player.addPotionEffect(EffectType.FIRE_RESISTANCE, Heroes.TAMER.getHero().getUltimateDuration(), 4);
        }

        @Override
        public void onUltimateEnd(GamePlayer player, TamerPack pack) {
            final PlayerInventory inventory = player.getInventory();

            inventory.setItem(4, new ItemStack(Material.AIR));
            inventory.setItem(9, new ItemStack(Material.AIR));
            inventory.setHeldItemSlot(0);
            //pack.remove(); impl in Tamer#onUltimateEnd
        }
    }),

    WOLVES(new Pack("Wolfies") {

        @Override
        public void spawnEntity(GamePlayer player, Location location, TamerPack pack) {
            pack.createEntity(location, Entities.WOLF, me -> {
                me.setOwner(player.getPlayer());
                me.setAngry(true);
                me.setCollarColor(DyeColor.CYAN);
                me.setAdult();
            });
        }

        @Override
        public int spawnAmount() {
            return 4;
        }

        @Override
        public void onUltimate(GamePlayer player, TamerPack pack) {
            player.addPotionEffect(EffectType.STRENGTH, Heroes.TAMER.getHero().getUltimateDuration(), 0);
            player.addPotionEffect(EffectType.SPEED, Heroes.TAMER.getHero().getUltimateDuration(), 2);

            GameTask.runDuration(Heroes.TAMER.getHero().getUltimate(), (task, i) -> {
                player.heal(2d);
                if (/*i == 0 || redundant tick check */!player.isAlive()) {
                    task.cancel();
                }
            }, 0, 20);
        }

    }),

    LASER_ZOMBIE(new Pack("Laser Zombie") {
        @Override
        public void spawnEntity(GamePlayer player, Location location, TamerPack pack) {
            final Zombie zombie = pack.createEntity(location, Entities.ZOMBIE, Ageable::setAdult /*Make sure zombie is always adult*/);

            final Guardian guardian = pack.createEntity(location, Entities.GUARDIAN, self -> {
                self.setAI(false);
            });

            zombie.addPassenger(guardian);
        }

        @Override
        public void onUltimate(GamePlayer player, TamerPack pack) {
            player.sendMessage(
                    "&bUnleash the power of the ocean! Your guardian laser fires with deadly precision, obliterating anything in its string. Beware the wrath of the deep!"
            );

            final Guardian guardian = pack.createEntity(player.getLocation(), Entities.GUARDIAN, self -> {
                self.setAI(false);
            });
            player.addPassenger(guardian);
        }

        @Override
        public void onTick(GamePlayer player, TamerPack pack) {
            final LivingEntity entity = pack.getEntity(EntityType.GUARDIAN);

            if (!(entity instanceof Guardian guardian)) {
                return;
            }

            final LivingGameEntity target = Collect.nearestEntity(guardian.getLocation(), 10.0d, living ->
                    !living.equals(player) && !pack.isInPack(living.getEntity()) && living.hasLineOfSight(guardian));

            if (target == null) {
                return;
            }

            guardian.setTarget(target.getEntity());
            guardian.setLaser(true);

            target.damage(1.0d, player, EnumDamageCause.MINION);
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

    public static TamerPack newRandom(GamePlayer player) {
        final TamerPacks randomPack = CollectionUtils.randomElement(values(), ZOMBIE_HORDE);
        return new TamerPack(randomPack.pack, player);
    }

}
