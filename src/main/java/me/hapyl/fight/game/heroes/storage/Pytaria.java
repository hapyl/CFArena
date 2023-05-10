package me.hapyl.fight.game.heroes.storage;

import me.hapyl.fight.event.DamageInput;
import me.hapyl.fight.event.DamageOutput;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.IGamePlayer;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.HeroEquipment;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.Role;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.util.Utils;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import me.hapyl.spigotutils.module.util.CollectionUtils;
import org.bukkit.*;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

public class Pytaria extends Hero {

    private final int healthRegenPercent = 20;

    public Pytaria() {
        super("Pytaria");

        setRole(Role.ASSASSIN);

        setInfo(
                "Beautiful, but deadly opponent with addiction to flowers. She suffered all her youth, which at the end, made her only stronger."
        );
        setItem("7bb0752f9fa87a693c2d0d9f29549375feb6f76952da90d68820e7900083f801");

        setWeapon(new Weapon(Material.ALLIUM).setName("Annihilallium")
                .setDamage(8.0)
                .setDescription("A beautiful flower, nothing more."));

        final HeroEquipment equipment = getEquipment();
        equipment.setChestplate(222, 75, 85);
        equipment.setLeggings(54, 158, 110);
        equipment.setBoots(179, 204, 204);

        setUltimate(new UltimateTalent(
                "Feel the Breeze",
                "Summon a blooming Bee in front of Pytaria.____The Bee will lock on a closest enemy and charge.____Once charged, unleashes damage in small AoE and regenerates %s%% &7of Pytaria's missing health.".formatted(
                        healthRegenPercent),
                60
        ).setCdSec(50)
                .setDuration(60)
                .setSound(Sound.ENTITY_BEE_DEATH, 0.0f)
                .setItem(
                        "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDQ1NzlmMWVhMzg2NDI2OWMyMTQ4ZDgyN2MwODg3YjBjNWVkNDNhOTc1YjEwMmEwMWFmYjY0NGVmYjg1Y2NmZCJ9fX0="
                ));

    }

    @Override
    public void useUltimate(Player player) {
        final IGamePlayer gp = GamePlayer.getPlayer(player);
        final double health = gp.getHealth();
        final double maxHealth = gp.getMaxHealth();
        final double missingHp = (maxHealth - health) * healthRegenPercent / maxHealth;

        final double finalDamage = calculateDamage(player, 30.0d);

        final Location location = player.getLocation();
        final Vector vector = location.getDirection();
        location.add(vector.setY(0).multiply(5));
        location.add(0, 7, 0);

        final Bee bee = Entities.BEE.spawn(location, me -> {
            me.setSilent(true);
            me.setAI(false);
        });

        final Player nearestPlayer = Utils.getNearestPlayer(location, 50, player);
        PlayerLib.playSound(location, Sound.ENTITY_BEE_LOOP_AGGRESSIVE, 1.0f);

        new GameTask() {
            private int windupTime = 60;

            @Override
            public void run() {
                final Location lockLocation = nearestPlayer == null ? location.clone().subtract(0, 9, 0) : nearestPlayer.getLocation();
                final Location touchLocation = drawLine(location.clone(), lockLocation.clone());

                // BOOM
                if (windupTime-- <= 0) {
                    PlayerLib.stopSound(Sound.ENTITY_BEE_LOOP_AGGRESSIVE);
                    PlayerLib.spawnParticle(location, Particle.EXPLOSION_NORMAL, 5, 0.2, 0.2, 0.2, 0.1f);
                    PlayerLib.playSound(location, Sound.ENTITY_BEE_DEATH, 1.5f);
                    bee.remove();
                    cancel();

                    Utils.getEntitiesInRange(touchLocation, 1.5d).forEach(victim -> {
                        GamePlayer.damageEntity(victim, finalDamage, player, EnumDamageCause.FELL_THE_BREEZE);
                    });

                    PlayerLib.spawnParticle(touchLocation, Particle.EXPLOSION_LARGE, 3, 0.5, 0, 0.5, 0);
                    PlayerLib.playSound(touchLocation, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 1.25f);
                }

            }
        }.runTaskTimer(0, 1);

        Chat.sendMessage(player, "&a&l][ &a%s healed for &c%s❤ &a!", this.getName(), BukkitUtils.decimalFormat(missingHp));
        gp.heal(missingHp);
    }

    private Location drawLine(Location start, Location end) {
        double distance = start.distance(end);
        Vector vector = end.toVector().subtract(start.toVector()).normalize().multiply(0.5d);

        for (double i = 0.0D; i < distance; i += 0.5) {
            start.add(vector);
            if (start.getWorld() == null) {
                continue;
            }
            if (!start.getBlock().getType().isAir()) {
                final Location cloned = start.add(0, 0.15, 0);
                start.getWorld().spawnParticle(Particle.FLAME, cloned, 3, 0.1, 0.1, 0.1, 0.02);
                return cloned;
            }
            //start.getWorld().playSound(start, Sound.BLOCK_BAMBOO_HIT, SoundCategory.RECORDS, 1.0f, 2.0f);
            start.getWorld().spawnParticle(Particle.REDSTONE, start, 1, 0, 0, 0, 0, new Particle.DustOptions(Color.RED, 0.5f));
        }
        return start;
    }

    @Override
    public void onStart() {
        new GameTask() {
            @Override
            public void run() {
                Heroes.PYTARIA.getAlivePlayers().forEach(gp -> {
                    if (gp.getHealth() > gp.getMaxHealth() / 2) {
                        return;
                    }
                    final Player player = gp.getPlayer();
                    final Item item = player.getWorld()
                            .dropItemNaturally(
                                    player.getLocation(),
                                    new ItemStack(CollectionUtils.randomElement(Tag.SMALL_FLOWERS.getValues(), Material.POPPY))
                            );
                    item.setPickupDelay(10000);
                    item.setTicksLived(5980);
                });
            }
        }.runTaskTimer(0, 5);
    }

    @Override
    public DamageOutput processDamageAsDamager(DamageInput input) {
        return new DamageOutput(calculateDamage(input.getPlayer(), input.getDamage()));
    }

    @Override
    public DamageOutput processDamageAsVictim(DamageInput input) {
        //updateChestplateColor(input.getPlayer());
        return null;
    }

    private final ItemStack[] armorColors = {
            createChestplate(255, 128, 128),// 1
            createChestplate(255, 77, 77),  // 2
            createChestplate(255, 26, 26),  // 3
            createChestplate(179, 0, 0),    // 4
            createChestplate(102, 0, 0)     // 5
    };

    private ItemStack createChestplate(int red, int green, int blue) {
        return ItemBuilder.leatherTunic(Color.fromRGB(red, green, blue)).cleanToItemSack();
    }

    private void updateChestplateColor(Player player) {
        final PlayerInventory inventory = player.getInventory();
        final IGamePlayer gp = GamePlayer.getPlayer(player);
        final double missingHealth = gp.getMaxHealth() - gp.getHealth();

        if (isBetween(missingHealth, 0, 10)) {
            inventory.setChestplate(armorColors[0]);
        }
        else if (isBetween(missingHealth, 10, 20)) {
            inventory.setChestplate(armorColors[1]);
        }
        else if (isBetween(missingHealth, 20, 30)) {
            inventory.setChestplate(armorColors[2]);
        }
        else if (isBetween(missingHealth, 30, 40)) {
            inventory.setChestplate(armorColors[3]);
        }
        else {
            inventory.setChestplate(armorColors[4]);
        }
    }

    private boolean isBetween(double value, double min, double max) {
        return value >= min && value < max;
    }

    public double calculateDamage(Player player, double damage) {
        final IGamePlayer gp = GamePlayer.getPlayer(player);
        final double health = gp.getHealth();
        final double maxHealth = gp.getMaxHealth();
        //final double multiplier = ((maxHealth - health) / 10);
        //final double addDamage = (damage * 30 / 100) * multiplier;
        //
        //return Math.max(damage + addDamage, damage);

        return (health <= maxHealth / 2) ? damage * 1.5d : damage;

    }

    @Override
    public Talent getFirstTalent() {
        return Talents.FLOWER_ESCAPE.getTalent();
    }

    @Override
    public Talent getSecondTalent() {
        return Talents.FLOWER_BREEZE.getTalent();
    }

    @Override
    public Talent getPassiveTalent() {
        return Talents.EXCELLENCY.getTalent();
    }
}
