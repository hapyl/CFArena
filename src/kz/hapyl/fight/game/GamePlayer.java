package kz.hapyl.fight.game;

import kz.hapyl.fight.game.heroes.Hero;
import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class GamePlayer {

	/**
	 * A single instance should exist per game bases and cleared after the game ends.
	 */

	private final Player player;
	private final Hero hero; // this represents hero that player locked in game with, cannot be changed

	private double health;
	private LivingEntity lastDamager;

	// 		[Kinda Important Note]
	//  These two can never be both true.
	//  dead means if player has died in game when
	//  spectator means if player started as spectator
	private boolean isDead;
	private boolean isSpectator;

	private int ultPoints;

	public GamePlayer(Player player, Hero hero) {
		this.player = player;
		this.hero = hero;
		this.health = 100.0d;
		this.isDead = false;
		this.isSpectator = false;
		player.setMaxHealth(40.0d);
	}

	public boolean isAlive() {
		return !isDead || !isSpectator;
	}

	public void damage(double damage, LivingEntity damager) {
		this.health -= damage;
		this.lastDamager = damager;
		if (this.health <= 0.0d) {
			this.die(true);
		}

		this.updateHealth();

	}

	public void updateHealth() {
		// update player visual health
		player.setMaxHealth(40.d);
		player.setHealth(Math.max(0.5d, 40.0d * this.health / 100));
	}

	public void heal(double amount) {
		this.health += amount;
	}

	public void die(boolean force) {
		if (this.health > 0.0d && !force) {
			return;
		}
		this.isDead = true;
		this.isSpectator = false;

		PlayerLib.playSound(player, Sound.ENTITY_BLAZE_DEATH, 2.0f);
		Chat.sendTitle(player, "&c&lYOU DIED", "", 5, 25, 10);

		player.setHealth(player.getMaxHealth());
		player.setAllowFlight(true);
		player.setGameMode(GameMode.SPECTATOR);

		// send death info to manager
		final GameInstance gameInstance = Manager.current().getGameInstance();
		if (gameInstance != null) {
			gameInstance.checkWinCondition();
		}

	}

	@Nullable
	public LivingEntity getLastDamager() {
		return lastDamager;
	}

	public double getHealth() {
		return health;
	}

	// This is a shortcut that returns an GamePlayer from a game instance if there is one.
	@Nullable
	public static GamePlayer getPlayer(Player player) {
		final GameInstance gameInstance = Manager.current().getGameInstance();
		if (gameInstance == null) {
			return null;
		}
		return gameInstance.getPlayer(player);
	}

	public Player getPlayer() {
		return player;
	}

	public Hero getHero() {
		return hero;
	}

	public boolean isDead() {
		return isDead;
	}

	public void setDead(boolean dead) {
		isDead = dead;
		isSpectator = !dead;
	}

	public boolean isSpectator() {
		return isSpectator;
	}

	public void setSpectator(boolean spectator) {
		isSpectator = spectator;
		isDead = !spectator;
	}

	public int getUltPoints() {
		return ultPoints;
	}

	public void setUltPoints(int ultPoints) {
		this.ultPoints = ultPoints;
	}

}
