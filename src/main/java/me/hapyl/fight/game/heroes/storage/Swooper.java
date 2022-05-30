package me.hapyl.fight.game.heroes.storage;

import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.GamePlayer;
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
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.math.Numbers;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class Swooper extends Hero implements Listener {

    private final int rifleCooldown = 45;

    private final ItemStack rocketLauncher = new ItemBuilder(Material.GOLDEN_HORSE_ARMOR, "swooper_ultimate")
            .setName("&aRocket Launcher")
            .addClickEvent(this::launchProjectile)
            .build();

    public Swooper() {
        super("Swooper");

        setRole(Role.RANGE);

        this.setInfo("A sniper with slow firing rifle, but fast ways to move around the battlefield.");
        this.setItem(Material.SUGAR);

        final ClassEquipment equipment = this.getEquipment();
        equipment.setHelmet(
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjE4MWM4MTFhZDM3NDY3NTUwZDdjMDFjYWMyZTUyMjNjNGU5OWZhNzkwNjM0OGY5NDBjOTQ1NmQ4YWEwY2QxYiJ9fX0="
        );
        equipment.setChestplate(25, 53, 82);
        equipment.setLeggings(25, 53, 92);
        equipment.setBoots(25, 53, 102);

        this.setWeapon(new Weapon(Material.WOODEN_HOE) {

            @Override
            public void onRightClick(Player player, ItemStack item) {
                if (player.hasCooldown(item.getType())) {
                    return;
                }

                player.setCooldown(item.getType(), rifleCooldown);
                PlayerLib.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 2.0f);
                Utils.rayTraceLine(
                        player,
                        player.isSneaking() ? 50 : 25,
                        0.5f,
                        player.isSneaking() ? 10.0d : 5.0d,
                        EnumDamageCause.RIFLE,
                        move -> {
                            PlayerLib.spawnParticle(move, Particle.FIREWORKS_SPARK, 1, 0.0d, 0.0d, 0.0d, 0.0f);
                        }, null
                );
            }

        }.setId("swooper_weapon").setName("Sniper Rifle").setInfo("Slow firing, but high damage rifle."));


        this.setUltimate(new UltimateTalent(
                "Showstopper",
                "Equip a rocket launcher for {duration}. &6&lCLICK &7to launch explosive in front of you that explodes on impact dealing massive damage.",
                80
        ).setDuration(200).setItem(Material.GOLDEN_HORSE_ARMOR));

    }

    @Override
    public void useUltimate(Player player) {
        setUsingUltimate(player, true);

        final PlayerInventory inventory = player.getInventory();
        inventory.setItem(4, rocketLauncher);
        inventory.setHeldItemSlot(4);

        new GameTask() {
            private int tick = getUltimateDuration();

            private void removeRocketLauncher() {
                setUsingUltimate(player, false);
                player.getInventory().setItem(4, new ItemStack(Material.AIR));
                this.cancel();
            }

            @Override
            public void run() {
                if (tick-- <= 0 || player.getInventory().getItem(4) == null) {
                    this.removeRocketLauncher();
                    return;
                }

                final int tick20 = tick * 20 / getUltimateDuration();
                final StringBuilder builder = new StringBuilder();
                for (int i = 0; i < 20; i++) {
                    builder.append(i <= tick20 ? ChatColor.GOLD : ChatColor.DARK_GRAY).append("-");
                }
                Chat.sendTitle(player, "&eRocket Fuse", builder.toString(), 0, 5, 2);

            }
        }.runTaskTimer(0, 1);
    }

    private void launchProjectile(Player player) {
        player.getInventory().getItemInMainHand().setAmount(0);
        final Location location = player.getEyeLocation().clone();
        final Vector vector = location.getDirection();

        player.setVelocity(player.getLocation().getDirection().normalize().multiply(-0.5d));

        new GameTask() {
            private double distance = 0.5d;
            private float pitch = 0.45f;

            private void explode() {
                PlayerLib.spawnParticle(location, Particle.EXPLOSION_HUGE, 1, 0, 0, 0, 0);
                PlayerLib.spawnParticle(location, Particle.LAVA, 20, 1, 1, 1, 0);
                PlayerLib.spawnParticle(location, Particle.FLAME, 15, 1, 1, 1, 0.75f);
                PlayerLib.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 0.0f);
                PlayerLib.playSound(location, Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 0.75f);

                Utils.getEntitiesInRange(location, 6.0d).forEach(entity -> {
                    final double damage = (100 - (entity.getLocation().distance(location) * 8.33d));
                    GamePlayer.damageEntity(entity, (entity == player ? damage / 2 : damage), player, EnumDamageCause.ENTITY_EXPLOSION);
                });

                this.cancel();
            }

            @Override
            public void run() {
                if (((distance += 0.5d) >= 40.0d) || (!location.getBlock().getType().isAir())) {
                    explode();
                    return;
                }

                double x = distance * vector.getX();
                double y = distance * vector.getY();
                double z = distance * vector.getZ();

                location.add(x, y, z);
                pitch = Numbers.clamp(pitch + 0.025f, 0.0f, 2.0f);
                PlayerLib.spawnParticle(location, Particle.LAVA, 4, 0.01, 00.1, 00.1, 0);
                PlayerLib.playSound(location, Sound.BLOCK_NOTE_BLOCK_PLING, pitch);

            }
        }.runTaskTimer(0, 1);
    }

    @EventHandler()
    public void handleSniperScope(PlayerToggleSneakEvent ev) {
        final Player player = ev.getPlayer();
        if (validatePlayer(player, Heroes.SWOOPER) && player.getInventory().getHeldItemSlot() == 0) {
            if (ev.isSneaking()) {
                PlayerLib.addEffect(player, PotionEffectType.SLOW, 10000, 4);
            }
            else {
                PlayerLib.removeEffect(player, PotionEffectType.SLOW);
            }
        }
    }

    @Override
    public Talent getFirstTalent() {
        return Talents.BLAST_PACK.getTalent();
    }

    @Override
    public Talent getSecondTalent() {
        return null;
    }

    @Override
    public Talent getPassiveTalent() {
        return Talents.SNIPER_SCOPE.getTalent();
    }
}
