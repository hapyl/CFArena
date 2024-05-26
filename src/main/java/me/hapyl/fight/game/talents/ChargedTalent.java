package me.hapyl.fight.game.talents;

import me.hapyl.fight.game.Event;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import javax.annotation.Nonnull;
import javax.annotation.OverridingMethodsMustInvokeSuper;

/**
 * Represents a talent with multiple charges.
 */
public class ChargedTalent extends Talent {

    private final int maxCharges;
    private final PlayerMap<ChargedTalentData> data;
    private int rechargeTime;
    private Material noChargedMaterial;

    public ChargedTalent(@Nonnull String name, int maxCharges) {
        this(name, "", maxCharges);
    }

    public ChargedTalent(@Nonnull String name, @Nonnull String description, int maxCharges) {
        this(name, description, maxCharges, TalentType.DAMAGE);
    }

    public ChargedTalent(@Nonnull String name, @Nonnull String description, int maxCharges, @Nonnull TalentType type) {
        super(name, description, type);

        this.maxCharges = maxCharges;
        this.data = PlayerMap.newMap();
        this.rechargeTime = -1; // -1 = does not recharge (manual)
        this.noChargedMaterial = Material.CHARCOAL;
    }

    public ChargedTalentData getData(GamePlayer player) {
        return data.computeIfAbsent(player, data -> new ChargedTalentData(player, this));
    }

    public Material getNoChargedMaterial() {
        return noChargedMaterial;
    }

    public void setNoChargedMaterial(Material noChargedMaterial) {
        this.noChargedMaterial = noChargedMaterial;
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void onStart() {
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void onStop() {
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

    public int getLastKnownSlot(GamePlayer player) {
        return getData(player).getLastKnownSlot();
    }

    public void setLastKnownSlot(GamePlayer player, int slot) {
        if (getLastKnownSlot(player) == slot) {
            return;
        }

        getData(player).setLastKnownSlot(slot);
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
        final int slot = getLastKnownSlot(player);
        final PlayerInventory inventory = player.getInventory();
        final ItemStack item = inventory.getItem(slot);

        getData(player).removeCharge();

        if (item == null) {
            return;
        }

        final int amount = item.getAmount();

        if (amount == 1) {
            inventory.setItem(slot, noChargesItem());
            if (getRechargeTime() >= 0) {
                player.setCooldown(noChargedMaterial, getRechargeTime());
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
        final PlayerInventory inventory = player.getInventory();
        final int slot = getLastKnownSlot(player);

        if (slot == -1) {
            return;
        }

        final ItemStack item = inventory.getItem(slot);
        if (item == null) {
            return;
        }

        inventory.setItem(slot, this.getItem());
        final ItemStack newItem = inventory.getItem(slot);

        if (newItem != null) {
            newItem.setAmount(maxCharges);
        }

        getData(player).maxCharge();

        // Fx
        player.playSound(Sound.ENTITY_CHICKEN_EGG, 1);
    }

    public void grantCharge(GamePlayer player, int delay) {
        GameTask.runLater(() -> grantCharge(player), delay);
    }

    public void grantCharge(GamePlayer player) {
        final PlayerInventory inventory = player.getInventory();
        final int slot = getLastKnownSlot(player);

        if (slot == -1) {
            return;
        }

        final ItemStack item = inventory.getItem(slot);
        if (item == null) {
            return;
        }

        if (item.getType() == noChargedMaterial) {
            inventory.setItem(slot, this.getItem());
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

    @Nonnull
    public ItemStack noChargesItem() {
        return ItemBuilder.of(noChargedMaterial, "&cOut of Charged!").build();
    }

    @Nonnull
    @Override
    public String getTalentClassType() {
        return "Charged Talent";
    }
}

