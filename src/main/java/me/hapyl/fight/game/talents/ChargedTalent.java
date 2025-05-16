package me.hapyl.fight.game.talents;

import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.loadout.HotBarSlot;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.collection.player.PlayerMap;
import org.bukkit.Sound;

import javax.annotation.Nonnull;
import javax.annotation.OverridingMethodsMustInvokeSuper;

/**
 * Represents a talent with multiple charges.
 */
public abstract class ChargedTalent extends Talent {
    
    public static final int DOES_NOT_RECHARGE_AUTOMATICALLY = -1;
    public static final int DEFAULT_INTERNAL_COOLDOWN = 5;
    
    private final int maxCharges;
    private final PlayerMap<ChargedTalentData> dataMap;
    
    private int internalCooldown;
    
    public ChargedTalent(@Nonnull Key key, @Nonnull String name, int maxCharges) {
        super(key, name);
        
        this.maxCharges = maxCharges;
        this.internalCooldown = DEFAULT_INTERNAL_COOLDOWN;
        this.dataMap = PlayerMap.newMap();
        
        // Cooldown acts as a recharge time
        setCooldown(DOES_NOT_RECHARGE_AUTOMATICALLY);
    }
    
    public int internalCooldown() {
        return internalCooldown;
    }
    
    public void internalCooldown(int internalCooldown) {
        this.internalCooldown = Math.max(1, internalCooldown);
    }
    
    @Nonnull
    @Override
    public String cooldownString() {
        return "Recharge Time";
    }
    
    @Nonnull
    public ChargedTalentData getData(@Nonnull GamePlayer player) {
        return dataMap.computeIfAbsent(player, data -> new ChargedTalentData(player, this));
    }
    
    @Override
    @OverridingMethodsMustInvokeSuper
    public void onStart(@Nonnull GameInstance instance) {
    }
    
    @Override
    @OverridingMethodsMustInvokeSuper
    public void onStop(@Nonnull GameInstance instance) {
        dataMap.values().forEach(ChargedTalentData::remove);
        dataMap.clear();
    }
    
    @Override
    @OverridingMethodsMustInvokeSuper
    public void onDeath(@Nonnull GamePlayer player) {
        final ChargedTalentData data = dataMap.remove(player);
        
        if (data != null) {
            data.remove();
        }
    }
    
    public int maxCharges() {
        return maxCharges;
    }
    
    public int chargesLeft(@Nonnull GamePlayer player) {
        return getData(player).charges();
    }
    
    public void grantAllCharges(GamePlayer player) {
        getData(player).recharge();
    }
    
    @Nonnull
    public abstract Response execute(@Nonnull GamePlayer player, int charges);
    
    public void onLastCharge(@Nonnull GamePlayer player) {
    }
    
    @Override
    public final Response execute(@Nonnull GamePlayer player) {
        final ChargedTalentData data = getData(player);
        
        // This is called even IF the player has the cooldown because it's clearer
        if (data.charges <= 0) {
            player.playSound(Sound.BLOCK_WOODEN_DOOR_CLOSE, 0.75f);
            return Response.error("Out of Charges! Next one in %s!".formatted(CFUtils.formatTick(getCooldownTimeLeft(player))));
        }
        
        final Response response = execute(player, data.charges - 1);
        
        if (!response.isOk()) {
            return response;
        }
        
        // Decrement charge
        data.decrementCharge();
        
        // Call onLastCharge if out of charges AFTER decrement
        if (data.charges == 0) {
            onLastCharge(player);
        }
        
        // Always return await since there is no cooldown between charges anymore
        return Response.AWAIT;
    }
    
    @Nonnull
    @Override
    public String getTalentClassType() {
        return "Charged Talent";
    }
    
    @Override
    public final boolean hasCooldown(@Nonnull GamePlayer player) {
        final ChargedTalentData data = getData(player);
        
        // Don't check for cooldown if no charts
        return data.charges != 0 && super.hasCooldown(player);
    }
    
    @Override
    public void giveItem(@Nonnull GamePlayer player, @Nonnull HotBarSlot slot) {
        super.giveItem(player, slot);
        
        // Fix amount
        getData(player).updateItem();
    }
    
    public void rechargeAll(@Nonnull GamePlayer player, int cooldown) {
        startCooldown(player, cooldown);
        
        // Give ALL charges after the cooldown
        player.schedule(
                () -> getData(player).recharge(), player.cooldownManager.scaleCooldown(cooldown, false)
        );
    }
    
    @Override
    public void juice(@Nonnull ItemBuilder builder) {
        builder.setAmount(maxCharges);
    }
    
    @Override
    public void juiceDetails(@Nonnull ItemBuilder builder) {
        builder.addLore("Max Charges: &f&l%s".formatted(maxCharges));
    }
}

