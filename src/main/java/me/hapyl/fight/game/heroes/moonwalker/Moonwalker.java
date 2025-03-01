package me.hapyl.fight.game.heroes.moonwalker;

import me.hapyl.eterna.module.block.display.BDEngine;
import me.hapyl.eterna.module.block.display.DisplayData;
import me.hapyl.eterna.module.block.display.DisplayEntity;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Disabled;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.effect.EffectType;
import me.hapyl.fight.game.entity.EquipmentSlots;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.WarningType;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.HeroEquipment;
import me.hapyl.fight.game.heroes.ultimate.UltimateInstance;
import me.hapyl.fight.game.heroes.ultimate.UltimateTalent;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.talents.moonwalker.MoonPassive;
import me.hapyl.fight.game.talents.moonwalker.MoonPillarTalent;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.game.ui.UIComponent;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.collection.player.PlayerDataMap;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public class Moonwalker extends Hero implements Disabled, PlayerDataHandler<MoonwalkerData>, UIComponent {

    private final PlayerDataMap<MoonwalkerData> playerData = PlayerMap.newDataMap(MoonwalkerData::new);

    public Moonwalker(@Nonnull Key key) {
        super(key, "Moonwalker");

        setMinimumLevel(3);

        final HeroProfile profile = getProfile();
        profile.setArchetypes(Archetype.RANGE, Archetype.DEFENSE, Archetype.POWERFUL_ULTIMATE);
        profile.setAffiliation(Affiliation.SPACE);
        profile.setGender(Gender.MALE);
        profile.setRace(Race.ALIEN);

        setDescription("""
                A traveler from another planet... or rather, a moon?
                
                Brings his skills and... plants... with himself!
                """);

        setItem("1cf8fbd76586920c5273519927862fdc111705a1851d4d1aac450bcfd2b3a");

        final HeroAttributes attributes = getAttributes();

        final HeroEquipment equipment = getEquipment();
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

    @Nonnull
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
        return TalentRegistry.MOONSLITE_PILLAR;
    }

    @Override
    public Talent getSecondTalent() {
        return TalentRegistry.MOON_GRAVITY;
    }

    @Override
    public MoonPassive getPassiveTalent() {
        return TalentRegistry.MOON_PASSIVE;
    }

    @Nullable
    public Block getTargetBlock(GamePlayer player) {
        return player.getTargetBlockExact(25);
    }

    @Override
    public void onStart(@Nonnull GamePlayer player) {
        player.setItem(EquipmentSlots.ARROW, new ItemStack(Material.ARROW));
        player.addEffect(EffectType.SLOW_FALLING, 2, -1);
    }

    @Nonnull
    @Override
    public String getString(@Nonnull GamePlayer player) {
        final MoonwalkerData data = getPlayerData(player);
        final double energy = data.energy;

        return "&e☄ &6&l%.0f".formatted(energy) + (energy >= getPassiveTalent().maxEnergy ? " &c&lMAX!" : "");
    }

    @Nonnull
    @Override
    public PlayerDataMap<MoonwalkerData> getDataMap() {
        return playerData;
    }

    public class MoonwalkerUltimate extends UltimateTalent {

        private final DisplayData blob = BDEngine.parse(
                "/summon block_display ~-0.5 ~-0.5 ~-0.5 {Passengers:[{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_stairs\",Properties:{facing:\"east\",half:\"bottom\",shape:\"straight\"}},transformation:[1.0000f,0.0000f,0.0000f,-1.5000f,0.0000f,1.0000f,0.0000f,2.0000f,0.0000f,0.0000f,1.0000f,-0.5000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_stairs\",Properties:{facing:\"west\",half:\"bottom\",shape:\"straight\"}},transformation:[1.0000f,0.0000f,0.0000f,0.5000f,0.0000f,1.0000f,0.0000f,2.0000f,0.0000f,0.0000f,1.0000f,-0.5000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_stairs\",Properties:{facing:\"north\",half:\"bottom\",shape:\"straight\"}},transformation:[1.0000f,0.0000f,0.0000f,-0.5000f,0.0000f,1.0000f,0.0000f,2.0000f,0.0000f,0.0000f,1.0000f,0.5000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_stairs\",Properties:{facing:\"south\",half:\"bottom\",shape:\"straight\"}},transformation:[1.0000f,0.0000f,0.0000f,-0.5000f,0.0000f,1.0000f,0.0000f,2.0000f,0.0000f,0.0000f,1.0000f,-1.5000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_slab\",Properties:{type:\"bottom\"}},transformation:[1.0000f,0.0000f,0.0000f,-0.5000f,0.0000f,1.0000f,0.0000f,2.5000f,0.0000f,0.0000f,1.0000f,-0.5000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_stairs\",Properties:{facing:\"north\",half:\"bottom\",shape:\"inner_left\"}},transformation:[1.0000f,0.0000f,0.0000f,0.5000f,0.0000f,1.0000f,0.0000f,1.5000f,0.0000f,0.0000f,1.0000f,0.5000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_stairs\",Properties:{facing:\"east\",half:\"bottom\",shape:\"inner_left\"}},transformation:[1.0000f,0.0000f,0.0000f,-1.5000f,0.0000f,1.0000f,0.0000f,1.5000f,0.0000f,0.0000f,1.0000f,0.5000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_stairs\",Properties:{facing:\"south\",half:\"bottom\",shape:\"inner_left\"}},transformation:[1.0000f,0.0000f,0.0000f,-1.5000f,0.0000f,1.0000f,0.0000f,1.5000f,0.0000f,0.0000f,1.0000f,-1.5000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_stairs\",Properties:{facing:\"west\",half:\"bottom\",shape:\"inner_left\"}},transformation:[1.0000f,0.0000f,0.0000f,0.5000f,0.0000f,1.0000f,0.0000f,1.5000f,0.0000f,0.0000f,1.0000f,-1.5000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_slab\",Properties:{type:\"bottom\"}},transformation:[1.0000f,0.0000f,0.0000f,-0.5000f,0.0000f,1.0000f,0.0000f,2.0000f,0.0000f,0.0000f,1.0000f,1.0000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_slab\",Properties:{type:\"bottom\"}},transformation:[1.0000f,0.0000f,0.0000f,-2.0000f,0.0000f,1.0000f,0.0000f,2.0000f,0.0000f,0.0000f,1.0000f,-0.5000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_slab\",Properties:{type:\"bottom\"}},transformation:[1.0000f,0.0000f,0.0000f,-0.5000f,0.0000f,1.0000f,0.0000f,2.0000f,0.0000f,0.0000f,1.0000f,-2.0000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_slab\",Properties:{type:\"bottom\"}},transformation:[1.0000f,0.0000f,0.0000f,1.0000f,0.0000f,1.0000f,0.0000f,2.0000f,0.0000f,0.0000f,1.0000f,-0.5000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_stairs\",Properties:{facing:\"east\",half:\"bottom\",shape:\"straight\"}},transformation:[1.0000f,0.0000f,0.0000f,-1.5000f,0.0000f,-1.0000f,0.0000f,-2.0000f,0.0000f,-0.0000f,-1.0000f,0.5000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_stairs\",Properties:{facing:\"west\",half:\"bottom\",shape:\"straight\"}},transformation:[1.0000f,0.0000f,0.0000f,0.5000f,0.0000f,-1.0000f,0.0000f,-2.0000f,0.0000f,-0.0000f,-1.0000f,0.5000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_stairs\",Properties:{facing:\"north\",half:\"bottom\",shape:\"straight\"}},transformation:[1.0000f,0.0000f,0.0000f,-0.5000f,0.0000f,-1.0000f,0.0000f,-2.0000f,0.0000f,-0.0000f,-1.0000f,-0.5000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_stairs\",Properties:{facing:\"south\",half:\"bottom\",shape:\"straight\"}},transformation:[1.0000f,0.0000f,0.0000f,-0.5000f,0.0000f,-1.0000f,0.0000f,-2.0000f,0.0000f,-0.0000f,-1.0000f,1.5000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_slab\",Properties:{type:\"bottom\"}},transformation:[1.0000f,0.0000f,0.0000f,-0.5000f,0.0000f,-1.0000f,0.0000f,-2.5000f,0.0000f,-0.0000f,-1.0000f,0.5000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_stairs\",Properties:{facing:\"north\",half:\"bottom\",shape:\"inner_left\"}},transformation:[1.0000f,0.0000f,0.0000f,0.5000f,0.0000f,-1.0000f,0.0000f,-1.5000f,0.0000f,-0.0000f,-1.0000f,-0.5000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_stairs\",Properties:{facing:\"east\",half:\"bottom\",shape:\"inner_left\"}},transformation:[1.0000f,0.0000f,0.0000f,-1.5000f,0.0000f,-1.0000f,0.0000f,-1.5000f,0.0000f,-0.0000f,-1.0000f,-0.5000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_stairs\",Properties:{facing:\"south\",half:\"bottom\",shape:\"inner_left\"}},transformation:[1.0000f,0.0000f,0.0000f,-1.5000f,0.0000f,-1.0000f,0.0000f,-1.5000f,0.0000f,-0.0000f,-1.0000f,1.5000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_stairs\",Properties:{facing:\"west\",half:\"bottom\",shape:\"inner_left\"}},transformation:[1.0000f,0.0000f,0.0000f,0.5000f,0.0000f,-1.0000f,0.0000f,-1.5000f,0.0000f,-0.0000f,-1.0000f,1.5000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_slab\",Properties:{type:\"bottom\"}},transformation:[1.0000f,0.0000f,0.0000f,-0.5000f,0.0000f,-1.0000f,0.0000f,-2.0000f,0.0000f,-0.0000f,-1.0000f,-1.0000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_slab\",Properties:{type:\"bottom\"}},transformation:[1.0000f,0.0000f,0.0000f,-2.0000f,0.0000f,-1.0000f,0.0000f,-2.0000f,0.0000f,-0.0000f,-1.0000f,0.5000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_slab\",Properties:{type:\"bottom\"}},transformation:[1.0000f,0.0000f,0.0000f,-0.5000f,0.0000f,-1.0000f,0.0000f,-2.0000f,0.0000f,-0.0000f,-1.0000f,2.0000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_slab\",Properties:{type:\"bottom\"}},transformation:[1.0000f,0.0000f,0.0000f,1.0000f,0.0000f,-1.0000f,0.0000f,-2.0000f,0.0000f,-0.0000f,-1.0000f,0.5000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_stairs\",Properties:{facing:\"east\",half:\"bottom\",shape:\"straight\"}},transformation:[0.0000f,1.0000f,-0.0000f,2.0000f,1.0000f,-0.0000f,0.0000f,-1.5000f,0.0000f,-0.0000f,-1.0000f,0.5000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_stairs\",Properties:{facing:\"west\",half:\"bottom\",shape:\"straight\"}},transformation:[0.0000f,1.0000f,-0.0000f,2.0000f,1.0000f,-0.0000f,0.0000f,0.5000f,0.0000f,-0.0000f,-1.0000f,0.5000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_stairs\",Properties:{facing:\"north\",half:\"bottom\",shape:\"straight\"}},transformation:[0.0000f,1.0000f,-0.0000f,2.0000f,1.0000f,-0.0000f,0.0000f,-0.5000f,0.0000f,-0.0000f,-1.0000f,-0.5000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_stairs\",Properties:{facing:\"south\",half:\"bottom\",shape:\"straight\"}},transformation:[0.0000f,1.0000f,-0.0000f,2.0000f,1.0000f,-0.0000f,0.0000f,-0.5000f,0.0000f,-0.0000f,-1.0000f,1.5000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_slab\",Properties:{type:\"bottom\"}},transformation:[0.0000f,1.0000f,-0.0000f,2.5000f,1.0000f,-0.0000f,0.0000f,-0.5000f,0.0000f,-0.0000f,-1.0000f,0.5000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_stairs\",Properties:{facing:\"north\",half:\"bottom\",shape:\"inner_left\"}},transformation:[0.0000f,1.0000f,-0.0000f,1.5000f,1.0000f,-0.0000f,0.0000f,0.5000f,0.0000f,-0.0000f,-1.0000f,-0.5000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_stairs\",Properties:{facing:\"east\",half:\"bottom\",shape:\"inner_left\"}},transformation:[0.0000f,1.0000f,-0.0000f,1.5000f,1.0000f,-0.0000f,0.0000f,-1.5000f,0.0000f,-0.0000f,-1.0000f,-0.5000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_stairs\",Properties:{facing:\"south\",half:\"bottom\",shape:\"inner_left\"}},transformation:[0.0000f,1.0000f,-0.0000f,1.5000f,1.0000f,-0.0000f,0.0000f,-1.5000f,0.0000f,-0.0000f,-1.0000f,1.5000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_stairs\",Properties:{facing:\"west\",half:\"bottom\",shape:\"inner_left\"}},transformation:[0.0000f,1.0000f,-0.0000f,1.5000f,1.0000f,-0.0000f,0.0000f,0.5000f,0.0000f,-0.0000f,-1.0000f,1.5000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_slab\",Properties:{type:\"bottom\"}},transformation:[0.0000f,1.0000f,-0.0000f,2.0000f,1.0000f,-0.0000f,0.0000f,-0.5000f,0.0000f,-0.0000f,-1.0000f,-1.0000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_slab\",Properties:{type:\"bottom\"}},transformation:[0.0000f,1.0000f,-0.0000f,2.0000f,1.0000f,-0.0000f,0.0000f,-2.0000f,0.0000f,-0.0000f,-1.0000f,0.5000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_slab\",Properties:{type:\"bottom\"}},transformation:[0.0000f,1.0000f,-0.0000f,2.0000f,1.0000f,-0.0000f,0.0000f,-0.5000f,0.0000f,-0.0000f,-1.0000f,2.0000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_slab\",Properties:{type:\"bottom\"}},transformation:[0.0000f,1.0000f,-0.0000f,2.0000f,1.0000f,-0.0000f,0.0000f,1.0000f,0.0000f,-0.0000f,-1.0000f,0.5000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_stairs\",Properties:{facing:\"east\",half:\"bottom\",shape:\"straight\"}},transformation:[0.0000f,-1.0000f,0.0000f,-2.0000f,-1.0000f,-0.0000f,0.0000f,1.5000f,0.0000f,-0.0000f,-1.0000f,0.5000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_stairs\",Properties:{facing:\"west\",half:\"bottom\",shape:\"straight\"}},transformation:[0.0000f,-1.0000f,0.0000f,-2.0000f,-1.0000f,-0.0000f,0.0000f,-0.5000f,0.0000f,-0.0000f,-1.0000f,0.5000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_stairs\",Properties:{facing:\"north\",half:\"bottom\",shape:\"straight\"}},transformation:[0.0000f,-1.0000f,0.0000f,-2.0000f,-1.0000f,-0.0000f,0.0000f,0.5000f,0.0000f,-0.0000f,-1.0000f,-0.5000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_stairs\",Properties:{facing:\"south\",half:\"bottom\",shape:\"straight\"}},transformation:[0.0000f,-1.0000f,0.0000f,-2.0000f,-1.0000f,-0.0000f,0.0000f,0.5000f,0.0000f,-0.0000f,-1.0000f,1.5000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_slab\",Properties:{type:\"bottom\"}},transformation:[0.0000f,-1.0000f,0.0000f,-2.5000f,-1.0000f,-0.0000f,0.0000f,0.5000f,0.0000f,-0.0000f,-1.0000f,0.5000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_stairs\",Properties:{facing:\"north\",half:\"bottom\",shape:\"inner_left\"}},transformation:[0.0000f,-1.0000f,0.0000f,-1.5000f,-1.0000f,-0.0000f,0.0000f,-0.5000f,0.0000f,-0.0000f,-1.0000f,-0.5000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_stairs\",Properties:{facing:\"east\",half:\"bottom\",shape:\"inner_left\"}},transformation:[0.0000f,-1.0000f,0.0000f,-1.5000f,-1.0000f,-0.0000f,0.0000f,1.5000f,0.0000f,-0.0000f,-1.0000f,-0.5000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_stairs\",Properties:{facing:\"south\",half:\"bottom\",shape:\"inner_left\"}},transformation:[0.0000f,-1.0000f,0.0000f,-1.5000f,-1.0000f,-0.0000f,0.0000f,1.5000f,0.0000f,-0.0000f,-1.0000f,1.5000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_stairs\",Properties:{facing:\"west\",half:\"bottom\",shape:\"inner_left\"}},transformation:[0.0000f,-1.0000f,0.0000f,-1.5000f,-1.0000f,-0.0000f,0.0000f,-0.5000f,0.0000f,-0.0000f,-1.0000f,1.5000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_slab\",Properties:{type:\"bottom\"}},transformation:[0.0000f,-1.0000f,0.0000f,-2.0000f,-1.0000f,-0.0000f,0.0000f,0.5000f,0.0000f,-0.0000f,-1.0000f,-1.0000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_slab\",Properties:{type:\"bottom\"}},transformation:[0.0000f,-1.0000f,0.0000f,-2.0000f,-1.0000f,-0.0000f,0.0000f,2.0000f,0.0000f,-0.0000f,-1.0000f,0.5000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_slab\",Properties:{type:\"bottom\"}},transformation:[0.0000f,-1.0000f,0.0000f,-2.0000f,-1.0000f,-0.0000f,0.0000f,0.5000f,0.0000f,-0.0000f,-1.0000f,2.0000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_slab\",Properties:{type:\"bottom\"}},transformation:[0.0000f,-1.0000f,0.0000f,-2.0000f,-1.0000f,-0.0000f,0.0000f,-1.0000f,0.0000f,-0.0000f,-1.0000f,0.5000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_stairs\",Properties:{facing:\"east\",half:\"bottom\",shape:\"straight\"}},transformation:[-0.0000f,0.0000f,1.0000f,-0.5000f,-1.0000f,-0.0000f,-0.0000f,1.5000f,0.0000f,-1.0000f,0.0000f,-2.0000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_stairs\",Properties:{facing:\"west\",half:\"bottom\",shape:\"straight\"}},transformation:[-0.0000f,0.0000f,1.0000f,-0.5000f,-1.0000f,-0.0000f,-0.0000f,-0.5000f,0.0000f,-1.0000f,0.0000f,-2.0000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_stairs\",Properties:{facing:\"north\",half:\"bottom\",shape:\"straight\"}},transformation:[-0.0000f,0.0000f,1.0000f,0.5000f,-1.0000f,-0.0000f,-0.0000f,0.5000f,0.0000f,-1.0000f,0.0000f,-2.0000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_stairs\",Properties:{facing:\"south\",half:\"bottom\",shape:\"straight\"}},transformation:[-0.0000f,0.0000f,1.0000f,-1.5000f,-1.0000f,-0.0000f,-0.0000f,0.5000f,0.0000f,-1.0000f,0.0000f,-2.0000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_slab\",Properties:{type:\"bottom\"}},transformation:[-0.0000f,0.0000f,1.0000f,-0.5000f,-1.0000f,-0.0000f,-0.0000f,0.5000f,0.0000f,-1.0000f,0.0000f,-2.5000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_stairs\",Properties:{facing:\"north\",half:\"bottom\",shape:\"inner_left\"}},transformation:[-0.0000f,0.0000f,1.0000f,0.5000f,-1.0000f,-0.0000f,-0.0000f,-0.5000f,0.0000f,-1.0000f,0.0000f,-1.5000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_stairs\",Properties:{facing:\"east\",half:\"bottom\",shape:\"inner_left\"}},transformation:[-0.0000f,0.0000f,1.0000f,0.5000f,-1.0000f,-0.0000f,-0.0000f,1.5000f,0.0000f,-1.0000f,0.0000f,-1.5000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_stairs\",Properties:{facing:\"south\",half:\"bottom\",shape:\"inner_left\"}},transformation:[-0.0000f,0.0000f,1.0000f,-1.5000f,-1.0000f,-0.0000f,-0.0000f,1.5000f,0.0000f,-1.0000f,0.0000f,-1.5000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_stairs\",Properties:{facing:\"west\",half:\"bottom\",shape:\"inner_left\"}},transformation:[-0.0000f,0.0000f,1.0000f,-1.5000f,-1.0000f,-0.0000f,-0.0000f,-0.5000f,0.0000f,-1.0000f,0.0000f,-1.5000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_slab\",Properties:{type:\"bottom\"}},transformation:[-0.0000f,0.0000f,1.0000f,1.0000f,-1.0000f,-0.0000f,-0.0000f,0.5000f,0.0000f,-1.0000f,0.0000f,-2.0000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_slab\",Properties:{type:\"bottom\"}},transformation:[-0.0000f,0.0000f,1.0000f,-0.5000f,-1.0000f,-0.0000f,-0.0000f,2.0000f,0.0000f,-1.0000f,0.0000f,-2.0000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_slab\",Properties:{type:\"bottom\"}},transformation:[-0.0000f,0.0000f,1.0000f,-2.0000f,-1.0000f,-0.0000f,-0.0000f,0.5000f,0.0000f,-1.0000f,0.0000f,-2.0000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_slab\",Properties:{type:\"bottom\"}},transformation:[-0.0000f,0.0000f,1.0000f,-0.5000f,-1.0000f,-0.0000f,-0.0000f,-1.0000f,0.0000f,-1.0000f,0.0000f,-2.0000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_stairs\",Properties:{facing:\"east\",half:\"bottom\",shape:\"straight\"}},transformation:[0.0000f,-0.0000f,-1.0000f,0.5000f,-1.0000f,-0.0000f,-0.0000f,1.5000f,-0.0000f,1.0000f,0.0000f,2.0000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_stairs\",Properties:{facing:\"west\",half:\"bottom\",shape:\"straight\"}},transformation:[0.0000f,-0.0000f,-1.0000f,0.5000f,-1.0000f,-0.0000f,-0.0000f,-0.5000f,-0.0000f,1.0000f,0.0000f,2.0000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_stairs\",Properties:{facing:\"north\",half:\"bottom\",shape:\"straight\"}},transformation:[0.0000f,-0.0000f,-1.0000f,-0.5000f,-1.0000f,-0.0000f,-0.0000f,0.5000f,-0.0000f,1.0000f,0.0000f,2.0000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_stairs\",Properties:{facing:\"south\",half:\"bottom\",shape:\"straight\"}},transformation:[0.0000f,-0.0000f,-1.0000f,1.5000f,-1.0000f,-0.0000f,-0.0000f,0.5000f,-0.0000f,1.0000f,0.0000f,2.0000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_slab\",Properties:{type:\"bottom\"}},transformation:[0.0000f,-0.0000f,-1.0000f,0.5000f,-1.0000f,-0.0000f,-0.0000f,0.5000f,-0.0000f,1.0000f,0.0000f,2.5000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_stairs\",Properties:{facing:\"north\",half:\"bottom\",shape:\"inner_left\"}},transformation:[0.0000f,-0.0000f,-1.0000f,-0.5000f,-1.0000f,-0.0000f,-0.0000f,-0.5000f,-0.0000f,1.0000f,0.0000f,1.5000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_stairs\",Properties:{facing:\"east\",half:\"bottom\",shape:\"inner_left\"}},transformation:[0.0000f,-0.0000f,-1.0000f,-0.5000f,-1.0000f,-0.0000f,-0.0000f,1.5000f,-0.0000f,1.0000f,0.0000f,1.5000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_stairs\",Properties:{facing:\"south\",half:\"bottom\",shape:\"inner_left\"}},transformation:[0.0000f,-0.0000f,-1.0000f,1.5000f,-1.0000f,-0.0000f,-0.0000f,1.5000f,-0.0000f,1.0000f,0.0000f,1.5000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_stairs\",Properties:{facing:\"west\",half:\"bottom\",shape:\"inner_left\"}},transformation:[0.0000f,-0.0000f,-1.0000f,1.5000f,-1.0000f,-0.0000f,-0.0000f,-0.5000f,-0.0000f,1.0000f,0.0000f,1.5000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_slab\",Properties:{type:\"bottom\"}},transformation:[0.0000f,-0.0000f,-1.0000f,-1.0000f,-1.0000f,-0.0000f,-0.0000f,0.5000f,-0.0000f,1.0000f,0.0000f,2.0000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_slab\",Properties:{type:\"bottom\"}},transformation:[0.0000f,-0.0000f,-1.0000f,0.5000f,-1.0000f,-0.0000f,-0.0000f,2.0000f,-0.0000f,1.0000f,0.0000f,2.0000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_slab\",Properties:{type:\"bottom\"}},transformation:[0.0000f,-0.0000f,-1.0000f,2.0000f,-1.0000f,-0.0000f,-0.0000f,0.5000f,-0.0000f,1.0000f,0.0000f,2.0000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_slab\",Properties:{type:\"bottom\"}},transformation:[0.0000f,-0.0000f,-1.0000f,0.5000f,-1.0000f,-0.0000f,-0.0000f,-1.0000f,-0.0000f,1.0000f,0.0000f,2.0000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone\",Properties:{}},transformation:[1.0000f,0.0000f,0.0000f,1.0000f,0.0000f,1.0000f,0.0000f,0.0000f,0.0000f,0.0000f,1.0000f,1.0000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone\",Properties:{}},transformation:[1.0000f,0.0000f,0.0000f,-2.0000f,0.0000f,1.0000f,0.0000f,0.0000f,0.0000f,0.0000f,1.0000f,1.0000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone\",Properties:{}},transformation:[1.0000f,0.0000f,0.0000f,-2.0000f,0.0000f,1.0000f,0.0000f,0.0000f,0.0000f,0.0000f,1.0000f,-2.0000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone\",Properties:{}},transformation:[1.0000f,0.0000f,0.0000f,1.0000f,0.0000f,1.0000f,0.0000f,0.0000f,0.0000f,0.0000f,1.0000f,-2.0000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone\",Properties:{}},transformation:[1.0000f,0.0000f,0.0000f,1.0000f,0.0000f,1.0000f,0.0000f,-1.0000f,0.0000f,0.0000f,1.0000f,1.0000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone\",Properties:{}},transformation:[1.0000f,0.0000f,0.0000f,-2.0000f,0.0000f,1.0000f,0.0000f,-1.0000f,0.0000f,0.0000f,1.0000f,1.0000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone\",Properties:{}},transformation:[1.0000f,0.0000f,0.0000f,-2.0000f,0.0000f,1.0000f,0.0000f,-1.0000f,0.0000f,0.0000f,1.0000f,-2.0000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone\",Properties:{}},transformation:[1.0000f,0.0000f,0.0000f,1.0000f,0.0000f,1.0000f,0.0000f,-1.0000f,0.0000f,0.0000f,1.0000f,-2.0000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone\",Properties:{}},transformation:[1.0000f,0.0000f,0.0000f,-1.0000f,0.0000f,1.0000f,0.0000f,1.0000f,0.0000f,0.0000f,1.0000f,1.0000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone\",Properties:{}},transformation:[1.0000f,0.0000f,0.0000f,-2.0000f,0.0000f,1.0000f,0.0000f,1.0000f,0.0000f,0.0000f,1.0000f,0.0000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone\",Properties:{}},transformation:[1.0000f,0.0000f,0.0000f,-2.0000f,0.0000f,1.0000f,0.0000f,1.0000f,0.0000f,0.0000f,1.0000f,-1.0000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone\",Properties:{}},transformation:[1.0000f,0.0000f,0.0000f,-1.0000f,0.0000f,1.0000f,0.0000f,1.0000f,0.0000f,0.0000f,1.0000f,-2.0000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone\",Properties:{}},transformation:[1.0000f,0.0000f,0.0000f,0.0000f,0.0000f,1.0000f,0.0000f,1.0000f,0.0000f,0.0000f,1.0000f,-2.0000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone\",Properties:{}},transformation:[1.0000f,0.0000f,0.0000f,1.0000f,0.0000f,1.0000f,0.0000f,1.0000f,0.0000f,0.0000f,1.0000f,-1.0000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone\",Properties:{}},transformation:[1.0000f,0.0000f,0.0000f,1.0000f,0.0000f,1.0000f,0.0000f,1.0000f,0.0000f,0.0000f,1.0000f,0.0000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone\",Properties:{}},transformation:[1.0000f,0.0000f,0.0000f,0.0000f,0.0000f,1.0000f,0.0000f,1.0000f,0.0000f,0.0000f,1.0000f,1.0000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone\",Properties:{}},transformation:[1.0000f,0.0000f,0.0000f,-1.0000f,0.0000f,1.0000f,0.0000f,-2.0000f,0.0000f,0.0000f,1.0000f,1.0000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone\",Properties:{}},transformation:[1.0000f,0.0000f,0.0000f,-2.0000f,0.0000f,1.0000f,0.0000f,-2.0000f,0.0000f,0.0000f,1.0000f,0.0000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone\",Properties:{}},transformation:[1.0000f,0.0000f,0.0000f,-2.0000f,0.0000f,1.0000f,0.0000f,-2.0000f,0.0000f,0.0000f,1.0000f,-1.0000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone\",Properties:{}},transformation:[1.0000f,0.0000f,0.0000f,-1.0000f,0.0000f,1.0000f,0.0000f,-2.0000f,0.0000f,0.0000f,1.0000f,-2.0000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone\",Properties:{}},transformation:[1.0000f,0.0000f,0.0000f,0.0000f,0.0000f,1.0000f,0.0000f,-2.0000f,0.0000f,0.0000f,1.0000f,-2.0000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone\",Properties:{}},transformation:[1.0000f,0.0000f,0.0000f,1.0000f,0.0000f,1.0000f,0.0000f,-2.0000f,0.0000f,0.0000f,1.0000f,-1.0000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone\",Properties:{}},transformation:[1.0000f,0.0000f,0.0000f,1.0000f,0.0000f,1.0000f,0.0000f,-2.0000f,0.0000f,0.0000f,1.0000f,0.0000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone\",Properties:{}},transformation:[1.0000f,0.0000f,0.0000f,0.0000f,0.0000f,1.0000f,0.0000f,-2.0000f,0.0000f,0.0000f,1.0000f,1.0000f,0.0000f,0.0000f,0.0000f,1.0000f]}]}"
        );

        @DisplayField private final int corrosionTime = 130;
        @DisplayField private final double meteoriteRadius = 8.5d;
        @DisplayField private final double meteoriteDamage = 50.0d;
        @DisplayField(suffix = "blocks") private final double distanceFromLanding = 15;

        public MoonwalkerUltimate() {
            super(Moonwalker.this, "Moonteorite", 80);

            setDescription("""
                    Summon a huge meteorite at the &etarget&7 location.
                    
                    Upon landing, it creates a huge explosion, dealing massive damage and applying &6&lCorrosion&7 for &b{corrosionTime}&7.
                    """
            );

            setItem(Material.END_STONE_BRICKS);
            setDuration(30);
            setCooldownSec(45);
        }

        @Nonnull
        @Override
        public UltimateInstance newInstance(@Nonnull GamePlayer player) {
            final Block targetBlock = HeroRegistry.MOONWALKER.getTargetBlock(player);

            if (targetBlock == null) {
                return error("Invalid block!");
            }

            return execute(() -> {
                final Location landingLocation = targetBlock.getRelative(BlockFace.UP).getLocation();
                landingLocation.setYaw(0.0f);
                landingLocation.setPitch(0.0f);

                final Location spawnLocation = landingLocation.clone();
                final Vector offset = player.getLocation().getDirection();

                offset.normalize().setY(0.0d).multiply(20);
                spawnLocation.add(0.0d, 15, 0.0d);
                spawnLocation.subtract(offset);

                final DisplayEntity entity = blob.spawnInterpolated(spawnLocation);

                final Vector vector = landingLocation.toVector()
                                                     .subtract(spawnLocation.toVector())
                                                     .normalize()
                                                     .multiply(0.7d);

                new TickingGameTask() {
                    private double theta = 0.0d;
                    private int spots = 10;

                    @Override
                    public void run(int tick) {
                        if (tick > getDuration()) {
                            entity.remove();
                            explode(player, landingLocation);
                            PlayerLib.stopSound(Sound.ENTITY_WITHER_DEATH);
                            cancel();
                            return;
                        }

                        spawnLocation.add(vector);
                        entity.teleport(spawnLocation);

                        // Notify players
                        Collect.nearbyPlayers(landingLocation, meteoriteRadius).forEach(player -> {
                            player.sendWarning(WarningType.DANGER, 5);
                        });

                        // Fx
                        for (int i = 1; i <= spots; i++) {
                            final double x = Math.sin(theta + i * spots) * meteoriteRadius;
                            final double z = Math.cos(theta + i * spots) * meteoriteRadius;

                            landingLocation.add(x, 0, z);

                            player.spawnWorldParticle(landingLocation, Particle.CRIT, 2, 0.1d, 0.05d, 0.1d, 0.5f);
                            player.spawnWorldParticle(landingLocation, Particle.ITEM_SNOWBALL, 1, 0.1d, 0.05d, 0.1d, 1.0f);

                            landingLocation.subtract(x, 0, z);
                        }

                        theta += Math.PI / 16;
                    }
                }.runTaskTimer(0, 1);

                // Fx
                player.playSound(Sound.ENTITY_WITHER_DEATH, 0.0f);
            });
        }

        public void createBlob(Location center, boolean last) {
            PlayerLib.spawnParticle(center, Particle.LAVA, 10, 1, 1, 1, 0);

            // Clear previous blob
            clearTrash(center.clone());

            // Move location to the next step
            center.subtract(1, 0, 1);

            final Set<Block> savedBlocks = new HashSet<>();

            // Spawn inner layer
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    final Block block = sendChange(center.clone().subtract(i, 0, j), Material.END_STONE_BRICKS);
                    // only save the last iteration
                    if (last) {
                        savedBlocks.add(block);
                    }
                }
            }

            // Spawn outer layer
            center.add(0, 1, 0);
            fillOuter(center, last ? savedBlocks : null);

            // Spawn outer layer 2
            center.subtract(0, 2, 0);
            fillOuter(center, last ? savedBlocks : null);

            if (last) {
                for (Block savedBlock : savedBlocks) {
                    savedBlock.getState().update(false, false);
                }
                savedBlocks.clear();
            }

        }

        private Block sendChange(Location location, Material material) {
            final BlockData data = material.createBlockData();
            Bukkit.getOnlinePlayers().forEach(player -> player.sendBlockChange(location, data));
            return location.getBlock();
        }

        private void fillOuter(Location center, Set<Block> blocks) {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if ((i == 0 || i == 2) && j != 1) {
                        continue;
                    }
                    final Block block = sendChange(center.clone().subtract(i, 0, j), Material.END_STONE);
                    if (blocks != null) {
                        blocks.add(block);
                    }
                }
            }
        }

        private void clearTrash(Location center) {
            center.add(0, 2, 0);
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if ((i == 0 || i == 2) && j != 1) {
                        continue;
                    }
                    center.clone().subtract(i, 0, j).getBlock().getState().update(true, false);
                }
            }

            center.subtract(0, 1, 0);
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (((i == 1 || i == 2) && j == 2) || (i == 2 && j == 1)) {
                        continue;
                    }
                    center.clone().subtract(i, 0, j).getBlock().getState().update(true, false);
                }
            }

            center.subtract(0, 1, 0);
            center.clone().subtract(1, 0, 0).getBlock().getState().update(true, false);
            center.clone().subtract(0, 0, 1).getBlock().getState().update(true, false);
        }

        private void explode(GamePlayer executor, Location location) {
            final World world = location.getWorld();

            if (world == null) {
                throw new NullPointerException("world is null");
            }

            Collect.nearbyEntities(location, meteoriteRadius).forEach(entity -> {
                entity.damage(meteoriteDamage, executor, DamageCause.METEORITE);
                entity.addEffect(EffectType.CORROSION, corrosionTime);
            });

            // FX
            PlayerLib.spawnParticle(location, Particle.EXPLOSION_EMITTER, 1, 0.8d, 0.2d, 0.8d, 0.0f);
            PlayerLib.spawnParticle(location, Particle.POOF, 15, 5d, 2d, 5d, 0.0f);

            PlayerLib.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 0.0f);
            PlayerLib.playSound(location, Sound.ENTITY_WITHER_HURT, 0.25f);
            PlayerLib.playSound(location, Sound.ENTITY_ENDER_DRAGON_HURT, 0.5f);
        }

    }
}
