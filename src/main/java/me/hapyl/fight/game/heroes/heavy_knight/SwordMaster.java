package me.hapyl.fight.game.heroes.heavy_knight;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Disabled;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.attribute.ModifierSource;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.HeroEquipment;
import me.hapyl.fight.game.heroes.ultimate.UltimateInstance;
import me.hapyl.fight.game.heroes.ultimate.UltimateTalent;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.talents.heavy_knight.Slash;
import me.hapyl.fight.game.talents.heavy_knight.Updraft;
import me.hapyl.fight.game.talents.heavy_knight.Uppercut;
import me.hapyl.fight.util.collection.player.PlayerDataMap;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import javax.annotation.Nonnull;

public class SwordMaster extends Hero implements PlayerDataHandler<SwordMasterData>, Disabled {
    
    private final PlayerDataMap<SwordMasterData> playerData = PlayerMap.newDataMap(player -> new SwordMasterData(this, player));
    
    public SwordMaster(@Nonnull Key key) {
        super(key, "Heavy Knight");
        
        final HeroProfile profile = getProfile();
        profile.setArchetypes(Archetype.DAMAGE, Archetype.TALENT_DAMAGE, Archetype.HEXBANE);
        profile.setAffiliation(Affiliation.KINGDOM);
        profile.setGender(Gender.MALE);
        
        setDescription("""
                       A royal knight with heavy armor used to fight the toughest beast and demons alike.
                       """);
        
        setItem("4b2a75f05437ba2e28fb2a7d0eb6697a6e091ce91072b5c4ff1945295b092");
        
        final HeroAttributes attributes = getAttributes();
        attributes.setDefense(150);
        attributes.setSpeed(60);
        attributes.setAttackSpeed(50);
        
        final HeroEquipment equipment = getEquipment();
        equipment.setChestPlate(Material.NETHERITE_CHESTPLATE, TrimPattern.HOST, TrimMaterial.NETHERITE);
        equipment.setLeggings(Material.NETHERITE_LEGGINGS, TrimPattern.SILENCE, TrimMaterial.IRON);
        equipment.setBoots(Material.NETHERITE_BOOTS, TrimPattern.SILENCE, TrimMaterial.NETHERITE);
        
        setWeapon(new SwordMasterWeapon());
        setUltimate(new SwordMasterUltimate());
    }
    
    @Override
    public Uppercut getFirstTalent() {
        return TalentRegistry.UPPERCUT;
    }
    
    @Override
    public Updraft getSecondTalent() {
        return TalentRegistry.UPDRAFT;
    }
    
    @Override
    public Slash getThirdTalent() {
        return TalentRegistry.SLASH;
    }
    
    @Override
    public Talent getPassiveTalent() {
        return TalentRegistry.SWORD_MASTER_PASSIVE;
    }
    
    @Nonnull
    @Override
    public SwordMasterWeapon getWeapon() {
        return (SwordMasterWeapon) super.getWeapon();
    }
    
    @Nonnull
    @Override
    public PlayerDataMap<SwordMasterData> getDataMap() {
        return playerData;
    }
    
    public boolean addSuccessfulTalent(@Nonnull GamePlayer player, @Nonnull Talent talent) {
        return getPlayerData(player).buffer.offer(talent);
    }
    
    public void empowerWeapon(@Nonnull GamePlayer player) {
        getPlayerData(player).empowerWeapon();
    }
    
    private class SwordMasterUltimate extends UltimateTalent {
        
        private final ModifierSource modifierSource = new ModifierSource(Key.ofString("ultimate_sacrifice"));
        
        @DisplayField(percentage = true) private final double attackIncrease = 1.0;
        @DisplayField private final double speedIncrease = 50;
        @DisplayField private final double defenseDecrease = -1_000_000;
        @DisplayField private final double fatigueDecrease = -50;
        
        public SwordMasterUltimate() {
            super(SwordMaster.this, "tbd", 9999999);
            
            setDescription("""
                           tbd
                           """
            );
            
        }
        
        @Nonnull
        @Override
        public UltimateInstance newInstance(@Nonnull GamePlayer player, boolean isFullyCharged) {
            return execute(() -> {});
        }
    }
}
