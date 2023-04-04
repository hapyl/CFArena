package me.hapyl.fight.game.talents.storage.tamer;

import me.hapyl.fight.game.AbstractGamePlayer;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Nulls;
import me.hapyl.fight.util.Utils;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.math.Geometry;
import me.hapyl.spigotutils.module.math.geometry.Quality;
import me.hapyl.spigotutils.module.math.geometry.WorldParticle;
import me.hapyl.spigotutils.module.player.EffectType;
import me.hapyl.spigotutils.module.player.PlayerLib;
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
        public void spawnEntity(Player player, Location location, TamerPack pack) {
            pack.createEntity(location, Entities.ZOMBIE, Ageable::setAdult/*Make sure the zombie is adult*/);
        }

        @Override
        public void onUltimate(Player player, TamerPack pack) {
            Chat.sendMessage(
                    player,
                    "&2Unleash the horde! Your zombies swarm forward, tearing through everything in their path. No one can stop the undead army now!"
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

        @Override
        public void onUltimateEnd(Player player, TamerPack pack) {
            //pack.remove(); impl in Tamer#onUltimateEnd
        }
    }),

    SKELETON_GANG(new Pack("Skeleton Gang") {

        // FIXME (hapyl): 001, Apr 1, 2023: Use Weapon
        private final ItemStack boneSword = new ItemBuilder(Material.IRON_SWORD)
                .addEnchant(Enchantment.FIRE_ASPECT, 1)
                .setUnbreakable()
                .build();

        @Override
        public void spawnEntity(Player player, Location location, TamerPack pack) {
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
        public void onUltimate(Player player, TamerPack pack) {
            Chat.sendMessage(
                    player,
                    "&7The army of bones is at your command! Your skeletons march forward, raining arrows down on your enemies. No one can escape their deadly aim!"
            );
            final PlayerInventory inventory = player.getInventory();

            inventory.setItem(4, boneSword);
            inventory.setHeldItemSlot(4);

            PlayerLib.addEffect(player, PotionEffectType.SPEED, Heroes.TAMER.getHero().getUltimateDuration(), 1);
            GameTask.runDuration(Heroes.TAMER.getHero().getUltimate(), (task, i) -> {
                final Location location = player.getLocation();

                Utils.getPlayersInRange(location, 2).forEach(target -> {
                    if (target != player) {
                        GamePlayer.damageEntity(target, 2, player, EnumDamageCause.AURA_OF_CIRCUS);
                    }
                });
                Geometry.drawCircle(location.add(0d, 1d, 0d), 2d, Quality.NORMAL, new WorldParticle(Particle.CRIT));

                if (!GamePlayer.getPlayer(player).isAlive()) {
                    task.cancel();
                }
            }, 0, 1);
        }

        @Override
        public void onUltimateEnd(Player player, TamerPack pack) {
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
        public void spawnEntity(Player player, Location location, TamerPack pack) {
            pack.createEntity(location, Entities.ZOMBIFIED_PIGLIN, me -> {
                Nulls.runIfNotNull(me.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED), at -> at.setBaseValue(0.5d));
                me.setAngry(true);
                me.setAnger(Short.MAX_VALUE);
            });
        }

        @Override
        public void onUltimate(Player player, TamerPack pack) {
            Chat.sendMessage(player, "&cSummon a pigman to crush your enemies!");
            final PlayerInventory inventory = player.getInventory();

            inventory.setItem(4, goldenBow);
            inventory.setItem(9, new ItemStack(Material.ARROW));
            inventory.setHeldItemSlot(4);

            PlayerLib.addEffect(player, EffectType.STRENGTH, Heroes.TAMER.getHero().getUltimateDuration(), 0);
            PlayerLib.addEffect(player, EffectType.SPEED, Heroes.TAMER.getHero().getUltimateDuration(), 0);
            PlayerLib.addEffect(player, EffectType.FIRE_RESISTANCE, Heroes.TAMER.getHero().getUltimateDuration(), 4);
        }

        @Override
        public void onUltimateEnd(Player player, TamerPack pack) {
            final PlayerInventory inventory = player.getInventory();

            inventory.setItem(4, new ItemStack(Material.AIR));
            inventory.setItem(9, new ItemStack(Material.AIR));
            inventory.setHeldItemSlot(0);
            //pack.remove(); impl in Tamer#onUltimateEnd
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

        @Override
        public void onUltimate(Player player, TamerPack pack) {
            PlayerLib.addEffect(player, EffectType.STRENGTH, Heroes.TAMER.getHero().getUltimateDuration(), 0);
            PlayerLib.addEffect(player, EffectType.SPEED, Heroes.TAMER.getHero().getUltimateDuration(), 2);
            GameTask.runDuration(Heroes.TAMER.getHero().getUltimate(), (task, i) -> {
                final AbstractGamePlayer gp = GamePlayer.getPlayer(player);
                gp.heal(2d);
                if (/*i == 0 || redundant tick check */!gp.isAlive()) {
                    task.cancel();
                }
            }, 0, 20);
        }

        @Override
        public void onUltimateEnd(Player player, TamerPack pack) {
            //pack.remove(); impl in Tamer#onUltimateEnd
        }
    }),

    LASER_ZOMBIE(new Pack("Laser Zombie") {
        @Override
        public void spawnEntity(Player player, Location location, TamerPack pack) {
            final Zombie zombie = pack.createEntity(location, Entities.ZOMBIE, Ageable::setAdult /*Make sure zombie is always adult*/);

            final Guardian guardian = pack.createEntity(location, Entities.GUARDIAN, self -> {
                self.setAI(false);
            });

            zombie.addPassenger(guardian);
        }

        @Override
        public void onUltimate(Player player, TamerPack pack) {
            Chat.sendMessage(
                    player,
                    "&bUnleash the power of the ocean! Your guardian laser fires with deadly precision, obliterating anything in its path. Beware the wrath of the deep!"
            );
            //pack.remove(); impl in Tamer#onUltimateEnd
            final Guardian guardian = pack.createEntity(player.getLocation(), Entities.GUARDIAN, self -> {
                self.setAI(false);
            });
            player.addPassenger(guardian);
        }

        @Override
        public void onUltimateEnd(Player player, TamerPack pack) {
            //pack.remove(); impl in Tamer#onUltimateEnd
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
