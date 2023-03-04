package me.hapyl.fight.game.talents.storage.extra;

import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.math.Geometry;
import me.hapyl.spigotutils.module.math.geometry.PlayerParticle;
import me.hapyl.spigotutils.module.math.geometry.Quality;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.reflect.visibility.Visibility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class AstralStars {

	private final double pickupDistance = 3.0d;

	private final Player player;
	private final Set<LivingEntity> bats;

	public AstralStars(Player player) {
		this.player = player;
		this.bats = new HashSet<>();
	}

	public int getStarsAmount() {
		return bats.size();
	}

	public void summonStar(Location location) {
		final Bat bat = Entities.BAT.spawn(location, me -> {
			me.setSilent(true);
			me.setInvulnerable(true);
			me.setInvisible(true);
			me.setAI(false);
			me.setAwake(false);
			addToGlowingTeam(me);
			me.setGlowing(true);
		});
		this.bats.add(bat);
		Visibility.of(bat, player);
		//		for (final Player online : Bukkit.getOnlinePlayers()) {
		//			if (online == player) {
		//				continue;
		//			}
		//			Utils.hideEntity(bat, online);
		//		}
	}

	public double getPickupDistance() {
		return pickupDistance;
	}

	public void tickStars() {
		bats.forEach(bat -> {
			// Display activation radius
			final Location location = bat.getLocation();
			Geometry.drawCircle(location, pickupDistance, Quality.HIGH, new PlayerParticle(Particle.CRIT, player));

			// Display particles for others
			Bukkit.getOnlinePlayers().forEach(other -> {
				if (other == player) {
					return;
				}
				PlayerLib.spawnParticle(location, Particle.CRIT, 5, 0.1d, 0.1d, 0.1d, 0.01f);
			});
		});
	}

	public void updateColors() {
		final List<LivingEntity> list = getLastTwoStars();

		final Team teamYellow = getOrCreateTeam(ChatColor.YELLOW);
		final Team teamGreen = getOrCreateTeam(ChatColor.GREEN);
		final Team teamAqua = getOrCreateTeam(ChatColor.AQUA);

		clearEntries(teamGreen);
		clearEntries(teamAqua);

		bats.forEach(entity -> {
			teamYellow.addEntry(entity.getUniqueId().toString());
		});

		if (list.size() >= 2) {
			teamAqua.addEntry(list.get(0).getUniqueId().toString());
			teamGreen.addEntry(list.get(1).getUniqueId().toString());
		}

	}

	private void clearEntries(Team team) {
		for (final String entry : team.getEntries()) {
			team.removeEntry(entry);
		}
	}

	public List<LivingEntity> getLastTwoStars() {
		final List<LivingEntity> list = new ArrayList<>();
		final Location location = player.getLocation();
		final Map<Double, LivingEntity> distanceToBat = new TreeMap<>();

		this.bats.forEach(bat -> distanceToBat.put(bat.getLocation().distance(location), bat));

		for (final LivingEntity value : distanceToBat.values()) {
			list.add(value);
			if (list.size() >= 2) {
				break;
			}
		}
		return list;

	}

	public void removeStar(LivingEntity entity) {
		this.bats.remove(entity);
		entity.remove();
	}

	private void addToGlowingTeam(LivingEntity entity) {
		getOrCreateTeam(ChatColor.YELLOW).addEntry(entity.getUniqueId().toString());
	}

	private Team getOrCreateTeam(ChatColor color) {
		final Scoreboard scoreboard = player.getScoreboard();
		final String name = "bC_" + color.name().toLowerCase(Locale.ROOT);

		Team team = scoreboard.getTeam(name);
		if (team == null) {
			team = scoreboard.registerNewTeam(name);
			team.setColor(color);
		}
		return team;
	}

	public void clear() {
		this.bats.forEach(Entity::remove);
		this.bats.clear();
	}

}

