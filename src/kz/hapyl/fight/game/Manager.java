package kz.hapyl.fight.game;

import com.google.common.collect.Maps;
import kz.hapyl.fight.Main;
import kz.hapyl.fight.game.database.Database;
import kz.hapyl.fight.game.heroes.ComplexHero;
import kz.hapyl.fight.game.heroes.Hero;
import kz.hapyl.fight.game.heroes.Heroes;
import kz.hapyl.fight.game.maps.GameMaps;
import kz.hapyl.fight.game.talents.ChargedTalent;
import kz.hapyl.fight.game.talents.Talent;
import kz.hapyl.fight.game.talents.Talents;
import kz.hapyl.fight.game.task.GameTask;
import kz.hapyl.fight.util.Holder;
import kz.hapyl.fight.util.Nulls;
import kz.hapyl.fight.util.ParamFunction;
import kz.hapyl.fight.util.Utils;
import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.entity.Entities;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import kz.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.*;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class Manager {

	private final int timeBeforeReveal = 100;
	private boolean isDebug = true;

	private GameInstance gameInstance;

	protected final Map<UUID, Heroes> selectedHero;
	protected final Holder<GameMaps> currentMap = new Holder<>(GameMaps.ARENA);

	private final Map<Integer, ParamFunction<Talent, Hero>> slotPerTalent = new HashMap<>();
	private final Map<Integer, ParamFunction<Talent, ComplexHero>> slotPerComplexTalent = new HashMap<>();

	public Manager() {
		this.selectedHero = Maps.newHashMap();

		slotPerTalent.put(1, Hero::getFirstTalent);
		slotPerTalent.put(2, Hero::getSecondTalent);
		slotPerComplexTalent.put(3, ComplexHero::getThirdTalent);
		slotPerComplexTalent.put(4, ComplexHero::getFourthTalent);
		slotPerComplexTalent.put(5, ComplexHero::getFifthTalent);

		// load map
		currentMap.set(GameMaps.byName(Main.getPlugin().getConfig().getString("current-map", null)));
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

	public GameMaps getCurrentMap() {
		return currentMap.getOr(GameMaps.ARENA);
	}

	public void setCurrentMap(GameMaps maps) {
		currentMap.set(maps);
		// save to config
		Main.getPlugin().getConfig().set("current-map", maps.name().toLowerCase(Locale.ROOT));
	}

	public void setCurrentMap(GameMaps maps, @Nullable Player player) {
		if (getCurrentMap() == maps) {
			PlayerLib.villagerNo(player, "&cAlready selected!");
			return;
		}

		setCurrentMap(maps);

		final String mapName = maps.getMap().getName();
		if (player == null) {
			Chat.broadcast("&aCurrent map is now &l%s&a.", mapName);
		}
		else {
			Chat.broadcast("&a%s selected &l%s &aas current map!", player.getName(), mapName);
		}
	}

	private void displayError(String message, Object... objects) {
		Chat.broadcast("&c&lUnable to start the game! &c" + message.formatted(objects));
	}

	public void createNewGameInstance() {
		createNewGameInstance(false);
	}

	public void createNewGameInstance(boolean debug) {
		// Pre game start tests
		final GameMaps currentMap = this.currentMap.get();
		if (currentMap == null || !currentMap.isPlayable() || !currentMap.getMap().hasLocation()) {
			displayError("Invalid map!");
			return;
		}

		isDebug = debug;

		// TODO: 027. 09/27/2021 -> impl player req and spectator option

		this.gameInstance = new GameInstance(600, this.currentMap.getOr(GameMaps.ARENA));
		this.gameInstance.onStart();

		for (final Heroes value : Heroes.values()) {
			Nulls.runIfNotNull(value.getHero(), Hero::onStart);
		}

		for (final Talents value : Talents.values()) {
			Nulls.runIfNotNull(value.getTalent(), Talent::onStart);
		}

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
			hero.onStart(player);

			inventory.setItem(0, hero.getWeapon().getItem());
			giveTalentItem(player, hero, 1);
			giveTalentItem(player, hero, 2);

			if (hero instanceof ComplexHero) {
				giveTalentItem(player, hero, 3);
				giveTalentItem(player, hero, 4);
				giveTalentItem(player, hero, 5);
			}

			Utils.hidePlayer(player);
			player.teleport(currentMap.getMap().getLocation());
			player.updateInventory();
		}

		if (!isDebug) {
			Chat.broadcast("&a&l➺ &aAll players have been hidden!");
			Chat.broadcast("&a&l➺ &aThey have &e%ss &ato spread before being revealed.", BukkitUtils.roundTick(timeBeforeReveal));
		}

		GameTask.runLater(() -> {
			Chat.broadcast("&a&l➺ &aPlayers have been revealed. &lFIGHT!");
			gameInstance.setGameState(State.IN_GAME);
			gameInstance.getPlayers().values().forEach(target -> {
				final Player player = target.getPlayer();
				final World world = player.getLocation().getWorld();

				Utils.showPlayer(player);

				if (world != null) {
					world.strikeLightningEffect(player.getLocation().add(0.0d, 1.0d, 0.0d));
				}
			});
		}, isDebug ? 1 : timeBeforeReveal);

	}

	private void giveTalentItem(Player player, Hero hero, int slot) {
		final PlayerInventory inventory = player.getInventory();
		final Talent talent = getTalent(hero, slot);
		final ItemStack talentItem = talent == null || talent.getItem() == null ? new ItemStack(Material.AIR) : talent.getItem();

		inventory.setItem(slot, talentItem);
		fixTalentItemAmount(player, slot, talent);
	}

	public Talent getTalent(Hero hero, int slot) {
		if (slot >= 1 && slot < 3) {
			final ParamFunction<Talent, Hero> function = slotPerTalent.get(slot);
			return function == null ? null : function.execute(hero);
		}

		else if (hero instanceof ComplexHero complexHero) {
			final ParamFunction<Talent, ComplexHero> function = slotPerComplexTalent.get(slot);
			return function == null ? null : function.execute(complexHero);
		}
		return null;
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

	// FIXME: 002. 10/02/2021 - multi
	public void stopCurrentGame() {
		if (this.gameInstance == null || this.gameInstance.getGameState() == State.POST_GAME) {
			return;
		}

		this.gameInstance.calculateEverything();

		// Reset player before clearing the instance
		this.gameInstance.getPlayers().values().forEach(player -> {
			player.updateScoreboard(false);
			player.resetPlayer();

			// keep winner in survival so it's clear for them that they have won
			if (!this.gameInstance.isWinner(player.getPlayer())) {
				player.getPlayer().setGameMode(GameMode.SPECTATOR);
			}
		});

		this.gameInstance.onStop();
		this.gameInstance.setGameState(State.POST_GAME);

		// reset all cooldowns
		for (final Material value : Material.values()) {
			Bukkit.getOnlinePlayers().forEach(player -> player.setCooldown(value, 0));
		}

		// call talents onStop and reset cooldowns
		for (final Talents value : Talents.values()) {
			Nulls.runIfNotNull(value.getTalent(), Talent::onStop);
		}

		// call heroes onStop
		for (final Heroes value : Heroes.values()) {
			Nulls.runIfNotNull(value.getHero(), hero -> {
				hero.onStop();
				hero.clearUsingUltimate();
			});
		}

		// stop all game tasks
		Main.getPlugin().getTaskList().onStop();

		// remove temp entities
		Entities.killSpawned();

		if (isDebug) {
			onStop();
			return;
		}

		// Spawn Fireworks
		gameInstance.spawnFireworks(true);

	}

	public void onStop() {
		// reset game state
		gameInstance.setGameState(State.FINISHED);
		gameInstance = null;

		// teleport player
		for (final Player player : Bukkit.getOnlinePlayers()) {
			player.setInvulnerable(false);
			player.setGameMode(GameMode.SURVIVAL);
			player.teleport(GameMaps.SPAWN.getMap().getLocation());
		}
	}

	public void loadLastHero(Player player) {
		this.selectedHero.put(player.getUniqueId(), Database.getDatabase(player).getHeroEntry().getSelectedHero());
	}

	public void setSelectedHero(Player player, Heroes heroes) {
		if (Manager.current().isGameInProgress()) {
			PlayerLib.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 0.0f);
			Chat.sendMessage(player, "&cUnable to change hero during the game!");
			return;
		}

		if (getSelectedHero(player) == heroes) {
			Chat.sendMessage(player, "&cAlready selected!");
			PlayerLib.villagerNo(player);
			return;
		}

		this.selectedHero.put(player.getUniqueId(), heroes);
		player.closeInventory();
		PlayerLib.villagerYes(player);
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
