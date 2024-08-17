package me.hapyl.fight.game.heroes.zealot;

import me.hapyl.fight.database.key.DatabaseKey;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.zealot.BrokenHeartRadiation;
import me.hapyl.fight.game.talents.zealot.FerociousStrikes;
import me.hapyl.fight.game.talents.zealot.MalevolentHitshield;
import me.hapyl.fight.game.ui.UIComponent;
import me.hapyl.fight.util.collection.player.PlayerDataMap;
import me.hapyl.fight.util.collection.player.PlayerMap;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import javax.annotation.Nonnull;

public class Zealot extends Hero implements Listener, PlayerDataHandler<ZealotData>, UIComponent {

    protected final Equipment abilityEquipment;
    private final PlayerDataMap<ZealotData> zealotData = PlayerMap.newDataMap(ZealotData::new);

    public Zealot(@Nonnull DatabaseKey key) {
        super(key, "Zealot");

        setDescription("""
                A space ranger with a single goal of maintaining order.
                """);

        setArchetypes(Archetype.DAMAGE);
        setAffiliation(Affiliation.SPACE);
        setGender(Gender.MALE);
        setRace(Race.ALIEN);

        setItem("131530db74bac84ad9e322280c56c4e0199fbe879883b76c9cf3fd8ff19cf025");
        setWeapon(new ZealotWeapon(this));

        final HeroAttributes attributes = getAttributes();
        attributes.setFerocity(25);

        final Equipment equipment = getEquipment();
        equipment.setChestPlate(104, 166, 232, TrimPattern.SILENCE, TrimMaterial.DIAMOND);
        equipment.setLeggings(Material.DIAMOND_LEGGINGS, TrimPattern.SILENCE, TrimMaterial.DIAMOND);
        equipment.setBoots(Material.DIAMOND_BOOTS, TrimPattern.SILENCE, TrimMaterial.DIAMOND);

        abilityEquipment = new Equipment();
        abilityEquipment.setHelmet(getItem());
        abilityEquipment.setChestPlate(104, 166, 232, TrimPattern.SILENCE, TrimMaterial.GOLD);
        abilityEquipment.setLeggings(Material.GOLDEN_LEGGINGS, TrimPattern.SILENCE, TrimMaterial.GOLD);
        abilityEquipment.setBoots(Material.GOLDEN_BOOTS, TrimPattern.RIB, TrimMaterial.GOLD);

        setUltimate(new ZealotUltimate(this));
    }

    @Override
    public void processDamageAsDamager(@Nonnull DamageInstance instance) {
        final GamePlayer player = instance.getDamagerAsPlayer();
        final EnumDamageCause cause = instance.getCause();

        if (player == null || cause != EnumDamageCause.FEROCITY || player.isUsingUltimate()) {
            return;
        }

        final ZealotData data = getPlayerData(player);
        data.incrementFerociousHits();
    }

    @Nonnull
    @Override
    public ZealotUltimate getUltimate() {
        return (ZealotUltimate) super.getUltimate();
    }

    @Override
    public BrokenHeartRadiation getFirstTalent() {
        return (BrokenHeartRadiation) Talents.BROKEN_HEART_RADIATION.getTalent();
    }

    @Override
    public MalevolentHitshield getSecondTalent() {
        return (MalevolentHitshield) Talents.MALEVOLENT_HITSHIELD.getTalent();
    }

    @Override
    public FerociousStrikes getPassiveTalent() {
        return (FerociousStrikes) Talents.FEROCIOUS_STRIKES.getTalent();
    }

    @Nonnull
    @Override
    public PlayerDataMap<ZealotData> getDataMap() {
        return zealotData;
    }

    @Nonnull
    @Override
    public String getString(@Nonnull GamePlayer player) {
        final ZealotData data = getPlayerData(player);
        final int ferociousHits = data.ferociousHits;
        final boolean isMaxed = ferociousHits == FerociousStrikes.maxStrikes;

        return "&4%s %s%s".formatted(Named.FEROCIOUS_STRIKE.getCharacter(), ferociousHits, isMaxed ? " &lMAX!" : "");
    }
}
