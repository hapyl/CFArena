package me.hapyl.fight.game.heroes.vampire;

import me.hapyl.fight.database.key.DatabaseKey;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.ui.UIComplexComponent;
import me.hapyl.fight.util.collection.player.PlayerDataMap;
import me.hapyl.fight.util.collection.player.PlayerMap;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.util.List;

public class Vampire extends Hero implements Listener, UIComplexComponent, PlayerDataHandler<VampireData> {

    private final PlayerDataMap<VampireData> vampireData = PlayerMap.newDataMap(VampireData::new);

    public Vampire(@Nonnull DatabaseKey key) {
        super(key, "Vampire");

        setArchetypes(Archetype.DAMAGE, Archetype.SELF_SUSTAIN, Archetype.HEXBANE);
        setGender(Gender.MALE);
        setRace(Race.VAMPIRE);
        setAffiliation(Affiliation.CHATEAU);

        setItem("8d44756e0b4ece8d746296a3d5e297e1415f4ba17647ffe228385383d161a9");

        final Equipment equipment = getEquipment();
        equipment.setChestPlate(Color.BLACK);
        equipment.setLeggings(Color.BLACK);
        equipment.setBoots(Color.BLACK);

        setWeapon(Material.GHAST_TEAR, "Vampire's Fang", 5.0d);

        setUltimate(new VampireUltimate());
    }

    @Override
    public Talent getFirstTalent() {
        return TalentRegistry.BLOODSHIFT;
    }

    @Override
    public Talent getSecondTalent() {
        return TalentRegistry.BAT_SWARM;
    }

    @Override
    public Talent getPassiveTalent() {
        return TalentRegistry.BLOOD_THIRST;
    }

    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        vampireData.remove(player);
    }

    @Override
    public void processDamageAsDamager(@Nonnull DamageInstance instance) {
        final GamePlayer player = instance.getDamagerAsPlayer();
        final VampireData data = getData(player);

        if (player == null) {
            return;
        }

    }

    @Override
    public void onPlayersRevealed(@Nonnull GameInstance instance) {
    }

    public VampireData getData(GamePlayer player) {
        return vampireData.computeIfAbsent(player, key -> new VampireData(player));
    }

    @Override
    public List<String> getStrings(@Nonnull GamePlayer player) {
        return List.of();
    }

    @Nonnull
    @Override
    public PlayerDataMap<VampireData> getDataMap() {
        return vampireData;
    }

    private class VampireUltimate extends UltimateTalent {

        public VampireUltimate() {
            super(Vampire.this, "Vampire Ultimate", 5);
        }

        @Nonnull
        @Override
        public UltimateResponse useUltimate(@Nonnull GamePlayer player) {
            return UltimateResponse.OK;
        }
    }
}
