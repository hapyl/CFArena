package kz.hapyl.fight.game.heroes.storage.extra;

import kz.hapyl.spigotutils.module.math.Numbers;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.util.HashMap;
import java.util.Map;

public class Shield {

	private final Map<Integer, ItemStack> shieldMap;

	public Shield() {
		shieldMap = new HashMap<>();
		this.createShields();
	}

	private void createShields() {
		// 0
		shieldMap.put(0, new Builder()
				.addPattern(PatternType.STRIPE_BOTTOM)
				.addPattern(PatternType.STRIPE_LEFT)
				.addPattern(PatternType.STRIPE_TOP)
				.addPattern(PatternType.STRIPE_RIGHT)
				.addPattern(DyeColor.WHITE, PatternType.BORDER)
				.build());
		// 1
		shieldMap.put(
				1,
				new Builder()
						.addPattern(PatternType.STRIPE_CENTER)
						.addPattern(PatternType.SQUARE_TOP_LEFT)
						.addPattern(DyeColor.WHITE, PatternType.CURLY_BORDER)
						.addPattern(PatternType.STRIPE_BOTTOM)
						.addPattern(DyeColor.WHITE, PatternType.BORDER)
						.build()
		);

		// 2
		shieldMap.put(2, new Builder()
				.addPattern(PatternType.STRIPE_TOP)
				.addPattern(DyeColor.WHITE, PatternType.RHOMBUS_MIDDLE)
				.addPattern(PatternType.STRIPE_BOTTOM)
				.addPattern(PatternType.STRIPE_DOWNLEFT)
				.addPattern(DyeColor.WHITE, PatternType.BORDER)
				.build());

		// 3
		shieldMap.put(3, new Builder()
				.addPattern(PatternType.STRIPE_BOTTOM)
				.addPattern(PatternType.STRIPE_MIDDLE)
				.addPattern(PatternType.STRIPE_TOP)
				.addPattern(DyeColor.WHITE, PatternType.CURLY_BORDER)
				.addPattern(PatternType.STRIPE_RIGHT)
				.addPattern(DyeColor.WHITE, PatternType.BORDER)
				.build());

		// 4
		shieldMap.put(4, new Builder()
				.addPattern(PatternType.STRIPE_LEFT)
				.addPattern(DyeColor.WHITE, PatternType.HALF_HORIZONTAL_MIRROR)
				.addPattern(PatternType.STRIPE_RIGHT)
				.addPattern(PatternType.STRIPE_MIDDLE)
				.addPattern(DyeColor.WHITE, PatternType.BORDER)
				.build());

		// 5
		shieldMap.put(5, new Builder()
				.addPattern(PatternType.STRIPE_BOTTOM)
				.addPattern(DyeColor.WHITE, PatternType.RHOMBUS_MIDDLE)
				.addPattern(PatternType.STRIPE_TOP)
				.addPattern(PatternType.STRIPE_DOWNRIGHT)
				.addPattern(DyeColor.WHITE, PatternType.BORDER)
				.build());

		// 6
		shieldMap.put(6, new Builder()
				.addPattern(PatternType.STRIPE_BOTTOM)
				.addPattern(PatternType.STRIPE_RIGHT)
				.addPattern(DyeColor.WHITE, PatternType.HALF_HORIZONTAL)
				.addPattern(PatternType.STRIPE_MIDDLE)
				.addPattern(PatternType.STRIPE_TOP)
				.addPattern(PatternType.STRIPE_LEFT)
				.addPattern(DyeColor.WHITE, PatternType.BORDER)
				.build());

		// 7
		shieldMap.put(7, new Builder()
				.addPattern(PatternType.STRIPE_DOWNLEFT)
				.addPattern(PatternType.STRIPE_TOP)
				.addPattern(DyeColor.WHITE, PatternType.BORDER)
				.build());

		// 8
		shieldMap.put(8, new Builder()
				.addPattern(PatternType.STRIPE_TOP)
				.addPattern(PatternType.STRIPE_LEFT)
				.addPattern(PatternType.STRIPE_MIDDLE)
				.addPattern(PatternType.STRIPE_BOTTOM)
				.addPattern(PatternType.STRIPE_RIGHT)
				.addPattern(DyeColor.WHITE, PatternType.BORDER)
				.build());

		// 9
		shieldMap.put(9, new Builder()
				.addPattern(PatternType.STRIPE_LEFT)
				.addPattern(DyeColor.WHITE, PatternType.HALF_HORIZONTAL_MIRROR)
				.addPattern(PatternType.STRIPE_MIDDLE)
				.addPattern(PatternType.STRIPE_TOP)
				.addPattern(PatternType.STRIPE_RIGHT)
				.addPattern(PatternType.STRIPE_BOTTOM)
				.addPattern(DyeColor.WHITE, PatternType.BORDER)
				.build());

	}

	public void updateTexture(Player player, int charge) {
		charge = Numbers.clamp(charge, 0, 9);
		final ItemStack offHand = player.getInventory().getItem(EquipmentSlot.OFF_HAND);

		if (offHand.getType() == Material.SHIELD) {
			offHand.setItemMeta(shieldMap.get(charge).getItemMeta());
		}
	}

	private static class Builder {

		private final ItemStack stack;
		private final BlockStateMeta meta;
		private final Banner banner;

		public Builder() {
			stack = new ItemStack(Material.SHIELD);
			meta = (BlockStateMeta)stack.getItemMeta();

			if (meta == null) {
				throw new NullPointerException("meta null");
			}

			banner = (Banner)meta.getBlockState();

		}

		public Builder addPattern(PatternType type) {
			return addPattern(DyeColor.BLACK, type);
		}

		public Builder addPattern(DyeColor color, PatternType type) {
			return addPattern(new Pattern(color, type));
		}

		public Builder addPattern(Pattern pattern) {
			banner.addPattern(pattern);
			return this;
		}

		public ItemStack build() {
			banner.update(false, false);
			meta.setBlockState(banner);
			stack.setItemMeta(meta);
			return stack;
		}

	}

}
