package me.hapyl.fight.game.heroes.miku;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Disabled;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.HeroEquipment;
import me.hapyl.fight.game.heroes.ultimate.UltimateInstance;
import me.hapyl.fight.game.heroes.ultimate.UltimateTalent;
import me.hapyl.fight.game.talents.Talent;
import org.bukkit.Material;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import javax.annotation.Nonnull;

public class MikuHatsune extends Hero implements Disabled {
    
    /// ### Weapon - Microphone
    /// * Right click projectile
    /// * Hold Right click burst
    ///
    /// ### First Talent - Name
    /// * +Speed +Defense
    ///
    /// ### Ultimate - ???
    /// * Teammate Buff
    /// * Enemy debuff
    public MikuHatsune(@Nonnull Key key) {
        super(key, "Hatsune Miku");
        
        final HeroProfile profile = getProfile();
        profile.setArchetypes(Archetype.HEALER, Archetype.SUPPORT);
        profile.setRace(Race.HUMAN);
        profile.setGender(Gender.FEMALE);
        
        final HeroAttributes attributes = getAttributes();
        
        final HeroEquipment equipment = getEquipment();
        equipment.setTexture("8ab112adb3873951b86184a9609ffc78a5eabed42f29c7d7aef0542e23625add");
        equipment.setChestPlate(150, 150, 150, TrimPattern.VEX, TrimMaterial.DIAMOND);
        equipment.setLeggings(Material.NETHERITE_LEGGINGS, TrimPattern.SNOUT, TrimMaterial.DIAMOND);
        equipment.setBoots(Material.NETHERITE_BOOTS, TrimPattern.WILD, TrimMaterial.DIAMOND);
        
        setWeapon(new MikuHatsuneWeapon());
        setUltimate(new MikuHatsuneUltimate());
    }
    
    @Override
    public Talent getFirstTalent() {
        return null;
    }
    
    @Override
    public Talent getSecondTalent() {
        return null;
    }
    
    @Override
    public Talent getPassiveTalent() {
        return null;
    }
    
    private class MikuHatsuneUltimate extends UltimateTalent {
        
        public MikuHatsuneUltimate() {
            super(MikuHatsune.this, "Miku Miku Beam!!", 60);
        }
        
        @Nonnull
        @Override
        public UltimateInstance newInstance(@Nonnull GamePlayer player, boolean isFullyCharged) {
            return execute(() -> {});
        }
    }
}
