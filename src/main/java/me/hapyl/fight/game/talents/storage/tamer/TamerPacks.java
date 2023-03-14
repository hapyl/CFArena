package me.hapyl.fight.game.talents.storage.tamer;

import me.hapyl.fight.util.Nulls;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.util.CollectionUtils;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public enum TamerPacks {

	ZOMBIE_HORDE(new TamerPack("Zombie Horde") {
		@Override
		public void spawn(Player player) {
			for (int i = 0; i < 3; i++) {
				final Location location = this.addRelativeOffset(player.getLocation(), i);
				this.createEntity(location, Entities.ZOMBIE);
			}
		}
	}),

	SKELETON_GANG(new TamerPack("Skeleton Gang") {
		@Override
		public void spawn(Player player) {
			for (int i = 0; i < 3; i++) {
				this.createEntity(
						this.addRelativeOffset(player.getLocation(), i),
						Entities.SKELETON,
						me -> Nulls.runIfNotNull(me.getEquipment(), eq -> {
							eq.setItemInMainHand(new ItemBuilder(Material.BOW).addEnchant(Enchantment.ARROW_DAMAGE, 1)
									.addEnchant(Enchantment.ARROW_KNOCKBACK, 1)
									.setUnbreakable()
									.build());
						})
				);
			}
		}
	}),

	PIGMAN_RUSHED(new TamerPack("Pigman Rusher") {
		@Override
		public void spawn(Player player) {
			this.createEntity(player.getLocation(), Entities.ZOMBIFIED_PIGLIN, me -> {
				Nulls.runIfNotNull(me.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED), at -> at.setBaseValue(0.5d));
				me.setAngry(true);
				me.setAnger(Short.MAX_VALUE);
			});
		}
	}),

	VEX(new TamerPack("Vex Brood Brothers") {
		@Override
		public void spawn(Player player) {
			for (int i = 0; i < 2; i++) {
				this.createEntity(addRelativeOffset(player.getLocation(), i), Entities.VEX);
			}
		}
	}),

	WOLVES(new TamerPack("Wolfies") {
		@Override
		public void spawn(Player player) {
			for (int i = 0; i < 4; i++) {
				this.createEntity(addRelativeOffset(player.getLocation(), i), Entities.WOLF, me -> {
					me.setOwner(player);
					me.setAngry(true);
					me.setCollarColor(DyeColor.CYAN);
					me.setAdult();
				});
			}
		}
	}),

	JOCKEY(new TamerPack("Skeleton Jockey") {
		@Override
		public void spawn(Player player) {

			final Location relative = addRelativeOffset(player.getLocation(), 0);

			final Entity skeleton = this.createEntity(relative, Entities.SKELETON, me -> {
				Nulls.runIfNotNull(
						me.getEquipment(),
						eq -> eq.setItemInMainHand(new ItemBuilder(Material.BOW).addEnchant(Enchantment.ARROW_DAMAGE, 2).build())
				);
			});

			this.createEntity(relative, Entities.SPIDER, me -> {
				Nulls.runIfNotNull(me.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED), at -> at.setBaseValue(0.2d));
				me.addPassenger(skeleton);
			});

		}
	}),

	CHICKEN_RIDER(new TamerPack("Chicken Rider") {
		@Override
		public void spawn(Player player) {

			final Location relative = addRelativeOffset(player.getLocation(), 0);

			final Entity zombie = this.createEntity(relative, Entities.ZOMBIE, me -> {
				Nulls.runIfNotNull(
						me.getEquipment(),
						eq -> eq.setItemInMainHand(new ItemBuilder(Material.NETHERITE_SWORD).setPureDamage(2.0d).build())
				);
				me.setBaby();
			});

			this.createEntity(relative, Entities.CHICKEN, me -> me.addPassenger(zombie));
		}
	});

	private final TamerPack pack;

	TamerPacks(TamerPack pack) {
		this.pack = pack;
	}

	public TamerPack getPack() {
		return pack;
	}

	public static TamerPack newRandom() {
		return CollectionUtils.randomElement(values(), ZOMBIE_HORDE).getPack();
	}

}
