package me.hapyl.fight.game.heroes.storage;

import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.PlayerElement;
import me.hapyl.fight.game.cosmetic.CosmeticsHandle;
import me.hapyl.fight.game.heroes.ClassEquipment;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.Role;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.util.Utils;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class Hercules extends Hero implements Listener, PlayerElement {

    private final int tridentCooldown = 300;
    private final Map<Player, Trident> fragileTrident = new HashMap<>();

    public Hercules() {
        super("Hercules");
        setRole(Role.MELEE);
        setInfo(
                "The greatest warrior of all time - \"The Great Hercules\" descended from heaven to punish the infidels! Super-Duper strong punches give you a chance to win."
        );
        setItem("f210c961b9d787327c0d1646e65ae40c6d834514877824335d4b9b62b2365a24");

        final ClassEquipment equipment = getEquipment();
        equipment.setChestplate(Color.WHITE);
        equipment.setBoots(Material.LEATHER_BOOTS);

        setWeapon(new Weapon(Material.TRIDENT).setName("Gorynych")
                .setDescription(
                        "A loyal trident which will return to you no matter what!____&e&lTHROW &7the trident to deal range damage."
                )
                .setDamage(10)
                .addEnchant(Enchantment.LOYALTY, 3));

        setUltimate(new UltimateTalent(
                "Crush the Ground",
                "Call upon divine power to increase your &ejump height &7and &cplunging damage&7 for {duration}.",
                50
        ).setDuration(240).setItem(Material.NETHERITE_HELMET).setCdSec(30));
    }

    @Override
    public void useUltimate(Player player) {
        // Fx
        new GameTask() {
            private int tick = 0;

            @Override
            public void run() {
                if (tick % 4 == 0) {
                    if (tick <= 20) {
                        PlayerLib.addEffect(player, PotionEffectType.SLOW, 4, tick / 4);
                        PlayerLib.playSound(player, Sound.ENTITY_WITHER_SHOOT, (float) (0.5d + (0.1d * tick / 4)));
                    }
                    else {
                        cancel();
                        PlayerLib.playSound(player, Sound.ENTITY_WITHER_HURT, 1.25f);
                    }
                }
                ++tick;
            }
        }.runTaskTimer(0, 1);
    }

    @Override
    public void onStop() {
        fragileTrident.values().forEach(Trident::remove);
        fragileTrident.clear();
    }

    @EventHandler
    public void handleFragileTrident(ProjectileLaunchEvent ev) {
        if (ev.getEntity() instanceof final Trident trident && trident.getShooter() instanceof final Player player) {
            if (player.hasCooldown(Material.TRIDENT)) {
                ev.setCancelled(true);
                return;
            }

            trident.setDamage(getWeapon().getDamage() * 1.5f);
            trident.setInvulnerable(true);
            trident.setPersistent(true);

            new GameTask() {
                @Override
                public void run() {
                    if (trident.isDead() || !player.getInventory().contains(Material.TRIDENT)) {
                        return;
                    }

                    giveTridentBack(player, false);
                }
            }.runTaskLater(15 * 20);

            if (fragileTrident.containsKey(player)) {
                fragileTrident.get(player).remove();
            }
            fragileTrident.put(player, trident);
        }
    }

    @EventHandler
    public void handleFragileBack(ProjectileHitEvent ev) {
        if (Manager.current().isGameInProgress()) {
            final Projectile entity = ev.getEntity();
            if (entity instanceof Trident trident && trident.getShooter() instanceof Player player) {
                giveTridentBack(player, ev.getHitEntity() != null);
            }
        }
    }

    private void giveTridentBack(Player player, boolean lessCooldown) {
        if (!fragileTrident.containsKey(player)) {
            return;
        }

        final Trident trident = fragileTrident.get(player);
        trident.remove();

        player.setCooldown(Material.TRIDENT, lessCooldown ? tridentCooldown / 3 : tridentCooldown);
        player.getInventory().setItem(0, this.getWeapon().getItem());
        player.updateInventory();

        fragileTrident.remove(player);
    }

    @EventHandler()
    public void handlePlayerJump(PlayerStatisticIncrementEvent ev) {
        final Player player = ev.getPlayer();
        if (ev.getStatistic() == Statistic.JUMP) {
            if (isUsingUltimate(player)) {
                final Vector velocity = player.getVelocity();
                player.setVelocity(velocity.setY(0.75f));
            }
        }
    }

    @EventHandler
    public void handleUltimate(PlayerToggleSneakEvent ev) {
        final Player player = ev.getPlayer();

        if (validatePlayer(player, Heroes.HERCULES) && player.isSneaking() && canPlunge(player) && !isPlunging(player)) {
            performPlunge(player, getPlungeDistance(player));
        }
    }

    private int getPlungeDistance(Player player) {
        final Location location = player.getLocation().clone();
        if (player.isOnGround()) {
            return -1;
        }
        for (int i = 1; i < location.getBlockY(); i++) {
            location.subtract(0.0d, i, 0.0d);
            if (!location.getBlock().getType().isAir()) {
                return i;
            }
            location.add(0.0d, i, 0.0d);
        }
        return -1;
    }

    private void performPlunge(Player player, int distance) {
        final double plungeDamage = 5.0d + (1.5d * distance);

        PlayerLib.playSound(player.getLocation(), Sound.ITEM_TRIDENT_RIPTIDE_2, 1.75f);
        PlayerLib.addEffect(player, PotionEffectType.JUMP, 80, 255);

        player.setVelocity(new Vector(0.0d, -1.0d, 0.0d));
        player.addScoreboardTag("plunging");

        new GameTask() {
            private int tickTime = 80;

            @Override
            public void run() {
                if (tickTime-- <= 0 || player.isOnGround()) {
                    this.cancel();

                    player.removeScoreboardTag("plunging");
                    PlayerLib.removeEffect(player, PotionEffectType.JUMP);

                    CosmeticsHandle.GROUND_PUNCH_COSMETIC.playAnimation(player.getLocation(), 2);

                    Utils.getEntitiesInRange(player.getLocation(), 4).forEach(target -> {
                        if (target == player) {
                            return;
                        }
                        GamePlayer.damageEntity(
                                target,
                                isUsingUltimate(player) ? plungeDamage * 2 : plungeDamage,
                                player,
                                EnumDamageCause.PLUNGE
                        );
                    });
                }

            }
        }.runTaskTimer(0, 1);
    }

    @Override
    public void onStop(Player player) {
        player.removeScoreboardTag("plunging");
    }

    private boolean isPlunging(Player player) {
        return player.getScoreboardTags().contains("plunging");
    }

    private boolean canPlunge(Player player) {
        return getPlungeDistance(player) > 3;
    }

    @Override
    public Talent getFirstTalent() {
        return Talents.HERCULES_DASH.getTalent();
    }

    @Override
    public Talent getSecondTalent() {
        return Talents.HERCULES_UPDRAFT.getTalent();
    }

    @Override
    public Talent getPassiveTalent() {
        return Talents.PLUNGE.getTalent();
    }

}
