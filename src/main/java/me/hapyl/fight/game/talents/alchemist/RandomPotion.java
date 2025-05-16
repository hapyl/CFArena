package me.hapyl.fight.game.talents.alchemist;

import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.MapMaker;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.heroes.alchemist.AlchemistState;
import me.hapyl.fight.game.loadout.HotBarSlot;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;
import org.bukkit.Sound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public class RandomPotion extends Talent {
    
    public final Map<HotBarSlot, AlchemistPotion> potionMap;
    
    @DisplayField public final int castDuration = 10;
    
    public RandomPotion(@Nonnull Key key) {
        super(key, "Abyssal Bottle");
        
        // blame sdimas74 he said to make it center it WASN'T ME!!
        this.potionMap = MapMaker.<HotBarSlot, AlchemistPotion>ofLinkedHashMap()
                                 .put(HotBarSlot.TALENT_2, new AlchemistPotionHealing())
                                 .put(HotBarSlot.TALENT_3, new AlchemistPotionSpeed())
                                 .put(HotBarSlot.TALENT_4, new AlchemistPotionAttack())
                                 .put(HotBarSlot.TALENT_5, new AlchemistPotionInvisibility())
                                 .put(HotBarSlot.HERO_ITEM, new AlchemistPotionDefense())
                                 .makeImmutableMap();
        
        setDescription("""
                       Shake the &dabyssal bottle&7 to conjure five potent potions.
                       &8&o;;The potions will replace your talents, awaiting your choice for use.
                       
                       Shake the bottle again to store the potions back.
                       
                       &6Available Potions:
                       """);
        
        setType(TalentType.ENHANCE);
        setMaterial(Material.OMINOUS_BOTTLE);
        
        setCooldownSec(10f);
    }
    
    @Override
    public void juiceDescription(@Nonnull ItemBuilder builder) {
        potionMap.forEach((slot, potion) -> {
            builder.addTextBlockLore("""
                                     &8 › &a%s
                                     """.formatted(potion.getName())
            );
        });
        
        builder.addTextBlockLore("""
                                 &8&o;;Hover over the potions for description.
                                 """);
    }
    
    @Override
    public @Nullable Response execute(@Nonnull GamePlayer player) {
        startCdIndefinitely(player);
        
        HeroRegistry.ALCHEMIST.setState(player, AlchemistState.CHOOSING_POTION);
        
        // Give potion item
        potionMap.forEach((slot, potion) -> {
            player.setItem(slot, potion.getPotionItem());
        });
        
        player.snapToWeapon();
        
        // Fx
        player.playWorldSound(Sound.ITEM_ARMOR_EQUIP_CHAIN, 0.75f);
        
        return Response.AWAIT;
    }
}
