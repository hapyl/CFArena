package me.hapyl.fight.game.heroes.archive.orc;

import com.google.common.collect.Maps;
import me.hapyl.fight.CF;
import me.hapyl.fight.event.io.DamageInput;
import me.hapyl.fight.event.io.DamageOutput;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Archetype;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.Equipment;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.archive.orc.OrcAxe;
import me.hapyl.fight.game.talents.archive.orc.OrcGrowl;
import me.hapyl.fight.game.task.PlayerTask;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.math.Tick;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public class Orc extends Hero {

    private final Map<Player, DamageData> damageMap = Maps.newHashMap();

    public Orc() {
        super("Pakarat Rakab");

        setArchetype(Archetype.DAMAGE);

        final HeroAttributes attributes = getAttributes();
        attributes.setValue(AttributeType.MAX_HEALTH, 150);
        attributes.setValue(AttributeType.DEFENSE, 0.6d);
        attributes.setValue(AttributeType.SPEED, 0.22d);
        attributes.setValue(AttributeType.CRIT_CHANCE, 0.15d);

        setWeapon(new OrcWeapon());

        setItem("a06220fdfef4d53da8bcef8cbef9a8a3add3d776de43a3781b2f58869ce3d738");

        final Equipment equipment = getEquipment();
        equipment.setChestplate(138, 140, 133, TrimPattern.RIB, TrimMaterial.QUARTZ);
        equipment.setLeggings(20, 19, 51);
        equipment.setBoots(Material.NETHERITE_BOOTS);

        setUltimate(
                new UltimateTalent("Berserk", 70)
                        .setDurationSec(20)
                        .setCooldown(30)
                        .setItem(Material.NETHER_WART)
                        .appendDescription(
                                """
                                        Enter %s for {duration}.
                                                                        
                                        While active, gain:
                                        • &aIncreased %s.
                                        • &aIncreased %s.
                                        • &aIncreased %s.
                                        • &c-70 %s.
                                        """,
                                Named.BERSERK,
                                AttributeType.ATTACK,
                                AttributeType.SPEED,
                                AttributeType.CRIT_CHANCE,
                                AttributeType.DEFENSE
                        )
        );
    }

    @Nullable
    @Override
    public DamageOutput processDamageAsVictim(DamageInput input) {
        final Player player = input.getEntityAsPlayer().getPlayer();
        final EnumDamageCause cause = input.getDamageCauseOr(EnumDamageCause.NONE);
        final double damage = input.getDamage();

        switch (cause) {
            case WITHER, POISON -> {
                return new DamageOutput(damage / 2);
            }
        }

        if (cause != EnumDamageCause.ENTITY_ATTACK) {
            return null;
        }

        if (damageMap.computeIfAbsent(player, DamageData::new).addHitAndCheck()) {
            enterBerserk(player, Tick.fromSecond(3));
        }

        return null;
    }

    @Override
    public void useUltimate(Player player) {
        enterBerserk(player, getUltimateDuration());
    }

    @Override
    public void onDeath(Player player) {
        if (!(getWeapon() instanceof OrcWeapon orcWeapon)) {
            return;
        }

        damageMap.remove(player);
        orcWeapon.remove(player);
    }

    public void enterBerserk(Player player, int duration) {
        final GamePlayer gamePlayer = CF.getOrCreatePlayer(player);
        final EntityAttributes attributes = gamePlayer.getAttributes();

        Temper.BERSERK_MODE.temper(attributes, duration);

        // Fx
        new PlayerTask(player) {
            private int tick = 0;

            @Override
            public void onTaskStop() {
                Chat.sendMessage(player, "%s &ais over!", Named.BERSERK);
            }

            @Override
            public void run(@Nonnull GamePlayer player) {
                if (tick++ >= duration) {
                    cancel();
                    return;
                }

                final Location location = player.getEyeLocation();

                // Sound FX
                if (tick % 20 == 0) {
                    PlayerLib.playSound(location, Sound.ENTITY_PIGLIN_AMBIENT, 0.75f);
                    PlayerLib.playSound(location, Sound.ENTITY_PIGLIN_ANGRY, 1.25f);
                }

                // Particle FX
                if (tick % 5 == 0) {
                    PlayerLib.spawnParticle(location, Particle.LAVA, 2, 0.1, 0.2, 0.1, 0.1f);
                }
            }
        }.runTaskTimer(0, 1);
    }

    @Override
    public void onStop() {
        if (getWeapon() instanceof OrcWeapon weapon) {
            weapon.removeAll();
        }
        damageMap.clear();
    }

    @Override
    public OrcGrowl getFirstTalent() {
        return (OrcGrowl) Talents.ORC_GROWN.getTalent();
    }

    @Override
    public OrcAxe getSecondTalent() {
        return (OrcAxe) Talents.ORC_AXE.getTalent();
    }

    @Override
    public Talent getPassiveTalent() {
        return Talents.ORC_PASSIVE.getTalent();
    }
}
