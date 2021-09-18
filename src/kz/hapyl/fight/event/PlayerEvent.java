package kz.hapyl.fight.event;

import kz.hapyl.fight.game.GamePlayer;
import kz.hapyl.fight.game.Manager;
import kz.hapyl.fight.game.Response;
import kz.hapyl.fight.game.database.Database;
import kz.hapyl.fight.game.heroes.Hero;
import kz.hapyl.fight.game.heroes.Heroes;
import kz.hapyl.fight.game.talents.Talent;
import kz.hapyl.fight.game.ui.DamageIndicator;
import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import kz.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerEvent implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void handlePlayerJoin(PlayerJoinEvent ev) {
		final Player player = ev.getPlayer();
		new Database(player);

		// load last hero
		Manager.current().loadLastHero(player);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void handleDamage(EntityDamageEvent ev) {

		final Entity entity = ev.getEntity();
		LivingEntity damagerFinal = null;
		double damage = ev.getDamage();

		if (entity instanceof Player && !Manager.current().isGameInProgress()) {
			ev.setCancelled(true);
			return;
		}

		// Calculate base damage
		if (ev instanceof EntityDamageByEntityEvent event) {
			final Entity damager = event.getDamager();

			// ignore all this if self damage (fall damage, explosion etc.)
			if (damager != entity) {
				if (damager instanceof Player playerDamager) {
					final Heroes hero = Manager.current().getSelectedHero(playerDamager);

					damage = getHeroDamageOr(playerDamager, 1.0d);

					// decrease damage if hitting with a bow
					final Material type = hero.getHero().getWeapon().getItem().getType();
					if (type == Material.BOW || type == Material.CROSSBOW) {
						damage *= 0.4d;
					}

					// assign the damager
					damagerFinal = playerDamager;
				}

				else if (damager instanceof Projectile projectile) {
					if (projectile.getShooter() instanceof Player playerDamager) {
						damage = getHeroDamageOr(playerDamager, 2.0d);

						// increase damage if fully charged shot
						if (projectile instanceof Arrow arrow) {
							if (arrow.isCritical()) {
								damage *= 1.5d;
							}
						}

						// assign the damager
						damagerFinal = playerDamager;
					}
				}
			}

			// apply modifiers for damager
			if (damager instanceof LivingEntity living) {
				final PotionEffect potionEffect = living.getPotionEffect(PotionEffectType.INCREASE_DAMAGE);
				if (potionEffect != null) {
					// add 30% of damage per strength level
					damage += ((damage * 3 / 10) * (potionEffect.getAmplifier() + 1));
				}
			}

			// apply modifiers for entity
			if (entity instanceof LivingEntity living) {
				final PotionEffect effectResistance = living.getPotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
				// reduce damage by 85% if we have resistance
				if (effectResistance != null) {
					damage *= 0.15d;
				}

				// negate all damage is blocking
				if (living instanceof Player player) {
					if (player.isBlocking()) {
						damage = 0.0d;
					}
				}

			}

		}

		// make sure not to kill player but instead put them in spectator
		if (entity instanceof Player player) {
			final GamePlayer gamePlayer = GamePlayer.getPlayer(player);
			// if game player is null means the game is not in progress
			if (gamePlayer != null) {
				if (damage >= gamePlayer.getHealth()) {
					gamePlayer.die(true);
					ev.setCancelled(true);
					return;
				}
				gamePlayer.damage(damage, damagerFinal);
			}

			// fail safe for actual health
			if (player.getHealth() <= 0.0d) {
				ev.setCancelled(true);
			}

		}

		// display damage
		if (damage > 0.0d) {
			new DamageIndicator(entity.getLocation(), damage);
		}

		ev.setDamage(0.0d);

	}

	private double getHeroDamageOr(Player player, double or) {
		final Heroes hero = Manager.current().getSelectedHero(player);
		// ye fuck enum saving but idk the better way ¯\_(ツ)_/¯
		if (player.getInventory().getHeldItemSlot() != 0 || hero == null || hero.getHero() == null
				|| hero.getHero().getWeapon() == null || hero.getHero().getWeapon().getWeapon() == null) {
			return or;
		}
		return hero.getHero().getWeapon().getWeapon().getDamage();
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void handleInventoryClickEvent(InventoryClickEvent ev) {
		if (Manager.current().isGameInProgress()) {
			ev.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void handlePlayerClick(PlayerItemHeldEvent ev) {
		if (!Manager.current().isGameInProgress()) {
			return;
		}

		// 1 -> ability first
		// 2 -> ability second
		// 0 def weapon slot

		final int newSlot = ev.getNewSlot();
		final Player player = ev.getPlayer();
		final Hero hero = Manager.current().getSelectedHero(player).getHero();

		if (newSlot == 1) {
			checkAndExecuteTalent(player, hero.getFirstTalent());
		}

		else if (newSlot == 2) {
			checkAndExecuteTalent(player, hero.getSecondTalent());
		}

		else {
			return;
		}

		ev.setCancelled(true);
		player.getInventory().setHeldItemSlot(0);

	}

	@EventHandler()
	public void handleSlotClick(InventoryClickEvent ev) {
		if (ev.getClick() == ClickType.DROP && ev.getWhoClicked() instanceof Player player && player.getGameMode() == GameMode.CREATIVE) {
			ev.setCancelled(true);
			Chat.sendMessage(player, "&aClicked %s slot.", ev.getRawSlot());
			PlayerLib.playSound(player, Sound.BLOCK_LEVER_CLICK, 2.0f);
		}
	}

	private void checkAndExecuteTalent(Player player, Talent talent) {
		if (talent == null) {
			Chat.sendMessage(player, "&cNullPointerException: talent is null");
			return;
		}

		if (talent.hasCd(player)) {
			Chat.sendMessage(player, "&cTalent on cooldown for %ss.", BukkitUtils.roundTick(talent.getCdTimeLeft(player)));
			return;
		}

		final Response response = talent.execute(player);

		if (response == null) {
			Chat.sendMessage(player, "&4ERROR > &cTalent returned null!");
			return;
		}

		if (response.isError()) {
			Chat.sendMessage(player, "&cCannot use this now! " + response.getReason());
			return;
		}

		talent.startCd(player);

	}

}
