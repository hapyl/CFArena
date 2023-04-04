package me.hapyl.fight.game.heroes.storage;

import com.google.common.collect.Maps;
import me.hapyl.fight.event.DamageInput;
import me.hapyl.fight.event.DamageOutput;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.heroes.ClassEquipment;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.NewHero;
import me.hapyl.fight.game.heroes.Role;
import me.hapyl.fight.game.heroes.storage.extra.SpiritualBones;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.storage.taker.DeathSwap;
import me.hapyl.fight.game.talents.storage.taker.FatalReap;
import me.hapyl.fight.game.talents.storage.taker.SpiritualBonesPassive;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.ui.UIComponent;
import me.hapyl.fight.util.Utils;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.fight.util.displayfield.DisplayFieldProvider;
import me.hapyl.fight.util.displayfield.DisplayFieldSerializer;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.*;
import org.bukkit.entity.Player;
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
    @DisplayField private double healingPerBone = 5d;

    public Taker() {
        super("Taker", "Will take your life away!");

        setRole(Role.MELEE);
        setItem("ff1e554161bd4b2ce4cad18349fd756994f74cabf1fd1dacdf91b6d05dffaf");

        final ClassEquipment equipment = getEquipment();
        equipment.setChestplate(Color.BLACK);
        equipment.setLeggings(Color.BLACK);
        equipment.setBoots(Color.BLACK);

        setWeapon(Material.IRON_HOE, "Scythe", 6.66d);

        final UltimateTalent ultimate = new UltimateTalent(
                "Embodiment of Death",
                "Instantly consume all &eSpiritual Bones&7 and cloak yourself in darkness for {duration}.____While cloaked, gain resistance and heal rapidly but suffer speed reduction.__Additionally, shoot death projectiles periodically.____&cHealing&7 and amount of &cdeath projectiles&7 is based on the amount of bones consumed.",
                60
        ).setDurationSec(3).setSound(Sound.ENTITY_HORSE_DEATH, 0.0f).setItem(Material.WITHER_SKELETON_SKULL).setCdFromCost(2);

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
    public void useUltimate(Player player) {
        final SpiritualBones bones = getBones(player);
        final UltimateTalent ultimate = getUltimate();
        final int playerBones = bones.getBones();
        final int duration = ultimate.getDuration();

        // FIXME (hapyl): 004, Apr 4, 2023: 5 bones shoots too fast?

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

            // Fx
            final double particleOffset = Utils.scaleParticleOffset(0.5d);

            PlayerLib.spawnParticle(player.getLocation(), Particle.SQUID_INK, 5, particleOffset, 0.6d, particleOffset, 0.01f);
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

                Utils.getEntitiesInRange(location, 1.0d, entity -> entity != player)
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
