package me.hapyl.fight.game.heroes.storage;

import me.hapyl.fight.event.DamageInput;
import me.hapyl.fight.event.DamageOutput;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.IGamePlayer;
import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.HeroEquipment;
import me.hapyl.fight.game.heroes.Role;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.ui.UIComponent;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.util.Utils;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class Ninja extends Hero implements Listener, UIComponent {

    private final double damage = 8.0d;
    private final double ultimateDamage = 20.0d;
    private final int stunCd = 200;
    private final Weapon normalSword = new Weapon(Material.STONE_SWORD).setName("斬馬刀").setDamage(damage / 2.0d);

    private final ItemStack throwingStar = new ItemBuilder(Material.NETHER_STAR, "THROWING_STAR").setName("Throwing Star")
            .setAmount(5)
            .addClickEvent(this::shootStar)
            .withCooldown(10)
            .build();

    public Ninja() {
        super(
                "Ninja",
                "Extremely well trained fighter with a gift from the wind, that allows him to Dash, Double Jump and take no fall damage."
        );

        setRole(Role.ASSASSIN);
        setItem("1413159cfab50aba283e68c1659d74412392fbcb1f7d663d1bd2a2a6430c2743");

        final HeroEquipment equipment = this.getEquipment();
        equipment.setChestplate(Color.WHITE);
        equipment.setLeggings(Material.CHAINMAIL_LEGGINGS);
        equipment.setBoots(Material.CHAINMAIL_BOOTS);

        setWeapon(new Weapon(Material.STONE_SWORD).setName("斬馬刀").setDescription(String.format(
                "Light but sharp sword that stuns opponents upon charge hit. After using the charge hit, your weapon damage is reduced by &b50%%&7.____&aCooldown: &l%ss",
                BukkitUtils.decimalFormat(ultimateDamage)
        )).setDamage(damage));

        setUltimate(new UltimateTalent(
                "Throwing Stars",
                "Equip &c&l5&7 dead-accurate throwing stars that deals &c&l%.1f&7 damage upon hitting an enemy.".formatted(ultimateDamage),
                70
        ).setItem(Material.NETHER_STAR).setSound(Sound.ITEM_TRIDENT_RIPTIDE_1, 0.75f));
    }

    @Override
    public boolean processInvisibilityDamage(Player player, LivingEntity entity, double damage) {
        final IGamePlayer gamePlayer = GamePlayer.getPlayer(player);
        gamePlayer.removeEffect(GameEffectType.INVISIBILITY);
        gamePlayer.sendMessage("&cYou dealt damage and lost your invisibility!");

        PlayerLib.spawnParticle(player.getEyeLocation(), Particle.EXPLOSION_NORMAL, 20, 0.25, 0.5, 0.25, 0.02f);

        return false;
    }

    @Override
    public void useUltimate(Player player) {
        final PlayerInventory inventory = player.getInventory();

        setUsingUltimate(player, true);
        inventory.setItem(4, throwingStar);
        inventory.setHeldItemSlot(4);
        player.setCooldown(throwingStar.getType(), 20);
    }

    private void shootStar(Player player) {
        final ItemStack item = player.getInventory().getItemInMainHand();
        item.setAmount(item.getAmount() - 1);

        if (item.getAmount() <= 0) {
            setUsingUltimate(player, false);
        }

        Utils.rayTraceLine(
                player,
                40,
                0.5d,
                ultimateDamage,
                move -> PlayerLib.spawnParticle(move, Particle.FIREWORKS_SPARK, 1, 0.0d, 0.0d, 0.0d, 0.0f),
                hit -> PlayerLib.playSound(hit.getLocation(), Sound.ITEM_TRIDENT_HIT, 2.0f)
        );

        PlayerLib.playSound(player.getLocation(), Sound.ITEM_TRIDENT_THROW, 1.5f);

    }

    @Override
    public void onStart(Player player) {
        player.setAllowFlight(true);

        PlayerLib.addEffect(player, PotionEffectType.SPEED, 999999, 0);
    }

    @EventHandler()
    public void handleDoubleJump(PlayerToggleFlightEvent ev) {
        final Player player = ev.getPlayer();
        if (!validatePlayer(player) || player.hasCooldown(this.getItem().getType()) || player.isFlying()) {
            return;
        }

        ev.setCancelled(true);

        final Location location = player.getLocation();
        player.setVelocity(new Vector(0.0d, 1.0d, 0.0d));
        player.setFlying(false);
        player.setAllowFlight(false);
        player.setCooldown(this.getItem().getType(), 100);
        GameTask.runLater(() -> {
            player.setAllowFlight(true);
        }, 100);

        // fx
        PlayerLib.playSound(player.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 1.2f);
        PlayerLib.spawnParticle(location, Particle.EXPLOSION_NORMAL, 5, 0.2d, 0.0d, 0.2d, 0.03f);
    }

    @Override
    public @Nonnull String getString(Player player) {
        return player.hasCooldown(getItem().getType()) ? "&f🌊 &l%ss".formatted(BukkitUtils.roundTick(player.getCooldown(this.getItem()
                .getType()))) : "";
    }

    @Override
    public DamageOutput processDamageAsDamager(DamageInput input) {
        final Player player = input.getPlayer();
        final LivingEntity entity = input.getEntity();
        if (entity == null || entity == player || player.hasCooldown(this.getWeapon().getMaterial())) {
            return null;
        }

        // remove smoke bomb invisibility if exists
        if (GamePlayer.getPlayer(player).hasEffect(GameEffectType.INVISIBILITY)) {
            GamePlayer.getPlayer(player).removeEffect(GameEffectType.INVISIBILITY);
            Chat.sendMessage(player, "&aYour invisibility is gone because you dealt damage.");
            PlayerLib.playSound(player, Sound.ITEM_SHIELD_BREAK, 2.0f);
        }

        if (player.getInventory().getHeldItemSlot() != 0) {
            return null;
        }

        player.getInventory().setItem(0, normalSword.getItem());
        player.setCooldown(this.getWeapon().getMaterial(), stunCd);

        // Fx
        PlayerLib.playSound(entity.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1.25f);
        PlayerLib.spawnParticle(entity.getEyeLocation(), Particle.VILLAGER_ANGRY, 5, 0.2d, 0.2d, 0.2d, 0.0f);

        GameTask.runLater(() -> {
            player.getInventory().setItem(0, this.getWeapon().getItem());
        }, stunCd);

        return null;
    }

    @Override
    public DamageOutput processDamageAsVictim(DamageInput input) {
        if (input.getDamageCause() == EnumDamageCause.FALL) {
            return DamageOutput.CANCEL;
        }
        return null;
    }

    @Override
    public Talent getFirstTalent() {
        return Talents.NINJA_DASH.getTalent();
    }

    @Override
    public Talent getSecondTalent() {
        return Talents.NINJA_SMOKE.getTalent();
    }

    @Override
    public Talent getPassiveTalent() {
        return Talents.FLEET_FOOT.getTalent();
    }
}
