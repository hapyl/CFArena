package kz.hapyl.fight.game;

import com.google.common.collect.Maps;
import kz.hapyl.fight.Main;
import kz.hapyl.fight.game.database.Database;
import kz.hapyl.fight.game.heroes.Hero;
import kz.hapyl.fight.game.heroes.Heroes;
import kz.hapyl.fight.game.talents.ChargedTalent;
import kz.hapyl.fight.game.talents.Talent;
import kz.hapyl.fight.game.talents.Talents;
import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.entity.Entities;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Map;
import java.util.UUID;

public class Manager {

	protected final Map<UUID, Heroes> selectedHero;
	private GameInstance gameInstance;

	public Manager() {
		this.selectedHero = Maps.newHashMap();
	}

	public boolean isGameInProgress() {
		return gameInstance != null && !gameInstance.isTimeIsUp();
	}

	@Nullable
	public GameInstance getGameInstance() {
		return gameInstance;
	}

	public GameInstance getCurrentGame() throws RuntimeException {
		final GameInstance gameInstance = getGameInstance();
		if (gameInstance == null) {
			throw new IllegalStateException("Game Instance called outside a game.");
		}
		return gameInstance;
	}

	private ItemStack getTalentItemIfExists(Talent talent) {
		return talent == null || talent.getItem() == null ? new ItemStack(Material.AIR) : talent.getItem();
	}

	public void countEverything() {

	}

	public void createNewGameInstance() {
		this.gameInstance = new GameInstance(600);

		this.gameInstance.onStart();

		for (final Player player : Bukkit.getOnlinePlayers()) {
			final PlayerInventory inventory = player.getInventory();
			inventory.setHeldItemSlot(0);
			player.setGameMode(GameMode.SURVIVAL);

			final Heroes heroEnum = getSelectedHero(player);
			if (heroEnum == null) {
				continue;
			}

			final Hero hero = heroEnum.getHero();

			// Apply equipment
			hero.getEquipment().equip(player);

			if (hero instanceof PlayerElement heroPE) {
				heroPE.onStart(player);
			}

			inventory.setItem(0, hero.getWeapon().getItem());
			inventory.setItem(1, getTalentItemIfExists(hero.getFirstTalent()));
			inventory.setItem(2, getTalentItemIfExists(hero.getSecondTalent()));

			fixTalentItemAmount(player, 1, hero.getFirstTalent());
			fixTalentItemAmount(player, 2, hero.getSecondTalent());

			player.updateInventory();

		}

	}

	private void fixTalentItemAmount(Player player, int slot, Talent talent) {
		if (!(talent instanceof ChargedTalent chargedTalent)) {
			return;
		}
		final PlayerInventory inventory = player.getInventory();
		final ItemStack item = inventory.getItem(slot);
		if (item == null) {
			return;
		}
		item.setAmount(chargedTalent.getMaxCharges());
	}

	public void stopCurrentGame() {
		if (this.gameInstance == null) {
			return;
		}

		// Reset player before clearing the instance
		this.gameInstance.getPlayers().values().forEach(player -> {
			player.updateScoreboard(false);
			player.resetPlayer();
		});

		this.gameInstance.onStop();
		this.gameInstance = null;

		// reset all cooldowns
		for (final Material value : Material.values()) {
			Bukkit.getOnlinePlayers().forEach(player -> player.setCooldown(value, 0));
		}

		// call talents onStop and reset cooldowns
		for (final Talents value : Talents.values()) {
			if (value.getTalent() != null) {
				value.getTalent().onStop();
			}
		}

		// call heroes onStop
		for (final Heroes value : Heroes.values()) {
			if (value.getHero() != null) {
				value.getHero().onStop();
			}
		}

		// stop all game tasks
		Main.getPlugin().getTaskList().onStop();

		// remove temp entities
		Entities.killSpawned();

	}

	public void loadLastHero(Player player) {
		this.selectedHero.put(player.getUniqueId(), Database.getDatabase(player).getHeroEntry().getSelectedHero());
	}

	public void setSelectedHero(Player player, Heroes heroes) {
		if (Manager.current().isGameInProgress()) {
			Chat.sendMessage(player, "&cUnable to change hero during the game!");
			return;
		}

		if (getSelectedHero(player) == heroes) {
			Chat.sendMessage(player, "&cAlready selected!");
			return;
		}
		this.selectedHero.put(player.getUniqueId(), heroes);
		Chat.sendMessage(player, "&aSelected %s!", heroes.getHero().getName());

		// save to database
		Database.getDatabase(player).getHeroEntry().setSelectedHero(heroes);
	}

	public Map<UUID, Heroes> getPerPlayerHeroes() {
		return selectedHero;
	}

	public Heroes getSelectedHero(Player player) {
		return this.selectedHero.getOrDefault(player.getUniqueId(), Heroes.ARCHER);
	}

	public boolean isPlayerInGame(Player player) {
		return this.gameInstance != null && this.gameInstance.getPlayer(player) != null && this.gameInstance.getPlayer(player).isAlive();
	}

	public static Manager current() {
		return Main.getPlugin().getManager();
	}
}
