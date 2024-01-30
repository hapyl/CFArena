package me.hapyl.fight.game.heroes.archive.heavy_knight;

import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.attribute.temper.TemperInstance;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.archive.heavy_knight.Slash;
import me.hapyl.fight.game.talents.archive.heavy_knight.Updraft;
import me.hapyl.fight.game.talents.archive.heavy_knight.Uppercut;
import me.hapyl.fight.game.talents.archive.techie.Talent;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.util.collection.player.PlayerDataMap;
import me.hapyl.fight.util.collection.player.PlayerMap;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.inventory.EntityEquipment;

import javax.annotation.Nonnull;

public class SwordMaster extends Hero implements PlayerDataHandler<SwordMasterData> {

    private final TemperInstance temperInstance = Temper.ULTIMATE_SACRIFICE.newInstance()
            .increase(AttributeType.ATTACK, 1.0d)
            .increase(AttributeType.SPEED, 0.1d) // 50%
            .decrease(AttributeType.DEFENSE, 100.0d)
            .decrease(AttributeType.COOLDOWN_MODIFIER, 0.5d);

    private final PlayerDataMap<SwordMasterData> playerData = PlayerMap.newDataMap(player -> new SwordMasterData(this, player));

    public SwordMaster(@Nonnull Heroes handle) {
        super(handle, "Heavy Knight");

        setArchetype(Archetype.DAMAGE);
        setAffiliation(Affiliation.KINGDOM);

        setItem("4b2a75f05437ba2e28fb2a7d0eb6697a6e091ce91072b5c4ff1945295b092");

        final HeroAttributes attributes = getAttributes();
        attributes.setDefense(150);
        attributes.setSpeed(80);

        final Equipment equipment = getEquipment();
        equipment.setChestPlate(Material.NETHERITE_CHESTPLATE);
        equipment.setLeggings(Material.IRON_LEGGINGS);
        equipment.setBoots(Material.NETHERITE_BOOTS);

        setWeapon(new Weapon(Material.NETHERITE_SWORD).setName("Basta").setDamage(12.0d).setAttackSpeed(-0.2d));

        setUltimate(new UltimateTalent(this, "Ultimate Sacrifice", """
                Instantly drop &nall&7 your &farmor&7 in exchange for more &4power&7, becoming a glass cannon.
                                
                &8;;You also benefit from lower talent cooldowns.
                """, 60)
                .setType(Talent.Type.ENHANCE)
                .setItem(Material.NETHERITE_INGOT)
                .setDurationSec(3));
    }

    @Override
    public UltimateCallback useUltimate(@Nonnull GamePlayer player) {
        temperInstance.temper(player, getUltimateDuration());

        // Remove armor for Fx
        final EntityEquipment equipment = player.getEquipment();
        equipment.setHelmet(null, true);
        equipment.setChestplate(null, true);
        equipment.setLeggings(null, true);
        equipment.setBoots(null, true);

        // Fx
        player.playWorldSound(Sound.ENTITY_IRON_GOLEM_DAMAGE, 0.75f);
        player.spawnWorldParticle(player.getMidpointLocation(), Particle.CRIT, 10, 0.15, 0.4, 0.15, 0.1f);

        return UltimateCallback.OK;
    }

    @Override
    public void onUltimateEnd(@Nonnull GamePlayer player) {
        getEquipment().equip(player);
    }

    @Override
    public Uppercut getFirstTalent() {
        return (Uppercut) Talents.UPPERCUT.getTalent();
    }

    @Override
    public Updraft getSecondTalent() {
        return (Updraft) Talents.UPDRAFT.getTalent();
    }

    @Override
    public Slash getThirdTalent() {
        return (Slash) Talents.SLASH.getTalent();
    }

    @Override
    public Talent getPassiveTalent() {
        return Talents.SWORD_MASTER_PASSIVE.getTalent();
    }

    @Nonnull
    @Override
    public PlayerDataMap<SwordMasterData> getDataMap() {
        return playerData;
    }

    public static boolean addSuccessfulTalent(@Nonnull GamePlayer player, @Nonnull Talent talent) {
        return Heroes.SWORD_MASTER.getHero(SwordMaster.class).getPlayerData(player).buffer.offer(talent);
    }
}
