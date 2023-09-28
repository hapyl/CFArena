package me.hapyl.fight.game.heroes.archive.zealot;

import com.google.common.collect.Maps;
import me.hapyl.fight.event.io.DamageInput;
import me.hapyl.fight.event.io.DamageOutput;
import me.hapyl.fight.game.heroes.Archetype;
import me.hapyl.fight.game.heroes.DisabledHero;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.archive.zealot.MalevolentHitshield;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import javax.annotation.Nullable;
import java.util.Map;

public class Zealot extends Hero implements DisabledHero {

    protected final Equipment abilityEquipment;
    protected final Map<Player, SoulsRebound> soulsReboundMap;

    @DisplayField private final double damageMultiplier = 0.8d;

    public Zealot() {
        super("Zealot");

        setArchetype(Archetype.HEXBANE);
        setItem("131530db74bac84ad9e322280c56c4e0199fbe879883b76c9cf3fd8ff19cf025");
        setWeapon(new ZealotWeapon(this));

        final Equipment equipment = getEquipment();
        equipment.setChestPlate(104, 166, 232, TrimPattern.SILENCE, TrimMaterial.DIAMOND);
        equipment.setLeggings(Material.DIAMOND_LEGGINGS, TrimPattern.SILENCE, TrimMaterial.DIAMOND);
        equipment.setBoots(Material.DIAMOND_BOOTS, TrimPattern.SILENCE, TrimMaterial.DIAMOND);

        abilityEquipment = new Equipment();
        abilityEquipment.setHelmet(getItem());
        abilityEquipment.setChestPlate(104, 166, 232, TrimPattern.SILENCE, TrimMaterial.GOLD);
        abilityEquipment.setLeggings(Material.GOLDEN_LEGGINGS, TrimPattern.SILENCE, TrimMaterial.GOLD);
        abilityEquipment.setBoots(Material.GOLDEN_BOOTS, TrimPattern.RIB, TrimMaterial.GOLD);

        soulsReboundMap = Maps.newConcurrentMap();

        setUltimate(new UltimateTalent("???", """
                """, 70).setItem(Material.SOUL_SAND).setDurationSec(5));
    }

    @Nullable
    @Override
    public DamageOutput processDamageAsDamager(DamageInput input) {
        return DamageOutput.OK;
    }

    @Nullable
    @Override
    public DamageOutput processDamageAsVictim(DamageInput input) {
        final MalevolentHitshield shield = getSecondTalent();
        final Player player = input.getBukkitPlayer();

        // Cancel even if internal cooldown
        if (player.hasCooldown(shield.cooldownItem)) {
            return DamageOutput.CANCEL;
        }

        if (shield.hasCharge(player)) {
            shield.reduce(player);
            return DamageOutput.CANCEL;
        }

        return DamageOutput.OK;
    }

    @Override
    public void onStop() {
    }

    @Override
    public void onDeath(Player player) {
    }

    @Override
    public void useUltimate(Player player) {
    }

    @Override
    public Talent getFirstTalent() {
        return Talents.BROKEN_HEART_RADIATION.getTalent();
    }

    @Override
    public MalevolentHitshield getSecondTalent() {
        return (MalevolentHitshield) Talents.MALEVOLENT_HITSHIELD.getTalent();
    }

    @Override
    public Talent getPassiveTalent() {
        return null;
    }
}
