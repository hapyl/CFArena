package me.hapyl.fight.game.heroes.archive.swooper;

import me.hapyl.fight.game.Debug;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.HeroEquipment;
import me.hapyl.fight.game.heroes.Role;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.ui.UIComponent;
import me.hapyl.fight.game.weapons.PackedParticle;
import me.hapyl.fight.game.weapons.RangeWeapon;
import me.hapyl.fight.util.Buffer;
import me.hapyl.fight.util.BufferMap;
import me.hapyl.fight.util.ItemStacks;
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

import javax.annotation.Nonnull;

public class Swooper extends Hero implements Listener, UIComponent {

    private final int bufferSize = 5 * 20;
    private final int ultimateSpeed = 2;
    private final int SHOWSTOPPER_DELAY = 30;

    private final ItemStack rocketLauncher = new ItemBuilder(Material.GOLDEN_HORSE_ARMOR, "swooper_ultimate")
            .setName("&aRocket Launcher")
            .addClickEvent(this::launchProjectile)
            .build();

    private final BufferMap<Player, SwooperData> dataMap = new BufferMap<>();

    public Swooper() {
        super("Swooper");

        setRole(Role.RANGE);

        setInfo("A sniper with slow firing rifle, but fast ways to move around the battlefield.");
        setItem("f181c811ad37467550d7c01cac2e5223c4e99fa7906348f940c9456d8aa0cd1b");

        final HeroAttributes attributes = getAttributes();
        attributes.setValue(AttributeType.SPEED, 0.23d);

        final HeroEquipment equipment = this.getEquipment();
        equipment.setChestplate(25, 53, 82);
        equipment.setLeggings(25, 53, 92);
        equipment.setBoots(25, 53, 102);

        setWeapon(new RangeWeapon(Material.WOODEN_HOE, "swooper_weapon") {

            @Override
            public double getDamage(Player player) {
                return player.isSneaking() ? 10.0d : 5.0d;
            }

            @Override
            public double getMaxDistance(Player player) {
                return player.isSneaking() ? 50 : 25;
            }

            @Override
            public EnumDamageCause getDamageCause(Player player) {
                return EnumDamageCause.RIFLE;
            }

            @Override
            public void onShoot(Player player) {
                PlayerLib.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 2.0f);
            }

        }.setCooldown(45)
                .setParticleTick(new PackedParticle(Particle.FIREWORKS_SPARK))
                .setMaxDistance(-1)
                .setDamage(-1)
                .setName("Sniper Rifle")
                .setDescription("""
                        Slow firing, but high-damage rifle.
                                        
                        &6&lSNEAK&7 to activate sniper scope, increasing your rifle's damage and max distance.
                        """));

        setUltimate(new UltimateTalent(
                "Showstopper", """
                Equip a rocket launcher for {duration}.
                                
                &6&lCLICK &7to launch explosive in front of you that explodes on impact dealing massive damage.
                """,
                75
        ).setDuration(160).setItem(Material.GOLDEN_HORSE_ARMOR).setSound(Sound.BLOCK_BEACON_POWER_SELECT, 2.0f));
    }

    @Override
    public void onStart() {
        //new GameTask() {
        //    private int tick = 0;
        //
        //    @Override
        //    public void run() {
        //        for (GamePlayer gamePlayer : Heroes.SWOOPER.getAlivePlayers()) {
        //            final Player player = gamePlayer.getPlayer();
        //
        //            if (gamePlayer.isDead() || isUsingUltimate(player)) {
        //                return;
        //            }
        //
        //            final Buffer<SwooperData> buffer = dataMap.computeIfAbsent(player, bufferSize);
        //            buffer.add(new SwooperData(gamePlayer));
        //
        //            // Draw lines every 5 ticks
        //            if (tick % 5 == 0) {
        //                buffer.forEach(data -> {
        //                    PlayerLib.spawnParticle(player, data.getLocation().add(0.0d, 0.15d, 0.0d), Particle.CRIT_MAGIC, 1);
        //                });
        //            }
        //        }
        //
        //        tick++;
        //    }
        //}.runTaskTimer(0, 1);
    }

    @Override
    public void onStop() {
        //dataMap.removeBuffers();
    }

    @Override
    public void onDeath(Player player) {
        //dataMap.removeBuffer(player);
    }

    public void useUltimate(Player player) {
        setUsingUltimate(player, true);

        final PlayerInventory inventory = player.getInventory();

        // Add delay before shooting but increase time
        player.setCooldown(rocketLauncher.getType(), SHOWSTOPPER_DELAY);

        inventory.setItem(4, rocketLauncher);
        inventory.setHeldItemSlot(4);


        new GameTask() {
            private final int DURATION = getUltimateDuration() + SHOWSTOPPER_DELAY;
            private int tick = DURATION;

            @Override
            public void run() {
                if (tick-- <= 0 || player.getInventory().getItem(4) == null) {
                    removeRocketLauncher();
                    return;
                }

                final int tick20 = tick * 20 / DURATION;
                final StringBuilder builder = new StringBuilder();

                for (int i = 0; i < 20; i++) {
                    builder.append(i <= tick20 ? ChatColor.GOLD : ChatColor.DARK_GRAY).append("-");
                }

                Chat.sendTitle(player, "&eRocket Fuse", builder.toString(), 0, 5, 2);
            }

            private void removeRocketLauncher() {
                setUsingUltimate(player, false);
                player.getInventory().setItem(4, ItemStacks.AIR);
                cancel();
            }
        }.runTaskTimer(0, 1);
    }

    @Nonnull
    @Override
    public String getString(Player player) {
        return "";
        //final Buffer<SwooperData> buffer = dataMap.get(player);
        //
        //if (buffer == null) {
        //    return "";
        //}
        //
        //final SwooperData first = buffer.peekFirst();
        //return first == null ? "" : "&e⇄ %.1f ❤".formatted(first.health());
    }

    @EventHandler()
    public void handleSniperScope(PlayerToggleSneakEvent ev) {
        final Player player = ev.getPlayer();
        if (validatePlayer(player) && player.getInventory().getHeldItemSlot() == 0) {
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
        return Talents.BLINK.getTalent();
    }

    @Override
    public Talent getPassiveTalent() {
        return Talents.SNIPER_SCOPE.getTalent();
    }

    @Deprecated
    private void useUltimateSwoop(Player player) {
        final Buffer<SwooperData> buffer = dataMap.remove(player);

        if (buffer == null) {
            Chat.sendMessage(player, "&cNo buffer, somehow.");
            return;
        }

        player.setGameMode(GameMode.SPECTATOR);
        player.addPotionEffect(PotionEffectType.SPEED.createEffect(10000, 3));

        new GameTask() {

            private SwooperData previous = null;

            @Override
            public void run() {
                if (next()) {
                    return;
                }

                this.cancel();
                player.setGameMode(GameMode.SURVIVAL);
                player.removePotionEffect(PotionEffectType.SPEED);

                if (previous == null) {
                    Debug.warn("previous swooper data somehow null for " + player.getName());
                    return; // should not happen but just in case
                }

                // Give health
                previous.player().setHealth(previous.health());
            }

            private boolean next() {
                for (int i = 0; i < ultimateSpeed; i++) {
                    final SwooperData data = buffer.pollLast();

                    if (data != null) {
                        previous = data;
                        player.teleport(data.location());
                    }
                    else {
                        return false;
                    }
                }

                return true;
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
        }.runTaskTimer(0, 1);
    }

}
