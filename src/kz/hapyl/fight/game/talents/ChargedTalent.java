package kz.hapyl.fight.game.talents;

import kz.hapyl.fight.game.Response;
import kz.hapyl.fight.game.task.GameTask;
import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Map;

public class ChargedTalent extends Talent {

	private final ItemStack noChargesItem = new ItemBuilder(Material.CHARCOAL)
			.setName("&aOut of Charges")
			.setLore("This talent is currently recharging!")
			.hideFlags()
			.toItemStack();

	private final int maxCharges;
	private final Map<Player, Integer> chargesAvailable;
	private int rechargeTime;

	public ChargedTalent(String name, String description, int maxCharges) {
		super(name, description, Type.COMBAT);
		this.maxCharges = maxCharges;
		this.chargesAvailable = new HashMap<>();
		this.rechargeTime = 0;
	}

	@Override
	public void onStop() {
		chargesAvailable.clear();
	}

	public void setRechargeTime(int i) {
		this.rechargeTime = i;
	}

	public void setRechargeTimeSec(int i) {
		setRechargeTime(i * 20);
	}

	public int getMaxCharges() {
		return maxCharges;
	}

	public int getRechargeTime() {
		return rechargeTime;
	}

	public int getChargedAvailable(Player player) {
		this.chargesAvailable.putIfAbsent(player, maxCharges);
		return this.chargesAvailable.get(player);
	}

	public void removeChargeAndStartCooldown(Player player, int slot) {
		this.chargesAvailable.put(player, getChargedAvailable(player) - 1);
		final PlayerInventory inventory = player.getInventory();
		final ItemStack item = inventory.getItem(slot);
		if (item == null) {
			return;
		}

		final int amount = item.getAmount();
		if (amount == 1) {
			inventory.setItem(slot, noChargesItem);
		}
		else {
			item.setAmount(amount - 1);
		}

		// give item back
		new GameTask() {
			@Override
			public void run() {

				final ItemStack item = inventory.getItem(slot);
				if (item == null) {
					return;
				}

				// give original item back
				if (item.isSimilar(noChargesItem)) {
					inventory.setItem(slot, getItem());
				}
				else {
					item.setAmount(item.getAmount() + 1);
				}

				chargesAvailable.put(player, getChargedAvailable(player) + 1);

				// Fx
				PlayerLib.playSound(player, Sound.ENTITY_CHICKEN_EGG, 1.0f);

			}
		}.runTaskLater(this.getRechargeTime());
	}

	@Override
	public Response execute(Player player) {
		return null;
	}
}
