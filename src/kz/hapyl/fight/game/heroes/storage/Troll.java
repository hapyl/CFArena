package kz.hapyl.fight.game.heroes.storage;

import kz.hapyl.fight.game.heroes.ClassEquipment;
import kz.hapyl.fight.game.heroes.Hero;
import kz.hapyl.fight.game.talents.Talent;
import kz.hapyl.fight.game.talents.Talents;
import kz.hapyl.fight.game.talents.UltimateTalent;
import kz.hapyl.fight.game.weapons.Weapon;
import kz.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Troll extends Hero {

	private final Map<Player, Set<Block>> blocks = new HashMap<>();

	public Troll() {
		super("Troll");
		this.setInfo("Not a good fighter nor a hero... but definitely a good troll!");
		this.setItem("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTYyNmMwMTljOGI0MWM3YjI0OWFlOWJiNjc2MGM0ZTY5ODAwNTFjZjBkNjg5NWNiM2U2ODQ2ZDgxMjQ1YWQxMSJ9fX0=");

		final ClassEquipment equipment = this.getEquipment();
		equipment.setHelmet("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTYyNmMwMTljOGI0MWM3YjI0OWFlOWJiNjc2MGM0ZTY5ODAwNTFjZjBkNjg5NWNiM2U2ODQ2ZDgxMjQ1YWQxMSJ9fX0=");
		equipment.setChestplate(255, 204, 84);
		equipment.setLeggings(255, 204, 84);
		equipment.setBoots(255, 204, 84);

		this.setWeapon(new Weapon(Material.STICK).setName("Stickonator")
				.setLore("- What's brown and sticky?__- What?__- A stick!__- ...")
				.setDamage(4.0)
				.addEnchant(Enchantment.KNOCKBACK, 1));

		this.setUltimate(new UltimateTalent("Sticky Situation", "Spawns a batch of cobweb at your position that only visible for your opponents.____Only one batch can exist at the same time.", 40) {

			@Override
			public void useUltimate(Player player) {
				Bukkit.getOnlinePlayers().forEach(target -> {
					if (target == player) {
						return;
					}
					Chat.sendMessage(target, "&aAh... Sticky! &e&lPUNCH &athe cobweb to remove it!");
				});
				clearCobweb(player);
				createCobweb(player);
			}

			private void clearCobweb(Player player) {
				final Set<Block> blocks = Troll.this.blocks.get(player);
				if (blocks == null) {
					return;
				}
				blocks.forEach(location -> location.getState().update(false, false));
			}

			private void createCobweb(Player player) {
				final Location location = player.getLocation().clone().subtract(2, 0, 2);
				final Set<Block> hashSet = blocks.computeIfAbsent(player, t -> new HashSet<>());

				for (int i = 0; i < 5; i++) {
					for (int j = 0; j < 5; j++) {
						location.add(i, 0, j);
						if (!location.getBlock().getType().isSolid()) {
							hashSet.add(location.getBlock());
							Bukkit.getOnlinePlayers().forEach(target -> {
								target.sendBlockChange(location, Material.COBWEB.createBlockData());
							});
						}
						location.subtract(i, 0, j);
					}
				}
			}

		}.setSound(Sound.ENTITY_SPIDER_AMBIENT, 1.0f).setCd(20));

	}

	@Override
	public void onStop() {
		blocks.values().forEach(locations -> locations.forEach(block -> block.getState().update(false, false)));
		blocks.clear();
	}

	@Override
	public Talent getFirstTalent() {
		return Talents.TROLL_SPIN.getTalent();
	}

	@Override
	public Talent getSecondTalent() {
		return Talents.REPULSOR.getTalent();
	}

	@Override
	public Talent getPassiveTalent() {
		return Talents.TROLL_PASSIVE.getTalent();
	}
}
