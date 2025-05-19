package me.hapyl.fight.game.talents.dylan;

import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.heroes.dylan.Dylan;
import me.hapyl.fight.game.heroes.dylan.DylanData;
import me.hapyl.fight.game.heroes.dylan.DylanFamiliar;
import me.hapyl.fight.game.loadout.HotBarSlot;
import me.hapyl.fight.game.talents.Talent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public abstract class WhelpTalent extends Talent {
    
    private static final Key itemUnavaiableKey = Key.ofString("whelp_talent_item_unavailable");
    
    private ItemStack itemUnavailable;
    
    WhelpTalent(@Nonnull Key key, @Nonnull String name) {
        super(key, name);
    }
    
    @Override
    public void setDescription(@Nonnull String description) {
        super.setDescription("""
                             &3&o;;This is a whelp talent, it can only be used when %s is on the field!
                             
                             %s
                             """.formatted(Dylan.familiarName, description));
    }
    
    @Nonnull
    @Override
    public String getTalentClassType() {
        return "Whelp Talent";
    }
    
    @Nonnull
    public abstract ItemBuilder makeUnavailableBuilder(@Nonnull ItemBuilder builder);
    
    @Nonnull
    public abstract Response execute(@Nonnull GamePlayer player, @Nonnull DylanFamiliar familiar);
    
    public void giveItem(@Nonnull GamePlayer player, @Nonnull HotBarSlot slot, boolean b) {
        if (b) {
            super.giveItem(player, slot);
        }
        else {
            if (itemUnavailable == null) {
                itemUnavailable = makeUnavailableBuilder(itemFactory().description.createBuilder())
                        .setCooldownGroup(itemUnavaiableKey)
                        .asIcon();
            }
            
            player.setItem(slot, itemUnavailable);
        }
    }
    
    @Nonnull
    @Override
    public final Response execute(@Nonnull GamePlayer player) {
        final DylanData data = HeroRegistry.DYLAN.getPlayerData(player);
        final Response response = validateEzel(data);
        
        if (!response.isOk()) {
            return response;
        }
        
        return execute(player, data.familiar);
    }
    
    @Nonnull
    public static Response validateEzel(@Nonnull DylanData data) {
        if (data.familiar == null) {
            return Response.error("%s is not on the field!".formatted(Dylan.familiarName));
        }
        
        if (!data.familiar.action().isInterruptible()) {
            return Response.error("Cannot interrupt current action!");
        }
        
        if (data.familiar.selfDestruct() != null) {
            return Response.error("Cannot use during self-destruct!");
        }
        
        return Response.OK;
    }
}
