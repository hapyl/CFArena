package me.hapyl.fight.game.talents;

import com.google.common.collect.Maps;
import me.hapyl.fight.game.Response;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Map;

public class ChargedTalent extends Talent {

    private final int maxCharges;
    private int rechargeTime;
    private Material noChargedMaterial;

    private final Map<Player, ChargedTalentData> data;

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

    public void setNoChargedMaterial(Material noChargedMaterial) {
        this.noChargedMaterial = noChargedMaterial;
    }

    public Material getNoChargedMaterial() {
        return noChargedMaterial;
    }

    @Override
    public void onStop() {
        data.forEach((p, d) -> d.reset());
        data.clear();
    }

    public final void onDeathCharged(Player player) {
        getData(player).reset();
        data.remove(player);
    }

    public void setRechargeTime(int i) {
        this.rechargeTime = i;
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

    public int getChargedAvailable(Player player) {
        return getData(player).getChargedAvailable();
    }

    private ItemStack noChargesItem() {
        return ItemBuilder.of(noChargedMaterial, "&cOut of Charged!").build();
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
                player.setCooldown(noChargedMaterial, getRechargeTime());
            }
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

}

