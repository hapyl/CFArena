package me.hapyl.fight.game.heroes.doctor;

import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.HeroEquipment;
import me.hapyl.fight.game.heroes.ultimate.UltimateInstance;
import me.hapyl.fight.game.heroes.ultimate.UltimateTalent;
import me.hapyl.fight.game.loadout.HotBarSlot;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.talents.doctor.BlockMaelstromPassive;
import me.hapyl.fight.game.talents.doctor.ConfusionPotion;
import me.hapyl.fight.game.talents.doctor.HarvestBlocks;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.ui.UIComponent;
import me.hapyl.fight.util.collection.player.PlayerDataMap;
import me.hapyl.fight.util.collection.player.PlayerMap;
import org.bukkit.Material;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import javax.annotation.Nonnull;

public class DrEd extends Hero implements UIComponent, PlayerDataHandler<DrEdData> {

    private final PlayerDataMap<DrEdData> drEdData = PlayerMap.newDataMap(player -> new DrEdData(this ,player));
    private final PhysGun ultimateWeapon = new PhysGun();

    public DrEd(@Nonnull Key key) {
        super(key, "Dr. Ed");

        final HeroProfile profile = getProfile();
        profile.setArchetypes(Archetype.STRATEGY, Archetype.RANGE, Archetype.POWERFUL_ULTIMATE);
        profile.setGender(Gender.MALE);

        setDescription("""
                Simply named scientist with not-so-simple inventions...
                """);
        setItem("3b51e96bddd177992d68278c9d5f1e685b60fbb94aaa709259e9f2781c76f8");

        final HeroAttributes attributes = getAttributes();
        attributes.setSpeed(115);
        attributes.setDefense(125);

        final HeroEquipment equipment = getEquipment();
        equipment.setChestPlate(237, 235, 235, TrimPattern.VEX, TrimMaterial.IRON);
        equipment.setLeggings(Material.IRON_LEGGINGS, TrimPattern.VEX, TrimMaterial.IRON);
        equipment.setBoots(71, 107, 107);

        setWeapon(new GravityGun());
        setUltimate(new DrEdUltimate());
    }

    @Override
    public void onStart(@Nonnull GameInstance instance) {
        new GameTask() {
            @Override
            public void run() {
                drEdData.values().forEach(DrEdData::tick);
            }
        }.runTaskTimer(0, 1);
    }

    @Override
    public void onStart(@Nonnull GamePlayer player) {
        scheduleNextShield(player, false);
    }

    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        if (getWeapon() instanceof GravityGun weapon) {
            weapon.remove(player);
        }
    }

    @Override
    public ConfusionPotion getFirstTalent() {
        return TalentRegistry.CONFUSION_POTION;
    }

    @Override
    public HarvestBlocks getSecondTalent() {
        return TalentRegistry.HARVEST;
    }

    @Override
    public BlockMaelstromPassive getPassiveTalent() {
        return TalentRegistry.BLOCK_SHIELD;
    }

    @Nonnull
    @Override
    public String getString(@Nonnull GamePlayer player) {
        final BlockShield shield = getPlayerData(player).shield;

        if (shield == null) {
            return "";
        }

        return "&6🛡 " + Chat.capitalize(shield.getType());
    }

    @Nonnull
    @Override
    public PlayerDataMap<DrEdData> getDataMap() {
        return drEdData;
    }

    protected void scheduleNextShield(GamePlayer player, boolean onHit) {
        getPlayerData(player).newShield(onHit);
    }

    private class DrEdUltimate extends UltimateTalent {
        public DrEdUltimate() {
            super(DrEd.this, "Upgrades People, Upgrades!", 70);

            setDescription("""
                    Grants Dr. Ed an upgraded version of &a%s&7 for {duration} that is capable of capturing entities' flesh and energy, allowing manipulating them.
                    """.formatted(getWeapon().getName())
            );

            setType(TalentType.IMPAIR);
            setItem(Material.GOLDEN_HORSE_ARMOR);
            setDurationSec(10);
        }

        @Nonnull
        @Override
        public UltimateInstance newInstance(@Nonnull GamePlayer player) {
            return builder()
                    .onExecute(() -> {
                        player.setItemAndSnap(HotBarSlot.HERO_ITEM, ultimateWeapon.getItem());
                    })
                    .onEnd(() -> {
                        ultimateWeapon.stop(player);
                    });
        }
    }
}
