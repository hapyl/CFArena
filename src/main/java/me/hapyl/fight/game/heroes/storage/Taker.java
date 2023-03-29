package me.hapyl.fight.game.heroes.storage;

import com.google.common.collect.Maps;
import me.hapyl.fight.event.DamageInput;
import me.hapyl.fight.event.DamageOutput;
import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.heroes.ClassEquipment;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.storage.extra.SpiritualBones;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.storage.taker.SpiritualBonesPassive;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.ui.UIComponent;
import me.hapyl.fight.util.Utils;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public class Taker extends Hero implements UIComponent {

    private final Map<Player, SpiritualBones> playerBones = Maps.newHashMap();

    public Taker() {
        super("Taker", "Will take your life away!");

        setItem("ff1e554161bd4b2ce4cad18349fd756994f74cabf1fd1dacdf91b6d05dffaf");

        final ClassEquipment equipment = getEquipment();
        equipment.setChestplate(Color.BLACK);
        equipment.setLeggings(Color.BLACK);
        equipment.setBoots(Color.BLACK);

        setWeapon(Material.IRON_HOE, "Scythe", 6.66d);

        setUltimate(new UltimateTalent(
                "Embodiment of Death",
                "Instantly consume all &eSpiritual Bones&7 and cloak yourself in darkness for {duration}.____While cloaked, become invulnerable, gain moderate speed boost and heal yourself over {duration}. ____Healing is based on the amount of bones consumed.",
                60
        ).setDurationSec(2).setSound(Sound.ENTITY_HORSE_DEATH, 0.0f).setItem(Material.WITHER_SKELETON_SKULL).setCdFromDuration(2));
    }

    @Override
    public boolean predicateUltimate(Player player) {
        return getBones(player).getBones() > 0;
    }

    @Override
    public String predicateMessage() {
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

        final double healing = 2.5 * playerBones;
        final double healingPerTick = healing / ultimate.getDuration();

        PlayerLib.addEffect(player, PotionEffectType.SPEED, ultimate.getDuration(), 2);

        GameTask.runDuration(ultimate, task -> {
            GamePlayer.getPlayer(player).heal(healingPerTick);
            player.setInvulnerable(true);
            Utils.hidePlayer(player);

            // Fx
            PlayerLib.spawnParticle(player.getLocation(), Particle.SQUID_INK, 5, 0.2d, 0.6d, 0.2d, 0.01f);
        }, 1);

        bones.reset();
        bones.clearArmorStands();
    }

    @Override
    public void onUltimateEnd(Player player) {
        player.setInvulnerable(false);
        Utils.showPlayer(player);
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

        final double d = damage * healing / 100.0d;
        GamePlayer.getPlayer(player).heal(d);

        return new DamageOutput(damage + (damage * bones.getDamageMultiplier()));
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

        return new DamageOutput(damage - bones.getDamageReduction());
    }

    @Override
    public Talent getFirstTalent() {
        return Talents.FATAL_REAP.getTalent();
    }

    @Override
    public Talent getSecondTalent() {
        return null;
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
}
