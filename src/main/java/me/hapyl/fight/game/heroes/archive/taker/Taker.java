package me.hapyl.fight.game.heroes.archive.taker;

import com.google.common.collect.Maps;
import me.hapyl.fight.event.DamageInput;
import me.hapyl.fight.event.DamageOutput;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.archive.taker.DeathSwap;
import me.hapyl.fight.game.talents.archive.taker.FatalReap;
import me.hapyl.fight.game.talents.archive.taker.SpiritualBonesPassive;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.ui.UIComponent;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.Utils;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.fight.util.displayfield.DisplayFieldProvider;
import me.hapyl.fight.util.displayfield.DisplayFieldSerializer;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.locaiton.LocationHelper;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Taker extends Hero implements UIComponent, NewHero, DisplayFieldProvider {

    private final Map<Player, SpiritualBones> playerBones = Maps.newHashMap();

    @DisplayField private final double ultimateProjectileSpeed = 0.5d;
    @DisplayField private final double ultimateProjectileDistance = 10.0d;
    @DisplayField private final int shotsPerBone = 3;
    @DisplayField private final double healingPerBone = 5d;

    public Taker() {
        super("Taker", "Will take your life away!");

        setRole(Role.MELEE);
        setArchetype(Archetype.DAMAGE);

        setItem("ff1e554161bd4b2ce4cad18349fd756994f74cabf1fd1dacdf91b6d05dffaf");

        final HeroEquipment equipment = getEquipment();
        equipment.setChestplate(28, 28, 28);
        equipment.setLeggings(0, 0, 0, TrimPattern.SILENCE, TrimMaterial.QUARTZ);
        equipment.setBoots(28, 28, 28, TrimPattern.SILENCE, TrimMaterial.QUARTZ);

        setWeapon(Material.IRON_HOE, "Scythe", 6.66d);

        final UltimateTalent ultimate = new UltimateTalent(
                "Embodiment of Death", """
                Instantly consume all &eSpiritual Bones&7 and cloak yourself in darkness for {duration}.
                                        
                While cloaked, become invulnerable.
                                        
                The darkness force will constantly rush forward, dealing damage and blinding anyone who dare to stay in the way.
                Also recover health every time enemy is hit.
                                        
                Hold &e&lSNEAK&7 to rush slower.
                                
                &6;;The damage and healing is scaled with &eSpiritual Bones&6 consumed.
                """, 70
        ).setDurationSec(4).setSound(Sound.ENTITY_HORSE_DEATH, 0.0f).setItem(Material.WITHER_SKELETON_SKULL).setCdFromCost(2);

        DisplayFieldSerializer.copy(this, ultimate);

        setUltimate(ultimate);
    }

    @Override
    public boolean predicateUltimate(Player player) {
        return getBones(player).getBones() > 0;
    }

    @Override
    public String predicateMessage(Player player) {
        return "Not enough &l%s&c!".formatted(getPassiveTalent().getName());
    }

    @Nonnull
    public SpiritualBones getBones(Player player) {
        return playerBones.computeIfAbsent(player, SpiritualBones::new);
    }

    @Override
    public void onStop() {
        playerBones.values().forEach(SpiritualBones::clearArmorStands);
        playerBones.clear();
    }

    @Override
    public void onDeath(Player player) {
        getBones(player).reset();
        getBones(player).clearArmorStands();
    }

    @Override
    public void onPlayersReveal() {
        new GameTask() {
            @Override
            public void run() {
                playerBones.values().forEach(SpiritualBones::tick);
            }
        }.runTaskTimer(0, 1);
    }

    @Override
    public void onUltimateEnd(Player player) {
        player.setInvulnerable(false);
    }

    @Override
    public void useUltimate(Player player) {
        player.setInvulnerable(true);

        final SpiritualBones bones = getBones(player);
        final int bonesAmount = bones.getBones();
        final double speed = 0.45d;
        final int hitDelay = 10;

        final double damage = 5.0d + bonesAmount;
        final double healing = 1.0d + bonesAmount;

        bones.reset();
        bones.clearArmorStands();

        GameTask.runDuration(getUltimate(), (task, i) -> {
            final boolean sneaking = player.isSneaking();

            player.setVelocity(player.getLocation()
                    .getDirection()
                    .normalize()
                    .multiply(sneaking ? speed / 2 : speed)
                    .add(new Vector(0.0d, BukkitUtils.GRAVITY, 0.0d)));

            // Damage
            if (i % hitDelay == 0) {
                final Location hitLocation = LocationHelper.getInFront(player.getEyeLocation(), 1.5d);

                Collect.nearbyLivingEntities(
                        hitLocation,
                        2.0d,
                        living -> Utils.isEntityValid(living, player)
                ).forEach(entity -> {
                    GamePlayer.damageEntityTick(entity, damage, player, EnumDamageCause.EMBODIMENT_OF_DEATH, hitDelay);
                    GamePlayer.getPlayer(player).heal(healing);
                });

                // Hit Fx
                PlayerLib.spawnParticle(hitLocation, Particle.SWEEP_ATTACK, 20, 1, 1, 1, 0.0f);
                PlayerLib.spawnParticle(hitLocation, Particle.ASH, 20, 1, 1, 1, 0.0f);
                PlayerLib.spawnParticle(hitLocation, Particle.SPELL_MOB, 20, 1, 1, 1, 0.0f);

                PlayerLib.playSound(hitLocation, Sound.ITEM_TRIDENT_THROW, 0.0f);
                PlayerLib.playSound(hitLocation, Sound.ENTITY_WITHER_HURT, 0.75f);
            }

            // Instant Fx
            PlayerLib.spawnParticle(player.getEyeLocation(), Particle.SQUID_INK, 5, 0.03125d, 0.6d, 0.03125d, 0.01f);
            PlayerLib.spawnParticle(player.getEyeLocation(), Particle.LAVA, 2, 0.03125d, 0.6d, 0.03125d, 0.01f);

        }, 0, 1);

        // Outer Fx
        Chat.sendMessage(player, "&0☠ &7The darkness will aid you with %s damage and %s healing per enemy hit.", damage, healing);
    }

    public void useUltimateOld(Player player) {
        final SpiritualBones bones = getBones(player);
        final UltimateTalent ultimate = getUltimate();
        final int playerBones = bones.getBones();
        final int duration = ultimate.getDuration();

        final double healing = healingPerBone * playerBones;
        final double healingPerTick = healing / duration;
        final int firePeriod = getUltimateDuration() / (playerBones * shotsPerBone);

        PlayerLib.addEffect(player, PotionEffectType.SLOW, duration, 4);
        PlayerLib.addEffect(player, PotionEffectType.DAMAGE_RESISTANCE, duration, 4);

        GameTask.runDuration(ultimate, (task, i) -> {
            GamePlayer.getPlayer(player).heal(healingPerTick);

            if (i > 0 && i % firePeriod == 0) {
                launchUltimateProjectile(player);
            }

            final double particleOffset = Utils.scaleParticleOffset(0.5d);

            PlayerLib.spawnParticle(player.getLocation(), Particle.SQUID_INK, 5, particleOffset, 0.6d, particleOffset, 0.01f);
            // Fx
            PlayerLib.spawnParticle(player.getLocation(), Particle.LAVA, 2, particleOffset, 0.6d, particleOffset, 0.01f);
        }, 0, 1);

        bones.reset();
        bones.clearArmorStands();
    }


    public void launchUltimateProjectile(Player player) {
        final Location location = player.getEyeLocation();
        final Vector vector = location.getDirection().normalize();

        vector.setY(0.0d);
        vector.setX(new Random().nextDouble(-1.0d, 1.0d));
        vector.setZ(new Random().nextDouble(-1.0d, 1.0d));

        new GameTask() {
            private double distance = 0.0d;

            @Override
            public void run() {
                if (distance >= (ultimateProjectileDistance * ultimateProjectileSpeed)) {
                    cancel();
                    return;
                }

                final double x = vector.getX() * distance;
                final double y = vector.getY() * distance;
                final double z = vector.getZ() * distance;

                location.add(x, y, z);

                Collect.nearbyLivingEntities(location, 1.0d, entity -> entity != player)
                        .forEach(entity -> GamePlayer.damageEntity(entity, 10.0d, player, EnumDamageCause.DEATH_RAY));

                // Fx
                PlayerLib.spawnParticle(location, Particle.SQUID_INK, 1, 0.0d, 0.0d, 0.0d, 0.0f);
                PlayerLib.spawnParticle(location, Particle.LAVA, 1, 0.0d, 0.0d, 0.0d, 0.0f);

                distance += ultimateProjectileSpeed;
            }
        }.runTaskTimer(0, 1);

        // Fx
        PlayerLib.playSound(location, Sound.ENTITY_BLAZE_SHOOT, 0.0f);
    }

    @Nullable
    @Override
    public DamageOutput processDamageAsDamager(DamageInput input) {
        final Player player = input.getPlayer();
        final SpiritualBones bones = getBones(player);

        if (bones.getBones() == 0) {
            return null;
        }

        final double healing = bones.getHealing();
        final double damage = input.getDamage();

        final double healingScaled = damage * healing / 100.0d;
        GamePlayer.getPlayer(player).heal(healingScaled);

        return new DamageOutput(damage + (damage / 10 * bones.getDamageMultiplier()));
    }

    @Nullable
    @Override
    public DamageOutput processDamageAsVictim(DamageInput input) {
        final Player player = input.getPlayer();
        final SpiritualBones bones = getBones(player);

        if (bones.getBones() == 0) {
            return null;
        }

        final double damage = input.getDamage();

        return new DamageOutput(damage - (damage / 100 * bones.getDamageReduction()));
    }

    @Override
    public FatalReap getFirstTalent() {
        return (FatalReap) Talents.FATAL_REAP.getTalent();
    }

    @Override
    public DeathSwap getSecondTalent() {
        return (DeathSwap) Talents.DEATH_SWAP.getTalent();
    }

    @Override
    public SpiritualBonesPassive getPassiveTalent() {
        return (SpiritualBonesPassive) Talents.SPIRITUAL_BONES.getTalent();
    }

    @Nonnull
    @Override
    public String getString(Player player) {
        return "&f☠: &l" + getBones(player).getBones();
    }

    @Override
    public long until() {
        return 1680207036792L + (TimeUnit.DAYS.toMillis(10));
    }
}
