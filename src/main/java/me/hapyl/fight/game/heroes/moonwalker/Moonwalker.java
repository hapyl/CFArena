package me.hapyl.fight.game.heroes.moonwalker;

import me.hapyl.fight.database.key.DatabaseKey;
import me.hapyl.fight.game.Disabled;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.EquipmentSlots;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.moonwalker.MoonPassive;
import me.hapyl.fight.game.talents.moonwalker.MoonPillarTalent;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.game.ui.UIComponent;
import me.hapyl.fight.util.collection.player.PlayerDataMap;
import me.hapyl.fight.util.collection.player.PlayerMap;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Moonwalker extends Hero implements Disabled, PlayerDataHandler<MoonwalkerData>, UIComponent {

    private final PlayerDataMap<MoonwalkerData> playerData = PlayerMap.newDataMap(MoonwalkerData::new);

    public Moonwalker(@Nonnull DatabaseKey key) {
        super(key, "Moonwalker");

        setMinimumLevel(3);
        setArchetypes(Archetype.RANGE);
        setAffiliation(Affiliation.SPACE);
        setGender(Gender.MALE);
        setRace(Race.ALIEN);

        setDescription("A traveler from another planet... or, should I say moon? Brings his skills and... planets... with himself!");
        setItem("1cf8fbd76586920c5273519927862fdc111705a1851d4d1aac450bcfd2b3a");

        final HeroAttributes attributes = getAttributes();

        final Equipment equipment = getEquipment();
        equipment.setChestPlate(255, 255, 255);
        equipment.setLeggings(186, 186, 186);
        equipment.setBoots(45, 28, 77);

        setWeapon(new MoonwalkerWeapon(this));
        setUltimate(new MoonwalkerUltimate());
    }

    @Override
    public void onStart(@Nonnull GameInstance instance) {
        new TickingGameTask() {
            @Override
            public void run(int tick) {
                playerData.values().forEach(MoonwalkerData::tick);
            }
        }.runTaskTimer(0, 1);
    }

    @Override
    public MoonwalkerWeapon getWeapon() {
        return (MoonwalkerWeapon) super.getWeapon();
    }

    @Nonnull
    @Override
    public MoonwalkerUltimate getUltimate() {
        return (MoonwalkerUltimate) super.getUltimate();
    }

    @Override
    public MoonPillarTalent getFirstTalent() {
        return (MoonPillarTalent) Talents.MOONSLITE_PILLAR.getTalent();
    }

    @Override
    public Talent getSecondTalent() {
        return Talents.MOON_GRAVITY.getTalent();
    }

    @Override
    public MoonPassive getPassiveTalent() {
        return (MoonPassive) Talents.MOON_PASSIVE.getTalent();
    }

    @Nullable
    public Block getTargetBlock(GamePlayer player) {
        return player.getTargetBlockExact(25);
    }

    @Override
    public void onStart(@Nonnull GamePlayer player) {
        player.setItem(EquipmentSlots.ARROW, new ItemStack(Material.ARROW));
        player.addEffect(Effects.SLOW_FALLING, 2, -1);
    }

    @Nonnull
    @Override
    public String getString(@Nonnull GamePlayer player) {
        final MoonwalkerData data = getPlayerData(player);

        return "&e☄ &6" +data.weaponEnergy;
    }

    @Nonnull
    @Override
    public PlayerDataMap<MoonwalkerData> getDataMap() {
        return playerData;
    }
}
