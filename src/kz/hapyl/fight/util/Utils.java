package kz.hapyl.fight.util;

import kz.hapyl.fight.Main;
import kz.hapyl.fight.game.*;
import kz.hapyl.fight.game.effect.GameEffectType;
import kz.hapyl.fight.game.task.GameTask;
import kz.hapyl.spigotutils.module.annotate.NULLABLE;
import kz.hapyl.spigotutils.module.math.Geometry;
import kz.hapyl.spigotutils.module.math.gometry.Quality;
import kz.hapyl.spigotutils.module.math.gometry.WorldParticle;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import kz.hapyl.spigotutils.module.reflect.Reflect;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntityLiving;
import net.minecraft.world.entity.EntityLiving;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Utils {

	// TODO: 030. 08/30/2021
	public static String colorString(String str, String defColor) {
		final StringBuilder builder = new StringBuilder();
		final String[] strings = str.split(" ");
		for (final String string : strings) {
			if (string.endsWith("%")) {
				builder.append(ChatColor.RED);
			}
			else if (string.endsWith("s") && string.contains("[0-9]")) {
				builder.append(ChatColor.AQUA);
			}
			else {
				builder.append(defColor);
			}
			builder.append(string).append(" ");
		}
		return builder.toString().trim();
	}

	public static <E> List<String> collectionToStringList(Collection<E> e, java.util.function.Function<E, String> fn) {
		final List<String> list = new ArrayList<>();
		e.forEach(el -> list.add(fn.apply(el)));
		return list;
	}

	@Nullable
	public static Team getEntityTeam(Entity entity) {
		final Scoreboard scoreboard = Bukkit.getScoreboardManager() == null ? null : Bukkit.getScoreboardManager().getMainScoreboard();
		if (scoreboard == null) {
			return null;
		}

		return getEntityTeam(entity, scoreboard);
	}

	@Nullable
	public static Team getEntityTeam(Entity entity, Scoreboard scoreboard) {
		return scoreboard.getEntryTeam(entity instanceof Player ? entity.getName() : entity.getUniqueId().toString());
	}

	public static void playSoundAndCut(Location location, Sound sound, float pitch, int cutAt) {
		final Set<Player> playingTo = new HashSet<>();
		Manager.current().getCurrentGame().getAlivePlayers().forEach(gp -> {
			final Player player = gp.getPlayer();
			player.playSound(location, sound, SoundCategory.RECORDS, 20f, pitch);
			playingTo.add(player);
		});
		new GameTask() {
			@Override
			public void run() {
				playingTo.forEach(player -> {
					player.stopSound(sound, SoundCategory.RECORDS);
				});
				playingTo.clear();
			}
		}.runTaskLater(cutAt);
	}

	public static void playSoundAndCut(Player player, Sound sound, float pitch, int cutAt) {
		PlayerLib.playSound(player, sound, pitch);
		new GameTask() {
			@Override
			public void run() {
				player.stopSound(sound, SoundCategory.RECORDS);
			}
		}.runTaskLater(cutAt);
	}

	public static boolean compare(@Nullable Object a, @Nullable Object b) {
		// true if both objects are null
		if (a == null && b == null) {
			return true;
		}
		// false if only one object is null
		if (a == null || b == null) {
			return false;
		}
		return a.equals(b);
	}

	public static <E> void clearCollectionAnd(Collection<E> collection, Consumer<E> action) {
		collection.forEach(action);
		collection.clear();
	}

	public static void hidePlayer(Player player) {
		Manager.current().getCurrentGame().getAlivePlayers().forEach(gp -> {
			if (gp.getPlayer() != player) {
				gp.getPlayer().hidePlayer(Main.getPlugin(), player);
			}
		});
	}

	public static void showPlayer(Player player) {
		Manager.current().getCurrentGame().getAlivePlayers().forEach(gp -> {
			if (gp.getPlayer() != player) {
				gp.getPlayer().showPlayer(Main.getPlugin(), player);
			}
		});
	}

	public static void hideEntity(Entity entity, Player player) {
		final PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(entity.getEntityId());
		Reflect.sendPacket(player, packet);
	}

	public static void showEntity(Entity entity, Player player) {
		final PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving((EntityLiving)Reflect.getNetEntity(entity));
		Reflect.sendPacket(player, packet);
	}

	public static void rayTracePath(Location start, Location end, double shift, double searchRange, Function<LivingEntity> funcLiving, Function<Location> funcLoc) {
		final double maxDistance = start.distance(end);
		final Vector vector = end.toVector().subtract(start.toVector()).normalize().multiply(shift);

		new GameTask() {
			private double tick = maxDistance;

			@Override
			public void run() {
				if (tick < 0) {
					this.cancel();
					return;
				}

				start.add(vector);

				Nulls.runIfNotNull(funcLiving, f -> Utils.getEntitiesInRange(start, searchRange).forEach(f::execute));
				Nulls.runIfNotNull(funcLoc, f -> f.execute(start));

				tick -= shift;

			}
		}.runTaskTimer(0, 1);

	}

	public static void rayTraceLine(Player shooter, double maxDistance, double shift, double damage, @NULLABLE EnumDamageCause cause, @NULLABLE Consumer<Location> onMove, @NULLABLE Consumer<LivingEntity> onHit) {
		final Location location = shooter.getLocation().add(0, 1.5, 0);
		final Vector vector = location.getDirection().normalize();

		main:
		for (double i = 0; i < maxDistance; i += shift) {

			double x = vector.getX() * i;
			double y = vector.getY() * i;
			double z = vector.getZ() * i;
			location.add(x, y, z);

			// check for the hitting a block and an entity
			if (location.getBlock().getType().isOccluding()) {
				break;
			}

			for (final LivingEntity living : getEntitiesInRange(location, 0.5)) {
				if (living == shooter || living instanceof Player player && !Manager.current().isPlayerInGame(player)) {
					continue;
				}

				if (onHit != null) {
					onHit.accept(living);
				}

				if (damage > 0.0d) {
					GamePlayer.damageEntity(living, damage, shooter, cause);
				}
				break main;
			}

			if (i > 1.0) {
				if (onMove != null) {
					onMove.accept(location);
				}
			}
			location.subtract(x, y, z);

		}
	}

	public static void rayTraceLine(Player shooter, double maxDistance, double shift, double damage, @NULLABLE Consumer<Location> onMove, @NULLABLE Consumer<LivingEntity> onHit) {
		rayTraceLine(shooter, maxDistance, shift, damage, null, onMove, onHit);
	}

	public static Response playerCanUseAbility(Player player) {
		final AbstractGamePlayer gp = GamePlayer.getPlayer(player);
		if (gp.hasEffect(GameEffectType.STUN)) {
			return Response.error("Talent is locked!");
		}

		if (Manager.current().isGameInProgress()) {
			final State state = Manager.current().getCurrentGame().getGameState();
			if (state != State.IN_GAME) {
				return Response.error("Game is not yet started!");
			}
		}

		return Response.OK;
	}

	public static Player getTargetPlayer(Player player, double maxDistance) {
		return (Player)getTargetEntity(
				player,
				maxDistance,
				entity -> entity != player && entity instanceof Player p && Manager.current().isPlayerInGame(p)
		);
	}

	@Nullable
	public static LivingEntity getTargetEntity(Player player, double maxDistance, Predicate<LivingEntity> predicate) {
		final Location location = player.getLocation().add(0, 1.5, 0);
		final Vector vector = location.getDirection().normalize();
		final float radius = 1.25f;

		for (double i = 0; i < maxDistance; i += 0.5d) {
			final double x = vector.getX() * i;
			final double y = vector.getY() * i;
			final double z = vector.getZ() * i;
			location.add(x, y, z);

			for (final LivingEntity entity : Utils.getEntitiesInRange(location, radius)) {
				if (!entity.hasLineOfSight(player) || !predicate.test(entity)) {
					continue;
				}
				return entity;
			}

			location.subtract(x, y, z);
		}

		return null;
	}

	public static <E> void clearCollection(Collection<E> collection) {
		for (final E entry : collection) {
			if (entry == null) {
				continue;
			}
			if (entry instanceof Entity entity) {
				entity.remove();
			}
			if (entry instanceof Block block) {
				block.getState().update(false, false);
			}
		}
		collection.clear();
	}

	public static Player getNearestPlayer(Location location, double radius, Player exclude) {
		return (Player)getNearestEntity(
				location,
				radius,
				entity -> entity instanceof Player && entity != exclude && Manager.current().isPlayerInGame((Player)entity)
		);
	}

	public static Entity getNearestEntity(Location fromWhere, double radius, Predicate<Entity> predicate) {
		if (fromWhere.getWorld() == null) {
			throw new NullPointerException("Cannot find entity in null world!");
		}
		final List<Entity> list = fromWhere.getWorld().getNearbyEntities(fromWhere, radius, radius, radius)
				.stream()
				.filter(predicate)
				.collect(Collectors.toList());
		Entity nearest = null;
		double dist = -1;
		for (Entity entity : list) {
			final double distance = entity.getLocation().distance(fromWhere);
			// init
			if (nearest == null) {
				nearest = entity;
				dist = distance;
			}
			else {
				if (distance <= dist) {
					nearest = entity;
					dist = distance;
				}
			}
		}
		return nearest;
	}

	public static List<LivingEntity> getEntitiesInRange(Location location, double range) {
		final World world = location.getWorld();
		final List<LivingEntity> entities = new ArrayList<>();
		if (world == null) {
			return entities;
		}

		world.getNearbyEntities(location, range, range, range).stream().filter(entity -> {
			if (entity instanceof Player player) {
				if (Manager.current().isGameInProgress()) {
					return Manager.current().getGameInstance().getPlayer(player).isAlive();
				}
			}

			if (entity instanceof LivingEntity living) {
				return living.getType() != EntityType.ARMOR_STAND && !living.isInvisible() && !living.isDead();
			}

			return false;
		}).forEach(entity -> entities.add((LivingEntity)entity));

		return entities;

	}

	public static List<Player> getPlayersInRange(Location location, double range) {
		final World world = location.getWorld();
		final List<Player> players = new ArrayList<>();
		if (world == null) {
			return players;
		}

		world.getNearbyEntities(location, range, range, range)
				.stream()
				.filter(entity -> entity instanceof Player && Manager.current().isPlayerInGame((Player)entity))
				.forEach(player -> players.add((Player)player));

		return players;

	}

	public static void main(String[] args) {
		System.out.println(colorString("Increased damage by 10% fo 30s.", "&7"));
	}

	public static void createExplosion(Location location, double range, double damage, Consumer<LivingEntity> consumer) {
		final World world = location.getWorld();
		if (world == null) {
			return;
		}

		Utils.getEntitiesInRange(location, range).forEach(entity -> {
			if (damage > 0.0d) {
				entity.damage(damage);
			}
			if (consumer != null) {
				consumer.accept(entity);
			}
		});

		// Fx
		Geometry.drawCircle(location, range, Quality.NORMAL, new WorldParticle(Particle.CRIT));
		Geometry.drawCircle(location, range + 0.5d, Quality.NORMAL, new WorldParticle(Particle.ENCHANTMENT_TABLE));
		PlayerLib.spawnParticle(location, Particle.EXPLOSION_HUGE, 1, 1, 0, 1, 0);
		PlayerLib.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1.0f);

	}

	public static void createExplosion(Location location, double range, double damage) {
		createExplosion(location, range, damage, null);
	}
}
