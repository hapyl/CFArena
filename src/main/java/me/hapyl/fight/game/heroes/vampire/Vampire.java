package me.hapyl.fight.game.heroes.vampire;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.MapMaker;
import me.hapyl.fight.event.custom.GameDamageEvent;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.entity.BloodDebt;
import me.hapyl.fight.game.entity.GameEntity;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.heroes.ultimate.UltimateInstance;
import me.hapyl.fight.game.heroes.ultimate.UltimateTalent;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.talents.vampire.BatTransferTalent;
import me.hapyl.fight.game.talents.vampire.BloodDebtTalent;
import me.hapyl.fight.game.talents.vampire.VampirePassive;
import me.hapyl.fight.game.weapons.Weapon;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import javax.annotation.Nonnull;
import java.util.Map;

public class Vampire extends Hero implements Listener {

    public Vampire(@Nonnull Key key) {
        super(key, "Vorath");

        setDescription("""
                One of the royal guards at the %s&8&o, believes that with enough firepower, &oeverything&8&o is possible.
                
                Prefers NoSunBurn™ sunscreen.
                """.formatted(Affiliation.CHATEAU));

        final HeroProfile profile = getProfile();
        profile.setArchetypes(Archetype.DAMAGE, Archetype.SELF_SUSTAIN, Archetype.SELF_BUFF);
        profile.setAffiliation(Affiliation.CHATEAU);
        profile.setGender(Gender.MALE);
        profile.setRace(Race.VAMPIRE);

        setItem("8d44756e0b4ece8d746296a3d5e297e1415f4ba17647ffe228385383d161a9");

        final HeroAttributes attributes = getAttributes();
        attributes.setMaxHealth(90);

        final Equipment equipment = getEquipment();
        equipment.setChestPlate(25, 25, 25, TrimPattern.RIB, TrimMaterial.NETHERITE);
        equipment.setLeggings(254, 253, 252, TrimPattern.SILENCE, TrimMaterial.IRON);
        equipment.setBoots(25, 25, 25, TrimPattern.SILENCE, TrimMaterial.NETHERITE);

        setWeapon(Weapon.builder(Material.GHAST_TEAR, Key.ofString("vampires_fang"))
                .name("Vampire's Fang")
                .description("""
                        A very sharp fang.
                        """)
                .damage(5.0d)
        );

        setUltimate(new VampireUltimate());
    }

    @EventHandler
    public void handleGameDamageEvent(GameDamageEvent ev) {
        final GameEntity damager = ev.getDamager();

        if (!(damager instanceof GamePlayer player) || !validatePlayer(player)) {
            return;
        }

        final BloodDebt bloodDebt = player.bloodDebt();

        if (!bloodDebt.hasDebt()) {
            return;
        }

        // Increase damage based on blood debt
        final double bloodDebtAmount = bloodDebt.amount();
        final double decrement = Math.min(bloodDebtAmount, player.getMaxHealth() * 0.15d);
        final double damageIncrease = 1 + bloodDebtAmount * TalentRegistry.BLOOD_DEBT.damageIncreaseMultiplier;

        bloodDebt.decrement(decrement);
        ev.multiplyDamage(damageIncrease);
    }

    @Override
    public BloodDebtTalent getFirstTalent() {
        return TalentRegistry.BLOOD_DEBT;
    }

    @Override
    public BatTransferTalent getSecondTalent() {
        return TalentRegistry.BAT_TRANSFER;
    }

    @Override
    public VampirePassive getPassiveTalent() {
        return TalentRegistry.VANPIRE_PASSIVE;
    }

    private class VampireUltimate extends UltimateTalent {

        // TODO (Mon, Dec 9 2024 @xanyjl): Move to passive I guess
        private final Map<AttributeType, Double> legionAttributes = MapMaker.<AttributeType, Double>ofLinkedHashMap()
                .put(AttributeType.MAX_HEALTH, 5.0d)
                .put(AttributeType.ATTACK, 10.0d)
                .put(AttributeType.CRIT_DAMAGE, 1.0d)
                .put(AttributeType.SPEED, 5.0d)
                .put(AttributeType.ATTACK_SPEED, 3.0d)
                .makeMap();

        public VampireUltimate() {
            super(Vampire.this, "", 50);

            setDescription("""
                    """);
        }

        @Nonnull
        @Override
        public UltimateInstance newInstance(@Nonnull GamePlayer player) {
            return execute(() -> {
            });
        }

    }
}
