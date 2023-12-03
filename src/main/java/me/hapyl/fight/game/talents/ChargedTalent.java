package me.hapyl.fight.game.talents;

import com.google.common.collect.Maps;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Map;

/**
 * Represents a talent with multiple charges.
 */
public class ChargedTalent extends Talent {

    private final int maxCharges;
    private final Map<Player, ChargedTalentData> data;
    private int rechargeTime;
    private Material noChargedMaterial;

    public ChargedTalent(String name, int maxCharges) {
        this(name, "", maxCharges);
    }

    public ChargedTalent(String name, String description, int maxCharges) {
        super(name, description, Type.COMBAT_CHARGED);
        this.maxCharges = maxCharges;
        this.data = Maps.newHashMap();
        //        this.chargesAvailable = new HashMap<>();
        this.rechargeTime = -1; // -1 = does not recharge (manual)
        this.noChargedMaterial = Material.CHARCOAL;
    }

    public ChargedTalentData getData(Player player) {
        return data.computeIfAbsent(player, data -> new ChargedTalentData(player, this));
    }

    public Material getNoChargedMaterial() {
        return noChargedMaterial;
    }

    public void setNoChargedMaterial(Material noChargedMaterial) {
        this.noChargedMaterial = noChargedMaterial;
    }

    public void onStartCharged() {
    }

    public void onStopCharged() {
    }

    public void onDeathCharged(Player player) {
    }

    @Override
    public final void onStart() {
        onStartCharged();
    }

    @Override
    public final void onDeath(Player player) {
        getData(player).reset();
        data.remove(player);

        onDeathCharged(player);
    }

    @Override
    public final void onStop() {
        data.forEach((p, d) -> d.reset());
        data.clear();

        onStopCharged();
    }

    public void onLastCharge(Player player) {
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

    public int getLastKnownSlot(Player player) {
        return getData(player).getLastKnownSlot();
    }

    public void setLastKnownSlot(Player player, int slot) {
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

    public int getChargedAvailable(Player player) {
        return getData(player).getChargedAvailable();
    }

    public void removeChargeAndStartCooldown(Player player) {
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
                GamePlayer.setCooldown(player, noChargedMaterial, getRechargeTime());
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

    public void grantAllCharges(Player player, int delay) {
        GameTask.runLater(() -> grantAllCharges(player), GamePlayer.scaleCooldown(player, delay));
    }

    public void grantAllCharges(Player player) {
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
        PlayerLib.playSound(player, Sound.ENTITY_CHICKEN_EGG, 1.0f);
    }

    public void grantCharge(Player player, int delay) {
        GameTask.runLater(() -> grantCharge(player), delay);
    }

    public void grantCharge(Player player) {
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
        PlayerLib.playSound(player, Sound.ENTITY_CHICKEN_EGG, 1.0f);
    }

    public void resetCooldown(Player player) {
        getData(player).reset();
    }

    @Override
    public Response execute(Player player) {
        return Response.AWAIT;
    }

    private ItemStack noChargesItem() {
        return ItemBuilder.of(noChargedMaterial, "&cOut of Charged!").build();
    }

}

