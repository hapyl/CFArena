package kz.hapyl.fight.game.gamemode.modes;

import kz.hapyl.fight.game.GameInstance;
import kz.hapyl.fight.game.GamePlayer;
import kz.hapyl.fight.game.StatContainer;
import kz.hapyl.fight.game.gamemode.CFGameMode;
import kz.hapyl.fight.game.task.GameTask;
import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import kz.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import java.util.TreeMap;

public class Deathmatch extends CFGameMode {
	private final ChatColor[] colors = {ChatColor.GREEN, ChatColor.DARK_GREEN, ChatColor.YELLOW, ChatColor.GOLD, ChatColor.RED};

	public Deathmatch() {
		super("Deathmatch", 300);
		this.setInfo("todo");
		this.setPlayerRequirements(2);
	}

	@Override
	public boolean testWinCondition(@Nonnull GameInstance instance) {
		return false;
	}

	public final TreeMap<Long, GamePlayer> getTopKills(@Nonnull GameInstance instance, int limit) {
		final TreeMap<Long, GamePlayer> players = new TreeMap<>();
		instance.getPlayers().values().forEach(player -> {
			players.put(player.getStats().getValue(StatContainer.Type.KILLS), player);
		});

		// limit players
		final TreeMap<Long, GamePlayer> limitedPlayers = new TreeMap<>();
		players.descendingMap().forEach((value, player) -> {
			if (limitedPlayers.size() >= limit) {
				return;
			}
			limitedPlayers.put(value, player);
		});

		players.clear();
		return limitedPlayers;
	}

	@Override
	public void onDeath(@Nonnull GameInstance instance, @Nonnull GamePlayer player) {
		final Player bukkitPlayer = player.getPlayer();

		new GameTask() {
			private int timeBeforeRespawn = 3;

			@Override
			public void run() {

				if (timeBeforeRespawn <= 0) {
					respawnPlayer(player, instance.getCurrentMap().getMap().getLocation());
					this.cancel();
					return;
				}

				Chat.sendTitle(bukkitPlayer, "&aRespawning in", (colors[timeBeforeRespawn - 1] + "" + timeBeforeRespawn), 0, 25, 0);
				PlayerLib.playSound(bukkitPlayer, Sound.BLOCK_NOTE_BLOCK_PLING, 2.0f - (0.2f * timeBeforeRespawn));
				--timeBeforeRespawn;

			}
		}.runTaskTimer(0, 20);

	}

	@Override
	public boolean onStop(@Nonnull GameInstance instance) {
		GamePlayer player = null;
		long mostKills = 0;

		for (final GamePlayer value : instance.getPlayers().values()) {
			final long currentKills = value.getStats().getValue(StatContainer.Type.KILLS);
			if (player == null || currentKills > mostKills) {
				player = value;
				mostKills = currentKills;
			}
		}

		instance.getWinners().add(player);
		return true;
	}

	private void respawnPlayer(GamePlayer player, Location location) {
		final Player bukkitPlayer = player.getPlayer();
		BukkitUtils.mergePitchYaw(bukkitPlayer.getLocation(), location);
		Chat.sendTitle(bukkitPlayer, "&aRespawned!", "", 0, 20, 5);
		bukkitPlayer.teleport(location);
		PlayerLib.addEffect(bukkitPlayer, PotionEffectType.BLINDNESS, 20, 1);
		player.respawn();
	}

}
