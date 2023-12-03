package me.hapyl.fight.game.heroes.storage;

import me.hapyl.fight.event.DamageInput;
import me.hapyl.fight.event.DamageOutput;
import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.HeroEquipment;
import me.hapyl.fight.game.heroes.Role;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.ui.UIComponent;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.util.Utils;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.math.Numbers;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class Mage extends Hero implements UIComponent {

    private final double wyvernHealingAmount = 25.0d;
    private final int wyvernHeartLength = 500;

    private final int dragonSkinLength = 400;

    private final int maxSoulsAmount = 26;

    private final ItemStack itemHeartOfWyvern = new ItemBuilder(Material.FERMENTED_SPIDER_EYE, "wyvern")
            .addClickEvent(this::useWyvern)
            .setName("&aHeart of Wyvern &7(Right Click)")
            .addLore("")
            .addLore("&a+ %s ❤".formatted(wyvernHealingAmount))
            .addLore("&a+ Speed")
            .addLore("&c+ Weakness")
            .build();

    private final ItemStack itemDragonSkin = new ItemBuilder(Material.PHANTOM_MEMBRANE, "dragon_skin")
            .addClickEvent(this::useDragon)
            .setName("&aDragon's Skin &7(Right Click)")
            .addLore("")
            .addLore("&a+ Strength")
            .addLore("&c+ Slowness")
            .build();

    private final Map<Player, Integer> soulsCharge = new HashMap<>();

    public Mage() {
        super("Mage");

        setRole(Role.MELEE_RANGE);

        setInfo(
                "Amateur Necromancer with ability to absorb soul fragments upon hitting his foes to use them as fuel for his &e&lSoul Eater&7.__Which makes him both &bmelee &7and &brange &7warrior!"
        );

        setItem("f41e6e4bcd2667bb284fb0dde361894840ea782efbfb717f6244e06b951c2b3f");

        final HeroEquipment equipment = this.getEquipment();
        equipment.setChestplate(82, 12, 135);
        equipment.setLeggings(163, 52, 247);
        equipment.setBoots(82, 12, 135);

        setWeapon(new Weapon(Material.IRON_HOE) {
            @Override
            public void onRightClick(Player player, ItemStack item) {
                if (player.hasCooldown(Material.IRON_HOE)) {
                    return;
                }

                final int souls = getSouls(player);
                if (souls <= 0) {
                    PlayerLib.playSound(player, Sound.ENTITY_PLAYER_BURP, 2.0f);
                    return;
                }

                addSouls(player, -1);
                player.setCooldown(Material.IRON_HOE, 10);
                PlayerLib.playSound(player, Sound.BLOCK_SOUL_SAND_BREAK, 0.75f);
                Utils.rayTraceLine(player, 50, 0.5, -1.0d, this::spawnParticles, this::hitEnemy);

            }

            private void spawnParticles(Location location) {
                PlayerLib.spawnParticle(location, Particle.SOUL, 1, 0.1d, 0.0d, 0.1d, 0.035f);
            }

            private void hitEnemy(LivingEntity livingEntity) {
                final Location location = livingEntity.getLocation();
                livingEntity.addScoreboardTag("LastDamage=Soul");
                livingEntity.damage(this.getDamage());
                PlayerLib.spawnParticle(location, Particle.SOUL, 8, 0, 0, 0, 0.10f);
                PlayerLib.spawnParticle(location, Particle.SOUL_FIRE_FLAME, 10, 0, 0, 0, 0.25f);
            }

        }.setDamage(10.0d)
                .setName("Soul Eater")
                .setDescription(
                        "A weapon capable of absorbing soul fragments and convert them into fuel.____&e&lRIGHT CLICK &7to shoot a soul laser.")
                .setId("soul_eater"));

        setUltimate(new UltimateTalent(
                "Magical Trainings",
                String.format(
                        "Retrieve two ancient spells and use one of them to your advantage!____&a- &7Heart of Wyvern heals you for &c%s&c❤&7, makes you fast but weak for &b%ss&7.____&a- &7Dragon's Skin makes you incredible strong but slow for &b%ss&7.____Only one of the spells can be used at the same time and you will &nnot&7 gain &b&l※ &7until spell is over.",
                        wyvernHealingAmount,
                        BukkitUtils.roundTick(wyvernHeartLength),
                        BukkitUtils.roundTick(dragonSkinLength)
                ),
                40
        ).setItem(Material.WRITABLE_BOOK).setCdSec(-1));
    }

    @Override
    public void useUltimate(Player player) {
        final PlayerInventory inventory = player.getInventory();
        setUsingUltimate(player, true);

        inventory.setItem(3, itemHeartOfWyvern);
        inventory.setItem(5, itemDragonSkin);
        inventory.setHeldItemSlot(4);
    }

    @Override
    public DamageOutput processDamageAsDamager(DamageInput input) {
        final LivingEntity victim = input.getEntity();
        final Player player = input.getPlayer();
        if (victim == null) {
            return null;
        }
        if (!victim.getScoreboardTags().contains("LastDamage=Soul")) {
            addSouls(player, 1);
        }
        victim.removeScoreboardTag("LastDamage=Soul");
        return null;
    }

    @Override
    public void onDeath(Player player) {
        soulsCharge.remove(player);
    }

    private void addSouls(Player player, int amount) {
        this.soulsCharge.put(player, Numbers.clamp(getSouls(player) + amount, 0, maxSoulsAmount));
    }

    private int getSouls(Player player) {
        return soulsCharge.getOrDefault(player, 0);
    }

    @Override
    public void onStop() {
        soulsCharge.clear();
    }

    private void useWyvern(Player player) {
        this.setUsingUltimate(player, true, wyvernHeartLength);

        PlayerLib.addEffect(player, PotionEffectType.SPEED, wyvernHeartLength, 2);
        PlayerLib.addEffect(player, PotionEffectType.WEAKNESS, wyvernHeartLength, 1);
        GamePlayer.getPlayer(player).heal(wyvernHealingAmount);

        // fx
        PlayerLib.spawnParticle(player.getLocation().add(0.0d, 1.0d, 0.0d), Particle.HEART, 7, 0.3d, 0.2d, 0.3d, 1);
        Chat.sendMessage(player, "&aYou have used &lHeart of Wyvern&a!");
        removeUltimateItems(player);
    }

    private void useDragon(Player player) {
        this.setUsingUltimate(player, true, dragonSkinLength);

        PlayerLib.addEffect(player, PotionEffectType.SLOW, dragonSkinLength, 2);
        PlayerLib.addEffect(player, PotionEffectType.INCREASE_DAMAGE, dragonSkinLength, 4);
        PlayerLib.addEffect(player, PotionEffectType.JUMP, dragonSkinLength, 250);

        // fx
        PlayerLib.spawnParticle(player.getLocation().add(0.0d, 1.0d, 0.0d), Particle.CRIT_MAGIC, 40, 0.1, 0.1, 0.1, 1);
        Chat.sendMessage(player, "&aYou have used &lDragon's Skin&a!");
        removeUltimateItems(player);
    }

    private void removeUltimateItems(Player player) {
        final PlayerInventory inventory = player.getInventory();
        inventory.remove(Material.PHANTOM_MEMBRANE);
        inventory.remove(Material.FERMENTED_SPIDER_EYE);
    }

    @Override
    public Talent getFirstTalent() {
        return Talents.MAGE_TRANSMISSION.getTalent();
    }

    @Override
    public Talent getSecondTalent() {
        return Talents.ARCANE_MUTE.getTalent();
    }

    @Override
    public Talent getPassiveTalent() {
        return Talents.SOUL_HARVEST.getTalent();
    }

    @Override
    public @Nonnull String getString(Player player) {
        final int souls = getSouls(player);
        return "&e⦾ &l" + souls + (souls == maxSoulsAmount ? " FULL" : "");
    }
}
