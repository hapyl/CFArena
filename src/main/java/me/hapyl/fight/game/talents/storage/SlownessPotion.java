package me.hapyl.fight.game.talents.storage;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class SlownessPotion extends Talent {

	private final ItemStack potionItem = new ItemBuilder(Material.SPLASH_POTION)
			.setPotionColor(Color.BLACK)
			.setPotionMeta(PotionEffectType.SLOW, 5, 80, Color.GRAY)
			.build();

	public SlownessPotion() {
        super(
                "Slowness Potion",
                "A little bottle that can cause a lot of troubles. Throw a slowing potion if front of that slows enemies in small AoE."
        );
        this.setItem(Material.SPLASH_POTION, builder -> builder.setPotionColor(Color.GRAY));
        this.setCdSec(12);
	}

	@Override
	public Response execute(Player player) {

		final ThrownPotion potion = player.launchProjectile(ThrownPotion.class);
		potion.setItem(potionItem);
		potion.setShooter(player);

		return Response.OK;
	}
}
