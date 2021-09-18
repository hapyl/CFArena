package kz.hapyl.fight.game;

import com.google.common.collect.Maps;
import kz.hapyl.fight.Main;
import kz.hapyl.fight.game.database.Database;
import kz.hapyl.fight.game.heroes.Hero;
import kz.hapyl.fight.game.heroes.Heroes;
import kz.hapyl.fight.game.talents.Talent;
import kz.hapyl.fight.game.talents.Talents;
import kz.hapyl.spigotutils.module.chat.Chat;
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

	public void createNewGameInstance() {
		this.gameInstance = new GameInstance(600);

		for (final Player player : Bukkit.getOnlinePlayers()) {
			final PlayerInventory inventory = player.getInventory();
			inventory.setHeldItemSlot(0);
			player.setGameMode(GameMode.SURVIVAL);

			final Heroes heroEnum = getSelectedHero(player);
			if (heroEnum == null) {
				continue;
			}

			final Hero hero = heroEnum.getHero();
			inventory.setItem(0, hero.getWeapon().getItem()); // fixme null?
			inventory.setItem(1, getTalentItemIfExists(hero.getFirstTalent()));
			inventory.setItem(2, getTalentItemIfExists(hero.getSecondTalent()));
			player.updateInventory();

		}

	}

	private ItemStack getTalentItemIfExists(Talent talent) {
		return talent == null || talent.getItem() == null ? new ItemStack(Material.AIR) : talent.getItem();
	}

	public void stopCurrentGame() {
		if (this.gameInstance == null) {
			return;
		}
		this.gameInstance.onStop();
		this.gameInstance = null;

		// call talents onStop
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

	}

	public void loadLastHero(Player player) {
		this.selectedHero.put(player.getUniqueId(), Database.getDatabase(player).getHeroEntry().getSelectedHero());
	}

	public void setSelectedHero(Player player, Heroes heroes) {
		if (getSelectedHero(player) == heroes) {
			Chat.sendMessage(player, "&cAlready selected!");
			return;
		}
		this.selectedHero.put(player.getUniqueId(), heroes);
		Chat.sendMessage(player, "&aSelected %s!", heroes.getHero().getName());

		// save to database
		Database.getDatabase(player).getHeroEntry().setSelectedHero(heroes);
	}

	public Map<UUID, Heroes> getSelectedHero() {
		return selectedHero;
	}

	public Heroes getSelectedHero(Player player) {
		return this.selectedHero.getOrDefault(player.getUniqueId(), Heroes.ARCHER);
	}

	public static Manager current() {
		return Main.getPlugin().getManager();
	}
}
