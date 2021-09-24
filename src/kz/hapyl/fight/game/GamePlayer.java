package kz.hapyl.fight.game;

import kz.hapyl.fight.game.effect.ActiveGameEffect;
import kz.hapyl.fight.game.effect.GameEffectType;
import kz.hapyl.fight.game.heroes.Hero;
import kz.hapyl.fight.game.talents.UltimateTalent;
import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.math.Numbers;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GamePlayer implements IGamePlayer {

	/**
	 * A single instance should exist per game bases and cleared after the game ends.
	 */

	private final double maxHealth = 100.0d;

	private final Player player;
	private final Hero hero; // this represents hero that player locked in game with, cannot be changed

	private double health;
	private LivingEntity lastDamager;
	private EnumDamageCause lastDamageCause; // todo -> impl this

	private final Map<GameEffectType, ActiveGameEffect> gameEffects;

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
		this.health = maxHealth;
		this.isDead = false;
		this.isSpectator = false;
		this.gameEffects = new ConcurrentHashMap<>();
		this.resetPlayer();
	}

	public void resetPlayer() {
		player.getInventory().clear();
		player.setMaxHealth(40.0d);
		player.setHealth(40.0d);
		player.setGameMode(GameMode.SURVIVAL);
		player.setFireTicks(0);
		player.setVisualFire(false);
		player.setFlying(false);
		player.setSaturation(0.0f);
		player.setFoodLevel(20);
		player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
	}

	public void addEffect(GameEffectType type, int ticks) {
		addEffect(type, ticks, false);
	}

	public void addEffect(GameEffectType type, int ticks, boolean override) {
		final ActiveGameEffect effect = gameEffects.get(type);
		if (effect != null) {
			effect.triggerUpdate();
			if (override) {
				effect.setRemainingTicks(ticks);
			}
			else {
				effect.addRemainingTicks(ticks);
			}
		}
		else {
			gameEffects.put(type, new ActiveGameEffect(player, type, ticks));
		}
	}

	public Map<GameEffectType, ActiveGameEffect> getActiveEffects() {
		return gameEffects;
	}

	public boolean hasEffect(GameEffectType type) {
		return gameEffects.containsKey(type);
	}

	public void clearEffects() {
		this.gameEffects.clear();
	}

	public void removeEffect(GameEffectType type) {
		final ActiveGameEffect gameEffect = gameEffects.get(type);
		gameEffect.forceStop();
	}

	public void clearEffect(GameEffectType type) {
		gameEffects.remove(type);
	}

	public boolean isAlive() {
		return !isDead || !isSpectator;
	}

	public boolean isUltimateReady() {
		return this.ultPoints >= this.hero.getUltimate().getCost();
	}

	@Override
	public void damage(double d) {
		damage(d, null, null);
	}

	public void damage(double d, EnumDamageCause cause) {
		damage(d, null, cause);
	}

	@Override
	public void setHealth(double health) {
		this.health = health;
		this.updateHealth();
	}

	public void damage(double damage, @Nullable LivingEntity damager, @Nullable EnumDamageCause cause) {
		this.player.damage(damage, damager);
		if (damager != null) {
			lastDamager = damager;
		}
		if (cause != null) {
			lastDamageCause = cause;
		}
	}

	/**
	 * This should only be called in the calculations, do not call it otherwise.
	 */
	public void decreaseHealth(double damage, LivingEntity damager) {
		this.decreaseHealth(damage);
		this.lastDamager = damager;
	}

	@Override
	public EnumDamageCause getLastDamageCause() {
		return lastDamageCause;
	}

	/**
	 * This should only be called in the calculations, do not call it otherwise.
	 */
	public void decreaseHealth(double damage) {
		this.health -= damage;
		if (this.health <= 0.0d) {
			this.die(true);
		}

		this.updateHealth();
	}

	public void updateScoreboard(boolean flag) {

		final Team team = getOrCreateTeam(player);

		// turn on nicknames and turn off collisions
		if (flag) {
			team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
			team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
		}
		else {
			team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.ALWAYS);
			team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
		}

		populateTeam(team);

	}

	private Team getOrCreateTeam(Player player) {
		final Scoreboard scoreboard = player.getScoreboard();
		Team team = scoreboard.getTeam("%Team");
		if (team == null) {
			team = scoreboard.registerNewTeam("%Team");
		}
		return team;
	}

	private void populateTeam(Team team) {
		for (final String entry : team.getEntries()) {
			team.removeEntry(entry);
		}

		Bukkit.getOnlinePlayers().forEach(player -> team.addEntry(player.getName()));
	}

	public void updateHealth() {
		// update player visual health
		player.setMaxHealth(40.d);
		player.setHealth(Math.max(0.5d, 40.0d * this.health / maxHealth));
	}

	public void heal(double amount) {
		this.health = Numbers.clamp(health + amount, 0.5d, maxHealth);
		this.updateHealth();
	}

	public void die(boolean force) {
		if (this.health > 0.0d && !force) {
			return;
		}
		this.isDead = true;
		this.isSpectator = false;

		PlayerLib.playSound(player, Sound.ENTITY_BLAZE_DEATH, 2.0f);
		Chat.sendTitle(player, "&c&lYOU DIED", "", 5, 25, 10);

		this.resetPlayer();
		player.setAllowFlight(true);
		player.setGameMode(GameMode.SPECTATOR);

		// send death info to manager
		final GameInstance gameInstance = Manager.current().getGameInstance();
		if (gameInstance != null) {
			gameInstance.checkWinCondition();
		}

	}

	@Override
	public double getMaxHealth() {
		return maxHealth;
	}

	public UltimateTalent getUltimate() {
		return this.hero.getUltimate();
	}

	public void setLastDamageCause(EnumDamageCause lastDamageCause) {
		this.lastDamageCause = lastDamageCause;
	}

	public void setLastDamager(LivingEntity lastDamager) {
		this.lastDamager = lastDamager;
	}

	public void addUltimatePoints(int points) {
		// cannot give points if using ultimate or dead
		if (Manager.current().getSelectedHero(player).getHero().isUsingUltimate(player) || !this.isAlive()) {
			return;
		}

		if (this.ultPoints >= this.getUltPointsNeeded()) {
			return;
		}
		this.ultPoints = Numbers.clamp(this.ultPoints + points, 0, this.hero.getUltimate().getCost());
		if (this.ultPoints >= this.getUltPointsNeeded()) {
			Chat.sendMessage(player, "&b&l※ &bYou Ultimate is ready! Press &e&lF &bto use it!");
		}
	}

	@Nullable
	public LivingEntity getLastDamager() {
		return lastDamager;
	}

	@Override
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

	public static IGamePlayer getPlayerSafe(Player player) {
		final GamePlayer gamePlayer = getPlayer(player);
		return gamePlayer == null ? new SafeGamePlayer() : gamePlayer;
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

	public int getUltPointsNeeded() {
		return this.hero == null ? 999 : this.hero.getUltimate().getCost();
	}

	public int getUltPoints() {
		return ultPoints;
	}

	public void setUltPoints(int ultPoints) {
		this.ultPoints = ultPoints;
	}

	public boolean compare(GamePlayer gamePlayer) {
		return gamePlayer == this;
	}

	public boolean compare(Player player) {
		return this.getPlayer() == player;
	}

}
