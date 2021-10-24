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
			.hideFlags()
			.toItemStack();

	private final int maxCharges;
	private final Map<Player, Integer> chargesAvailable;
	private int rechargeTime;

	// queue
	private GameTask currentTask;
	private int queueTask;

	private final Map<Player, Integer> lastKnownSlot = new HashMap<>(); // this used to track last slot of an ability item to replace it manually

	public ChargedTalent(String name, int maxCharges) {
		this(name, "", maxCharges);
	}

	public ChargedTalent(String name, String description, int maxCharges) {
		super(name, description, Type.COMBAT_CHARGED);
		this.maxCharges = maxCharges;
		this.chargesAvailable = new HashMap<>();
		this.rechargeTime = -1; // -1 = does not recharge (manual)
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

	/**
	 * @deprecated does not recharge by default
	 */
	@Deprecated
	public void setDoesNotRecharge() {
		this.setRechargeTime(-1);
	}

	public int getLastKnownSlot(Player player) {
		return lastKnownSlot.getOrDefault(player, -1);
	}

	public void setLastKnownSlot(Player player, int slot) {
		if (getLastKnownSlot(player) == slot) {
			return;
		}
		lastKnownSlot.put(player, slot);
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

	public void removeChargeAndStartCooldown(Player player) {
		final int slot = getLastKnownSlot(player);
		final PlayerInventory inventory = player.getInventory();
		final ItemStack item = inventory.getItem(slot);

		this.chargesAvailable.put(player, getChargedAvailable(player) - 1);

		if (item == null) {
			return;
		}

		final int amount = item.getAmount();

		if (amount == 1) {
			inventory.setItem(slot, noChargesItem);
			if (getRechargeTime() >= 0) {
				player.setCooldown(noChargesItem.getType(), getRechargeTime());
			}
		}
		else {
			item.setAmount(amount - 1);
		}

		// if recharge time is -1 = ability does not recharge
		if (getRechargeTime() <= -1) {
			return;
		}

		if (currentTask != null) {
			++queueTask;
			return;
		}

		createAndStartTask(player);
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

		if (item.isSimilar(noChargesItem)) {
			inventory.setItem(slot, this.getItem());
		}
		else {
			item.setAmount(item.getAmount() + 1);
		}

		chargesAvailable.put(player, getChargedAvailable(player) + 1);

		// Fx
		PlayerLib.playSound(player, Sound.ENTITY_CHICKEN_EGG, 1.0f);
	}

	private void createAndStartTask(Player player) {
		currentTask = new GameTask() {
			@Override
			public void run() {
				// start another task
				if (queueTask >= 1) {
					--queueTask;
					createAndStartTask(player);
				}
				// nullate tasks and queue
				else {
					currentTask = null;
					queueTask = 0;
				}

				grantCharge(player);
			}
		}.runTaskLater(rechargeTime);
	}


	@Override
	public Response execute(Player player) {
		return Response.AWAIT;
	}

}

