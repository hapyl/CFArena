package me.hapyl.fight.game.talents.harbinger;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Constants;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.attribute.ModifierSource;
import me.hapyl.fight.game.attribute.ModifierType;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.heroes.harbinger.Harbinger;
import me.hapyl.fight.game.heroes.harbinger.HarbingerData;
import me.hapyl.fight.game.heroes.harbinger.StanceData;
import me.hapyl.fight.game.loadout.HotBarSlot;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;
import org.bukkit.Sound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MeleeStance extends Talent {
    
    @DisplayField public final int maxDuration = 600;
    
    @DisplayField private final int minimumCd = 60;
    @DisplayField private final int cdPerSecond = 30;
    @DisplayField private final double critChanceIncrease = 50;
    
    private final ModifierSource modifierSource = new ModifierSource(Key.ofString("melee_stance"), false);
    
    private final Weapon abilityItem = Weapon.builder(Material.DIAMOND_SWORD, Key.ofString("raging_blade"))
                                             .name(Color.STANCE_RANGE + "Raging Blade")
                                             .description("A blade forged from pure water.")
                                             .damage(8.0d)
                                             .build();
    
    public MeleeStance(@Nonnull Key key) {
        super(key, "Melee Stance");
        
        setDescription("""
                       Enter %1$s for maximum of &b{maxDuration}&7 to replace your bow with %2$s!
                       &8&o;;Also gain a %3$s increase while in this stance.
                       
                       Use again in %1$s to get your bow back.
                       
                       &8&o;;The longer you're in Melee Stance, the longer the cooldown of this ability.
                       """.formatted(Named.STANCE_MELEE, abilityItem.getName(), AttributeType.CRIT_CHANCE.getName())
        );
        
        setType(TalentType.ENHANCE);
        setMaterial(Material.PRISMARINE_SHARD);
        
        setCooldown(-1);
        setPoint(0);
    }
    
    @Override
    public @Nullable Response execute(@Nonnull GamePlayer player) {
        final HarbingerData data = HeroRegistry.HARBINGER.getPlayerData(player);
        
        switchTo(player, data.stance == null);
        return Response.OK;
    }
    
    public void switchTo(@Nonnull GamePlayer player, boolean toMelee) {
        final Harbinger harbinger = HeroRegistry.HARBINGER;
        final HarbingerData data = harbinger.getPlayerData(player);
        final EntityAttributes attributes = player.getAttributes();
        
        if (data.stance != null) {
            data.stance.cancel();
        }
        
        if (toMelee) {
            data.stance = new StanceData(player, this);
            player.setItemAndSnap(HotBarSlot.WEAPON, abilityItem.createItem());
            
            // Add modifier
            attributes.addModifier(
                    modifierSource, Constants.INFINITE_DURATION, modifier -> modifier.of(AttributeType.CRIT_CHANCE, ModifierType.FLAT, critChanceIncrease)
            );
            
            // Fix instant use
            startCooldown(player, 10);
            
            // Fx
            player.playWorldSound(Sound.ITEM_SHIELD_BREAK, 1.25f);
            player.sendTitle("&2⚔", "", 5, 15, 5);
        }
        else {
            // Remove modifier
            attributes.removeModifier(modifierSource);
            
            startCooldown(player, calculateCooldown(data.stance));
            player.setItemAndSnap(HotBarSlot.WEAPON, harbinger.getWeapon().createItem());
            
            player.playWorldSound(Sound.ENTITY_ARROW_SHOOT, 0.75f);
            player.sendTitle("&2🏹", "", 5, 15, 5);
            
            data.stance = null;
        }
    }
    
    public int calculateCooldown(@Nonnull StanceData data) {
        final long ctm = System.currentTimeMillis();
        final long usedAt = data.usedAt();
        
        return (int) ((ctm - usedAt) / 1000 * cdPerSecond + minimumCd);
    }
    
}
