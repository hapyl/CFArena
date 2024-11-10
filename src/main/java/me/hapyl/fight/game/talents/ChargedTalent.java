package me.hapyl.fight.game.talents;


import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.annotate.DoNotMutate;
import me.hapyl.fight.game.Event;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.loadout.HotBarSlot;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.collection.player.PlayerMap;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.OverridingMethodsMustInvokeSuper;

/**
 * Represents a talent with multiple charges.
 */
public class ChargedTalent extends Talent {

    private final int maxCharges;
    private final PlayerMap<ChargedTalentData> data;
    private int rechargeTime;
    private ItemStack noChargesItem;

    public ChargedTalent(@Nonnull Key key, @Nonnull String name, int maxCharges) {
        super(key, name);

        this.maxCharges = maxCharges;
        this.data = PlayerMap.newMap();
        this.rechargeTime = -1; // -1 = does not recharge (manual)
        this.noChargesItem = makeNoChargesItem(Material.CHARCOAL);
    }

    public ChargedTalentData getData(GamePlayer player) {
        return data.computeIfAbsent(player, data -> new ChargedTalentData(player, this));
    }

    public ItemStack getNoChargesItem() {
        return noChargesItem;
    }

    public void setNoChargesItem(Material noChargesItem) {
        this.noChargesItem = makeNoChargesItem(noChargesItem);
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void onStart(@Nonnull GameInstance instance) {
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void onStop(@Nonnull GameInstance instance) {
        data.forEach((p, d) -> d.reset());
        data.clear();
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void onDeath(@Nonnull GamePlayer player) {
        getData(player).reset();
        data.remove(player);
    }

    @Event
    public void onLastCharge(@Nonnull GamePlayer player) {
    }

    public void setRechargeTimeSec(int i) {
        setRechargeTime(i * 20);
    }

    /**
     * @deprecated does not recharge by default
     */
    @Deprecated
    public void setDoesNotRecharge() {
        this.setRechargeTime(-1);
    }

    public int getMaxCharges() {
        return maxCharges;
    }

    public int getRechargeTime() {
        return rechargeTime;
    }

    protected void setRechargeTime(int i) {
        this.rechargeTime = i;
    }

    public int getChargesAvailable(GamePlayer player) {
        return getData(player).getChargesAvailable();
    }

    public void removeChargeAndStartCooldown(GamePlayer player) {
        final HotBarSlot slot = getData(player).getLastKnownSlot();

        // Illegal call
        if (slot == null) {
            return;
        }

        final ItemStack item = player.getItem(slot);

        getData(player).removeCharge();

        if (item == null) {
            return;
        }

        final int amount = item.getAmount();

        if (amount == 1) {
            player.setItem(slot, noChargesItem());
            if (getRechargeTime() >= 0) {
                player.setCooldownInternal(noChargesItem.getType(), getRechargeTime());
            }

            onLastCharge(player);
        }
        else {
            item.setAmount(amount - 1);
        }

        // if recharge time is -1 = ability does not recharge
        if (getRechargeTime() <= -1) {
            return;
        }

        getData(player).workTask();
    }

    public void grantAllCharges(GamePlayer player, int delay) {
        GameTask.runLater(() -> grantAllCharges(player), player.scaleCooldown(delay));
    }

    public void grantAllCharges(GamePlayer player) {
        final HotBarSlot slot = getData(player).getLastKnownSlot();

        if (slot == null) {
            return;
        }

        final ItemStack item = player.getItem(slot);

        if (item == null) {
            return;
        }

        player.setItem(slot, getItem());
        final ItemStack newItem = player.getItem(slot);

        // Fix amount
        if (newItem != null) {
            newItem.setAmount(maxCharges);
        }

        getData(player).maxCharge();

        // Fx
        player.playSound(Sound.ENTITY_CHICKEN_EGG, 1);
    }

    public void grantCharge(GamePlayer player) {
        final HotBarSlot slot = getData(player).getLastKnownSlot();

        if (slot == null) {
            return;
        }

        final ItemStack item = player.getItem(slot);

        if (item == null) {
            return;
        }

        if (item.getType() == noChargesItem.getType()) {
            player.setItem(slot, getItem());
        }
        else {
            item.setAmount(item.getAmount() + 1);
        }

        getData(player).addCharge();

        // Fx
        player.playSound(Sound.ENTITY_CHICKEN_EGG, 1);
    }

    public void resetCooldown(GamePlayer player) {
        getData(player).reset();
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        return Response.AWAIT;
    }

    @DoNotMutate
    @Nonnull
    public ItemStack noChargesItem() {
        return this.noChargesItem;
    }

    @Nonnull
    @Override
    public String getTalentClassType() {
        return "Charged Talent";
    }

    private static ItemStack makeNoChargesItem(Material material) {
        return new ItemBuilder(material).setName("&4Out of Charges!").toItemStack();
    }
}

