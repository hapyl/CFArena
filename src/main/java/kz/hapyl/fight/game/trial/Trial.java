package kz.hapyl.fight.game.trial;

import io.netty.util.internal.ConcurrentSet;
import kz.hapyl.fight.game.GameElement;
import kz.hapyl.fight.game.GamePlayer;
import kz.hapyl.fight.game.Manager;
import kz.hapyl.fight.game.heroes.Hero;
import kz.hapyl.fight.game.heroes.Heroes;
import kz.hapyl.fight.game.maps.GameMaps;
import kz.hapyl.fight.game.task.GameTask;
import kz.hapyl.fight.util.MessageSender;
import kz.hapyl.fight.util.Nulls;
import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.entity.Entities;
import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import kz.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.Set;

// TODO: 019. 10/19/2021
public class Trial implements MessageSender, GameElement {

	private final String prefix = "&c&lTRIAL &cBeta";
	private final long timeLimit = 300;
	private final Manager manager = Manager.current();

	private final Set<LivingEntity> trialEntities;
	private final GamePlayer gamePlayer;
	private final Player player;
	private final Heroes heroes;
	private long startedAt;

	private GameTask task;

	public Trial(Player player, Heroes heroes) {
		this.trialEntities = new ConcurrentSet<>();
		this.gamePlayer = new GamePlayer(player, heroes.getHero());
		this.player = player;
		this.heroes = heroes;
	}

	@Override
	public void onStart() {
		start();
	}

	@Override
	public void onStop() {
		stop();
	}

	private void start() {
		startedAt = System.currentTimeMillis();
		manager.equipPlayer(player);

		// Call onStart
		// not sure if onStart() should be called or no
		heroes.getHero().onStart();
		heroes.getHero().onStart(player);

		task = new GameTask() {
			private int tick = 0;

			@Override
			public void run() {
				if (tick % 20 == 0) {
					if (trialEntities.isEmpty()) {
						createTrialEntity(Entities.ZOMBIE, BukkitUtils.defLocation(100.5, 64, 106.5));
					}
					updateTrialEntitiesNames();
				}

				if (tick % 5 == 0) {
					gamePlayer.addUltimatePoints(1);
				}

				++tick;

			}
		}.runTaskTimer(0, 1);

		player.teleport(GameMaps.TRAINING_GROUNDS.getMap().getLocation());
		this.sendMessage(
				player,
				"Keep in mind trial is in early stages of development and not all features are available. Also, only one player can use trial feature at the same time!"
		);
		this.sendMessage(player, "Use &e/trial &7to stop your trial challenge.");
	}


	public long getTimeLeft() {
		return timeLimit * 1000 - (System.currentTimeMillis() - startedAt);
	}

	public boolean isTimeIsUp() {
		return getTimeLeft() <= 0;
	}

	private void updateTrialEntitiesNames() {
		for (final LivingEntity entity : trialEntities) {
			if (entity.isDead()) {
				trialEntities.remove(entity);
				return;
			}

			entity.setCustomName(Chat.format(
					"&c&lTRIAL &a%s &7| &c%s&l❤",
					Chat.capitalize(entity.getType()),
					BukkitUtils.decimalFormat(entity.getHealth())
			));
			entity.setCustomNameVisible(true);
		}
	}

	private LivingEntity createTrialEntity(Entities<? extends Entity> entities, Location location) {
		location.setYaw(-180.0f);
		return (LivingEntity)entities.spawn(location, me -> {
			if (!(me instanceof LivingEntity living)) {
				return;
			}
			living.setAI(false);
			living.setMaxHealth(100.0d);
			living.setHealth(100.0d);
			Nulls.runIfNotNull(living.getEquipment(), equipment -> {
				equipment.setHelmet(ItemBuilder.leatherHat(Color.FUCHSIA).setUnbreakable().build());
			});
			trialEntities.add(living);
		});
	}

	private void stop() {
		final Hero hero = heroes.getHero();
		gamePlayer.resetPlayer();
		gamePlayer.setValid(false);

		hero.onStop();
		hero.onStop(player);

		trialEntities.forEach(Entity::remove);
		Nulls.runIfNotNull(task, GameTask::cancel);

		player.teleport(GameMaps.SPAWN.getMap().getLocation());

	}

	public GamePlayer getGamePlayer() {
		return gamePlayer;
	}

	public Player getPlayer() {
		return player;
	}

	public Heroes getHeroes() {
		return heroes;
	}

	@Override
	public void broadcastMessage(String string, Object... objects) {
		Chat.broadcast(prefix + "&7: " + string, objects);
	}

	@Override
	public void sendMessage(Player player, String string, Object... objects) {
		Chat.sendMessage(player, prefix + "&7: " + string, objects);
	}
}
