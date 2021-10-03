package kz.hapyl.fight.game.heroes.storage.extra;

import kz.hapyl.fight.game.talents.storage.extra.Element;
import kz.hapyl.fight.game.talents.storage.extra.ElementType;
import kz.hapyl.fight.game.task.GameTask;
import kz.hapyl.fight.game.weapons.Weapon;
import kz.hapyl.spigotutils.module.annotate.NULLABLE;
import kz.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class GravityGun extends Weapon {

	private final Map<Player, Element> elements = new HashMap<>();

	public GravityGun() {
		super(Material.IRON_HORSE_ARMOR);
		this.setDamage(0.0d);
		this.setId("dr_ed_gun");
		this.setName("Dr. Ed's Gravity Energy Capacitor Mk. 3");
		this.setLore("A tool that is capable of absorbing blocks elements.____&e&lRIGHT CLICK &7a block to harvest element from it.____&e&lRIGHT CLICK &7again with element equipped to launch it forward, damaging up to &bone &7opponents on it's way. The damage and cooldown is scaled based on the element.");
		GameTask.scheduleCancelTask(() -> {
			elements.values().forEach(Element::remove);
			elements.clear();
		});
	}

	@NULLABLE
	private Element getElement(Player player) {
		return elements.getOrDefault(player, null);
	}

	private boolean hasElement(Player player) {
		return this.getElement(player) != null;
	}

	public void setElement(Player player, @NULLABLE Element element) {
		if (element == null) {
			this.elements.remove(player);
			return;
		}
		this.elements.put(player, element);
	}

	@Override
	public void onRightClick(Player player, ItemStack item) {
		if (player.hasCooldown(this.getItem().getType())) {
			return;
		}

		final Block targetBlock = player.getTargetBlockExact(7);

		// throw
		if (hasElement(player)) {
			final Element element = getElement(player);
			element.stopTask();
			element.throwEntity();
			this.setElement(player, null);
			return;
		}

		// pick up
		if (targetBlock == null) {
			Chat.sendMessage(player, "&cNo valid block in sight!");
			return;
		}

		if (ElementType.getElementOf(targetBlock.getType()) == ElementType.NULL) {
			Chat.sendMessage(player, "&cTarget block does not have any valid elements...");
			return;
		}

		if (!targetBlock.getType().isBlock()) {
			Chat.sendMessage(player, "&cTarget block is not a block?");
			return;
		}

		final Element element = new Element(player, targetBlock);
		// fix instant throw
		player.setCooldown(this.getItem().getType(), 2);
		element.startTask();
		setElement(player, element);
		Chat.sendMessage(player, "&aPicked up element of %s!", Chat.capitalize(targetBlock.getType()));

	}
}
