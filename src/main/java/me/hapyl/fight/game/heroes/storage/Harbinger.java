package me.hapyl.fight.game.heroes.storage;

import io.netty.util.internal.ConcurrentSet;
import me.hapyl.fight.event.DamageInput;
import me.hapyl.fight.event.DamageOutput;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.heroes.ClassEquipment;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.Role;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentHandle;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.util.Utils;
import me.hapyl.spigotutils.module.math.Geometry;
import me.hapyl.spigotutils.module.math.gometry.WorldParticle;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class Harbinger extends Hero implements Listener {

    private final double ultimateRadius = 4.0d;
    private final Map<Player, Set<LivingEntity>> riptideAffected = new HashMap<>();

    public Harbinger() {
        super("Harbinger", "She is a harbinger of unknown organization. Nothing else is known.", Material.ANVIL);

        this.setRole(Role.STRATEGIST);

        final ClassEquipment equipment = this.getEquipment();
        equipment.setHelmet(
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjJhMWFjMmE4ZGQ0OGMzNzE0ODI4MDZiMzk2MzU3MTk1Mjk5N2E1NzEyODA2ZTJjODA2MGI4ZTc3Nzc3NTQifX19");
        equipment.setChestplate(82, 82, 76);
        equipment.setLeggings(54, 48, 48);
        equipment.setBoots(183, 183, 180);

        this.setWeapon(new Weapon(Material.BOW).setDamage(2.0d).setName("Bow").setInfo("Just a normal bow."));

        this.setUltimate(new UltimateTalent(
                "Crowned Mastery",
                "Gather the energy around you to execute a fatal slash:____While in &e&lRange Stance&7, shoot a magic arrow in front of you that explodes on impact, dealing AoE damage and applying &bRiptide &7effect to opponents.____While in &e&lMelee Stance&7, perform a slash around you that deals AoE damage and executes &bRiptide Slash &7if opponent is affected by &bRiptide&7.",
                70
        ).setItem(Material.DIAMOND).setDuration(40));
    }

    @Override
    public DamageOutput processDamageAsDamager(DamageInput input) {
        final Player player = input.getPlayer();
        final LivingEntity entity = input.getEntity();

        if (!TalentHandle.MELEE_STANCE.isActive(player) || !isAffectedByRiptide(player, entity)) {
            return null;
        }

        removeRiptide(player, entity);
        executeRiptideSlash(player, entity);

        return null;
    }

    public void executeRiptideSlash(Player player, LivingEntity entity) {
        if (entity.isDead()) {
            return;
        }

        final int maxTicks = entity.getMaximumNoDamageTicks();
        entity.setMaximumNoDamageTicks(0);

        GameTask.runTaskTimerTimes((task, tick) -> {
            GamePlayer.damageEntity(entity, 3.0d, player, EnumDamageCause.RIPTIDE);
            entity.setVelocity(new Vector(new Random().nextDouble() * 0.5d, 0.25d, new Random().nextDouble() * 0.5d));

            if (tick == 0) {
                entity.setMaximumNoDamageTicks(maxTicks);
            }

            final Location location = entity.getLocation();
            PlayerLib.spawnParticle(location, Particle.SWEEP_ATTACK, 1, 0, 0, 0, 0);
            PlayerLib.playSound(location, Sound.ITEM_BUCKET_FILL, 1.75f);
        }, 0, 2, 5);
    }

    @EventHandler()
    public void handleRiptide(ProjectileHitEvent ev) {
        if (ev.getEntity() instanceof Arrow arrow && arrow.getShooter() instanceof Player player) {
            if (!arrow.isCritical() || !validatePlayer(player, Heroes.HARBINGER)) {
                return;
            }

            final Entity hitEntity = ev.getHitEntity();
            if (!(hitEntity instanceof LivingEntity living)) {
                return;
            }

            if (!isAffectedByRiptide(player, living)) {
                addRiptide(player, living);
            }
        }
    }

    public Set<LivingEntity> getAffectedSet(Player player) {
        return riptideAffected.computeIfAbsent(player, (t) -> new ConcurrentSet<>());
    }

    @Override
    public void onStop() {
        riptideAffected.clear();
    }

    @Override
    public void onStart(Player player) {
        player.getInventory().setItem(9, new ItemStack(Material.ARROW));
    }

    @Override
    public void onDeath(Player player) {
        riptideAffected.remove(player);
    }

    @Override
    public void onStart() {
        new GameTask() {
            @Override
            public void run() {
                if (riptideAffected.isEmpty()) {
                    return;
                }

                // display particles for riptide owner
                riptideAffected.forEach((player, set) -> {
                    for (final LivingEntity living : set) {
                        if (living.isDead()) {
                            executeRiptideSlash(player, living);
                            set.remove(living);
                            return;
                        }

                        final Location location = living.getEyeLocation().add(0.0d, 0.2d, 0.0d);
                        PlayerLib.spawnParticle(location, Particle.WATER_SPLASH, 10, 0.15d, 0.5d, 0.15d, 0.01f);
                        PlayerLib.spawnParticle(location, Particle.GLOW, 5, 0.15d, 0.15d, 0.5d, 0.025f);

                        living.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20, 0));
                        living.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 0));
                    }
                });
            }
        }.runTaskTimer(0, 10);
    }

    private void addRiptide(Player player, LivingEntity living) {
        getAffectedSet(player).add(living);
        if (living instanceof Player victim) {
            PlayerLib.playSound(victim, Sound.AMBIENT_UNDERWATER_ENTER, 1.25f);
        }
    }

    private void removeRiptide(Player player, LivingEntity living) {
        getAffectedSet(player).remove(living);
        if (living instanceof Player victim) {
            PlayerLib.playSound(victim, Sound.AMBIENT_UNDERWATER_EXIT, 1.75f);
        }
    }

    private boolean isAffectedByRiptide(Player player, LivingEntity entity) {
        return getAffectedSet(player).contains(entity);
    }

    @Override
    public void useUltimate(Player player) {
        final Location playerLocation = player.getLocation();
        PlayerLib.addEffect(player, PotionEffectType.SLOW, 20, 2);
        PlayerLib.playSound(playerLocation, Sound.BLOCK_CONDUIT_AMBIENT, 2.0f);

        // Stance Check
        final boolean isMeleeStance = TalentHandle.MELEE_STANCE.isActive(player);

        if (isMeleeStance) {
            // Melee Stance
            new GameTask() {
                @Override
                public void run() {
                    new GameTask() {
                        private double d = 0.0d;

                        @Override
                        public void run() {
                            if (d < Math.PI * 2) {
                                final Location location = player.getEyeLocation();

                                final double x = ultimateRadius * Math.sin(d);
                                final double z = ultimateRadius * Math.cos(d);

                                location.add(x, 0, z);

                                Utils.getEntitiesInRange(location, 2.0d).forEach(entity -> {
                                    if (entity == player) {
                                        return;
                                    }

                                    GamePlayer.damageEntity(entity, 40.0d, player);
                                });

                                PlayerLib.spawnParticle(location, Particle.SWEEP_ATTACK, 1, 0, 0, 0, 0);
                                PlayerLib.spawnParticle(location, Particle.FALLING_WATER, 3, 0.5, 0, 0.5, 0);

                                d += Math.PI / 8;
                                return;
                            }

                            this.cancel();
                        }
                    }.runTaskTimer(0, 1);
                }
            }.runTaskLater(15);
            return;
        }

        // Ranged Stance
        final Location location = playerLocation.add(playerLocation.getDirection().setY(0.0d).multiply(1.5d));
        final Arrow arrow = player.getWorld()
                .spawnArrow(
                        location.clone().add(0.0d, 3.0d, 0.0d),
                        playerLocation.getDirection().normalize().multiply(0.75d).setY(-0.25d),
                        0,
                        0
                );

        arrow.setShooter(player);
        arrow.setCritical(false);
        arrow.setColor(Color.AQUA);

        new GameTask() {
            @Override
            public void run() {

                Utils.getEntitiesInRange(location, ultimateRadius).forEach(entity -> {
                    if (entity == player) {
                        return;
                    }

                    GamePlayer.damageEntity(entity, 25.0d, player);
                    PlayerLib.playSound(entity.getLocation(), Sound.ENTITY_GENERIC_BIG_FALL, 0.75f);
                    PlayerLib.playSound(entity.getLocation(), Sound.ENTITY_GENERIC_HURT, 1.25f);

                    addRiptide(player, entity);
                });

                // Fx
                Geometry.drawSphere(location, 10, ultimateRadius, new WorldParticle(Particle.BUBBLE_POP));
                PlayerLib.playSound(location, Sound.AMBIENT_UNDERWATER_EXIT, 0.0f);

            }
        }.runTaskLater(10);
    }

    @Override
    public Talent getFirstTalent() {
        return Talents.STANCE.getTalent();
    }

    @Override
    public Talent getSecondTalent() {
        return null;
    }

    @Override
    public Talent getPassiveTalent() {
        return Talents.RIPTIDE.getTalent();
    }
}
