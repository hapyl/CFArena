package me.hapyl.fight.game.heroes.engineer;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.IndexedTicking;
import me.hapyl.eterna.module.util.RomanNumber;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.ModifierSource;
import me.hapyl.fight.game.attribute.ModifierType;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.PlayerData;
import me.hapyl.fight.game.loadout.HotBarSlot;
import me.hapyl.fight.game.talents.engineer.Construct;
import me.hapyl.fight.game.talents.engineer.EngineerConstructTalent;
import me.hapyl.fight.game.talents.engineer.MagneticAttractionPassive;
import me.hapyl.fight.game.ui.UIComplexComponent;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class EngineerData extends PlayerData implements IndexedTicking, UIComplexComponent {
    
    public static final String ironDescription = """
                                                 %sRIGHT CLICK&7 on a construct to upgrade it's level up to level &b%s&7.
                                                 &8&o;;Upgrading a construct increases its Max Health and remaining charges.
                                                 
                                                 %sLEFT CLICK&7 on a construct to heal it for &a%.0f%%&7 of its %s.
                                                 """.formatted(
            Color.BUTTON.bold(), RomanNumber.toRoman(Construct.MAX_LEVEL),
            Color.BUTTON_DARKER.bold(), Construct.HEALING * 100, AttributeType.MAX_HEALTH
    );
    
    private static final ModifierSource modifierSource = new ModifierSource(Key.ofString("mecha_industry"));
    
    private final Engineer engineer;
    private final Map<EngineerConstructTalent, Construct> constructs;
    
    @Nullable private MechaIndustries mecha;
    
    private int iron;
    
    EngineerData(@Nonnull Engineer engineer, @Nonnull GamePlayer player) {
        super(player);
        
        this.engineer = engineer;
        this.constructs = Maps.newHashMap();
        
        setIron(engineer.getPassiveTalent().startIron);
    }
    
    public int getIron() {
        return iron;
    }
    
    public void setIron(int iron) {
        this.iron = Math.clamp(iron, 0, engineer.getPassiveTalent().maxIron);
        
        // Update iron
        this.player.setItem(
                HotBarSlot.HERO_ITEM,
                new ItemBuilder(engineer.getPassiveTalent().getMaterial())
                        .setName("&fIron")
                        .addTextBlockLore("""
                                          &8Resource
                                          
                                          Used to create, upgrade, and heal own constructs.
                                          
                                          %s
                                          """.formatted(ironDescription))
                        .setAmount(Math.max(1, this.iron))
                        .asIcon()
        );
    }
    
    @Override
    public void remove() {
        iron = 0;
        
        constructs.values().forEach(Construct::remove);
        constructs.clear();
        
        if (mecha != null) {
            mecha.remove();
        }
    }
    
    public void createMechaIndustries(Engineer hero) {
        removeMechaIndustries();
        
        mecha = new MechaIndustries(player, hero);
        
        player.setItemAndSnap(HotBarSlot.TALENT_4, engineer.ironFist.createItem());
        player.cooldownManager.setCooldown(engineer.ironFist, engineer.ultimateHitCd);
        
        // fixme -> Stop hardcoding numbers -h
        player.getAttributes().addModifier(modifierSource, engineer.getUltimate(), modifier -> modifier.of(AttributeType.SPEED, ModifierType.FLAT, -25));
    }
    
    public void removeMechaIndustries() {
        if (mecha == null) {
            return;
        }
        
        player.setItem(HotBarSlot.TALENT_4, null);
        player.snapToWeapon();
        
        player.getAttributes().removeModifier(modifierSource);
        
        mecha.remove();
        mecha = null;
    }
    
    @Nullable
    public MechaIndustries getMechaIndustries() {
        return mecha;
    }
    
    public void swingMechaIndustriesHand() {
        if (mecha == null) {
            return;
        }
        
        mecha.swing();
    }
    
    public void construct(@Nonnull EngineerConstructTalent talent, @Nonnull Location location) {
        final Construct construct = talent.create(player, location);
        final Construct previousConstruct = constructs.put(talent, construct);
        
        if (previousConstruct != null) {
            previousConstruct.message("&eYour previous %s was removed!".formatted(previousConstruct.getName()));
            previousConstruct.remove();
        }
        
        setIron(iron - talent.buildCost());
        
        // Fx
        player.spawnWorldParticle(location, Particle.EXPLOSION_EMITTER, 1);
        player.playWorldSound(location, Sound.BLOCK_ANVIL_USE, 1.25f);
    }
    
    @Override
    public void tick(int tick) {
        final MagneticAttractionPassive passive = engineer.getPassiveTalent();
        
        // Increment iron
        if (tick > 0 && tick % passive.rechargeRate == 0) {
            setIron(iron + 1);
        }
        
        // Tick constructs
        constructs.values().removeIf(Construct::removeIfShould);
        constructs.values().forEach(Construct::tick);
    }
    
    @Nullable
    @Override
    public List<String> getStrings(@Nonnull GamePlayer player) {
        final Construct constructSentry = constructs.get(engineer.getFirstTalent());
        final Construct constructDispenser = constructs.get(engineer.getSecondTalent());
        
        return List.of(
                mecha != null ? mecha.toString() : "",
                constructSentry != null ? "\uD83C\uDFAF " + constructSentry : "",
                constructDispenser != null ? "⛨ " + constructDispenser : ""
        );
    }
}
