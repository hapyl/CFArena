package kz.hapyl.fight.game.talents.storage;

import kz.hapyl.fight.game.GamePlayer;
import kz.hapyl.fight.game.Response;
import kz.hapyl.fight.game.heroes.Heroes;
import kz.hapyl.fight.game.heroes.storage.Alchemist;
import kz.hapyl.fight.game.heroes.storage.extra.CauldronEffect;
import kz.hapyl.fight.game.talents.Talent;
import kz.hapyl.fight.game.talents.storage.extra.Effect;
import kz.hapyl.fight.util.RandomTable;
import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import static org.bukkit.potion.PotionEffectType.*;

public class RandomPotion extends Talent {

	private final RandomTable<Effect> effects = new RandomTable<>();

	public RandomPotion() {
		super("Abyssal Bottle", "A bottle that is capable of creating potions from the &0&lvoid &7itself.", Type.COMBAT);
		this.setItem(Material.POTION);
		this.setCd(50);

		effects.add(new Effect("&b\uD83C\uDF0A", "Speed Boost", SPEED, 60, 1))
				.add(new Effect("☕", "Jump Boost", JUMP, 100, 1))
				.add(new Effect("&c⚔", "Strength", INCREASE_DAMAGE, 60, 5))
				.add(new Effect("&6🛡", "Resistance", DAMAGE_RESISTANCE, 80, 1))
				.add(new Effect("&9⁘⁙", "Invisibility", INVISIBILITY, 80, 1))
				.add(new Effect("&c❤", "Healing") {
					@Override
					public void affect(Player player) {
						GamePlayer.getPlayerSafe(player).heal(10);
					}
				});

	}

	@Override
	public Response execute(Player player) {
		final Alchemist hero = (Alchemist)Heroes.ALCHEMIST.getHero();
		this.effects.getRandomElement().applyEffects(player);
		hero.addToxinForUsingPotion(player);

		final CauldronEffect effect = hero.getEffect(player);
		if (effect != null && effect.getDoublePotion() > 0) {
			effect.decrementDoublePotions();
			Chat.sendMessage(player, "&e☕ &aDouble Potion has %s changes left, gained:", effect.getDoublePotion());
			PlayerLib.playSound(player, Sound.ITEM_BOTTLE_FILL, 1.25f);
			this.effects.getRandomElement().applyEffects(player);
		}

		return Response.OK;
	}
}
