package me.hapyl.fight.game.talents.storage;

import me.hapyl.fight.game.AbstractGamePlayer;
import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.util.CollectionUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class FlowerBreeze extends Talent {

	private final int effectDuration = 80;
	private final double damageAmount = 15.0d;

	public FlowerBreeze() {
		// Feel the breeze of the flowers that damages your but grants &cStrength &7and &bResistance &7for a short duration. This ability cannot kill.
		super("Flower Breeze", "Feel the breeze of the flowers that damages your but grants &cStrength &7and &bResistance &7for a short duration.__This ability cannot kill.", Type.COMBAT);
		this.setItem(Material.RED_DYE);
		this.setCd(effectDuration * 4);
	}

	private final Material[] flowers = {Material.POPPY, Material.DANDELION, Material.ALLIUM, Material.RED_TULIP, Material.ORANGE_TULIP, Material.PINK_TULIP, Material.WHITE_TULIP, Material.OXEYE_DAISY, Material.CORNFLOWER, Material.AZURE_BLUET};

	@Override
	public Response execute(Player player) {
		final Location location = player.getLocation();
		PlayerLib.playSound(location, Sound.ENTITY_HORSE_BREATHE, 0.0f);
		player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10, 2));

		final World world = location.getWorld();
		final AbstractGamePlayer gp = GamePlayer.getPlayer(player);

		// can't go lower than 1 heart
		gp.setHealth(Math.max(2, gp.getHealth() - damageAmount));

		if (world != null) {
			for (int i = 0; i < 20; i++) {
				final Item item = world.dropItemNaturally(location, new ItemStack(CollectionUtils.randomElement(flowers, flowers[0])));
				item.setPickupDelay(10000);
				item.setTicksLived(5900);
			}
		}

		//fx
		PlayerLib.addEffect(player, PotionEffectType.DAMAGE_RESISTANCE, effectDuration, 1);
		PlayerLib.addEffect(player, PotionEffectType.INCREASE_DAMAGE, effectDuration, 0);

		return Response.OK;
	}
}
