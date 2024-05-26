package me.hapyl.fight.game.heroes.orc;

import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.attribute.temper.TemperInstance;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.heroes.UltimateResponse;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.orc.OrcAxe;
import me.hapyl.fight.game.talents.orc.OrcGrowl;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.task.player.PlayerGameTask;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.spigotutils.module.math.Tick;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import javax.annotation.Nonnull;

public class Orc extends Hero implements Listener {

    private final PlayerMap<DamageData> damageMap = PlayerMap.newMap();

    private final TemperInstance berserk = Temper.BERSERK_MODE.newInstance(Named.BERSERK.toString())
            .increase(AttributeType.ATTACK, 0.4d)
            .increase(AttributeType.SPEED, 0.05d)
            .increase(AttributeType.CRIT_CHANCE, 0.4d)
            .decrease(AttributeType.DEFENSE, 0.6d)
            .message(Named.BERSERK.getCharacter() + " &aYou're berserk!");

    public Orc(@Nonnull Heroes handle) {
        super(handle, "Pakarat Rakab");

        setDescription("""
                Half-orc half-dwarf loner.
                """);

        setArchetype(Archetype.DAMAGE);
        setGender(Gender.MALE);

        final HeroAttributes attributes = getAttributes();
        attributes.setHealth(120);
        attributes.setDefense(60);
        attributes.setSpeed(110);
        attributes.setCritChance(15);
        attributes.setEffectResistance(35);
        attributes.setAttackSpeed(60);

        setWeapon(new OrcWeapon());

        setItem("a06220fdfef4d53da8bcef8cbef9a8a3add3d776de43a3781b2f58869ce3d738");

        final Equipment equipment = getEquipment();
        equipment.setChestPlate(138, 140, 133, TrimPattern.RIB, TrimMaterial.QUARTZ);
        equipment.setLeggings(20, 19, 51);
        equipment.setBoots(Material.NETHERITE_BOOTS);

        setUltimate(new OrcUltimate());
    }

    @Override
    public void processDamageAsVictim(@Nonnull DamageInstance instance) {
        final GamePlayer player = instance.getEntityAsPlayer();
        final EnumDamageCause cause = instance.getCauseOr(EnumDamageCause.NONE);

        if (cause != EnumDamageCause.ENTITY_ATTACK) {
            return;
        }

        if (damageMap.computeIfAbsent(player, DamageData::new).addHitAndCheck()) {
            enterBerserk(player, Tick.fromSecond(3));
        }
    }

    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        if (!(getWeapon() instanceof OrcWeapon orcWeapon)) {
            return;
        }

        damageMap.remove(player);
        orcWeapon.remove(player);
    }

    public void enterBerserk(GamePlayer player, int duration) {
        final EntityAttributes attributes = player.getAttributes();
        berserk.temper(attributes, duration);

        // Fx
        new PlayerGameTask(player, Named.BERSERK) {
            private int tick = 0;

            @Override
            public void run() {
                if (tick++ >= duration) {
                    cancel();
                    return;
                }

                // Sound FX
                if (tick % 20 == 0) {
                    player.playWorldSound(Sound.ENTITY_PIGLIN_AMBIENT, 0.75f);
                    player.playWorldSound(Sound.ENTITY_PIGLIN_ANGRY, 1.25f);
                }

                // Particle FX
                if (tick % 5 == 0) {
                    player.spawnWorldParticle(Particle.LAVA, 2, 0.1, 0.2, 0.1, 0.1f);
                }
            }

            @Override
            public void onTaskStop() {
                player.sendMessage(Named.BERSERK + " &ais over!");
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

    private class OrcUltimate extends UltimateTalent {
        public OrcUltimate() {
            super("Berserk", 70);

            setDescription("""
                    Enter %s for {duration}.
                                                    
                    While active, gain:
                    • &aIncreased %s.
                    • &aIncreased %s.
                    • &aIncreased %s.
                    • &c%.0f %s.
                    """.formatted(Named.BERSERK,
                    AttributeType.ATTACK,
                    AttributeType.SPEED,
                    AttributeType.CRIT_CHANCE,
                    berserk.get(AttributeType.DEFENSE) * 100, AttributeType.DEFENSE
            ));

            setType(TalentType.ENHANCE);
            setItem(Material.NETHER_WART);
            setDurationSec(10);
            setCooldownSec(20);
        }

        @Nonnull
        @Override
        public UltimateResponse useUltimate(@Nonnull GamePlayer player) {
            enterBerserk(player, getUltimateDuration());

            return UltimateResponse.OK;
        }
    }
}
