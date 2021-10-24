package kz.hapyl.fight.game.heroes.storage.extra;

import kz.hapyl.fight.anotate.StaticField;
import kz.hapyl.fight.game.GamePlayer;
import kz.hapyl.fight.game.Manager;
import kz.hapyl.fight.game.effect.GameEffectType;
import kz.hapyl.fight.game.heroes.HeroHandle;
import kz.hapyl.fight.game.task.GameTask;
import kz.hapyl.fight.util.Nulls;
import kz.hapyl.fight.util.Utils;
import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.entity.Entities;
import kz.hapyl.spigotutils.module.math.Geometry;
import kz.hapyl.spigotutils.module.math.gometry.Draw;
import kz.hapyl.spigotutils.module.particle.AbstractParticleBuilder;
import kz.hapyl.spigotutils.module.particle.ParticleBuilder;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import kz.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Lockdown {

	@StaticField private final String emptyStringTitle = "                  ";
	@StaticField private final String emptyString = "                          ";
	@StaticField private final double lockdownHealth = 200.0d;

	private final Player player;
	private final Location location;
	private final LivingEntity entity;
	private final GameTask tasks;

	public Lockdown(Player player) {
		this.player = player;
		this.location = player.getLocation();
		this.entity = createEntity();
		this.tasks = createTasks();
	}

	public void remove() {
		entity.remove();
		tasks.cancel();
	}


	private GameTask createTasks() {

		final AbstractParticleBuilder particleSelf = ParticleBuilder.redstoneDust(Color.fromRGB(57, 123, 189))
				.setAmount(2)
				.setOffX(0.2d)
				.setOffZ(0.2d)
				.setSpeed(10);
		final AbstractParticleBuilder particleOther = ParticleBuilder.redstoneDust(Color.fromRGB(255, 51, 51))
				.setAmount(2)
				.setOffX(0.2d)
				.setOffZ(0.2d)
				.setSpeed(10);
		final int delayBetweenDraw = 15;

		return new GameTask() {
			private int tick = HeroHandle.TECHIE.lockdownWindupTime;

			@Override
			public void run() {
				if (entity.isDead() || entity.getHealth() <= 0.0d) {
					PlayerLib.playSound(location, Sound.BLOCK_BEACON_DEACTIVATE, 2.0f);
					PlayerLib.playSound(location, Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 2.0f);
					Chat.sendTitle(Lockdown.this.player, "", "&cLockdown Destroyed!", 10, 20, 10);
					this.cancel();
					return;
				}

				if (tick < 0) {
					affect();
					return;
				}

				// Teleport FX
				final Location entityLocation = entity.getLocation();
				entityLocation.setYaw(entityLocation.getYaw() + 2.5f);
				entity.teleport(entityLocation);

				// Fx
				if (tick % delayBetweenDraw == 0) {
					int lockdownRadius = HeroHandle.TECHIE.lockdownRadius;
					Utils.getPlayersInRange(Lockdown.this.location, lockdownRadius).forEach(target -> {
						// Fx
						PlayerLib.playSound(target, Sound.BLOCK_BEACON_AMBIENT, 2.0f);
						//Chat.sendTitle(target, "", "&c&lLockdown Warning!", 0, 20, 0);
					});

					Geometry.drawSphere(Lockdown.this.location, lockdownRadius * 1.5d, lockdownRadius, new Draw(Particle.VILLAGER_ANGRY) {
						@Override
						public void draw(Location location) {
							Bukkit.getOnlinePlayers().forEach(player -> {
								if (player == Lockdown.this.player) {
									particleSelf.display(location, player);
								}
								else {
									particleOther.display(location, player);
								}
							});
						}
					}, true);
				}

				// Countdown
				Manager.current().getCurrentGame().getAlivePlayers().forEach(gamePlayer -> {
					final String timeLeft = BukkitUtils.decimalFormat(tick * 5, "##,##");
					displayLockdownMessage(
							"&aAlly Lockdown", "&a&l" + timeLeft,
							"&cEnemy Lockdown", "&c&l" + timeLeft, 20
					);
				});

				--tick;
			}
		}.runTaskTimer(0, 1);
	}

	public void displayLockdownMessage(String allyTitle, String allySub, String enemyTitle, String enemySub, int length) {
		Manager.current().getCurrentGame().getAlivePlayers().forEach(gamePlayer -> {
			if (gamePlayer.compare(Lockdown.this.player)) {
				gamePlayer.sendTitle(allyTitle + emptyStringTitle, allySub + emptyString, 0, length, 0);
			}
			else {
				gamePlayer.sendTitle(emptyStringTitle + enemyTitle, emptyString + enemySub, 0, length, 0);
			}
		});
	}

	public void affect() {
		tasks.cancel();
		entity.remove();

		int affectedSize = 0;
		for (final Player player : Utils.getPlayersInRange(location, HeroHandle.TECHIE.lockdownRadius)) {
			if (player == Lockdown.this.player) {
				continue;
			}

			++affectedSize;
			GamePlayer.getPlayer(player).addEffect(GameEffectType.LOCK_DOWN, HeroHandle.TECHIE.lockdownAffectTime);
		}

		displayLockdownMessage(
				"&aLockdown Affected",
				"&a&L" + affectedSize + " &aopponents.",
				"&cLockdown Affected",
				"&c&L" + affectedSize + " &cplayers.", 80
		);

		if ((affectedSize - 1) == Manager.current().getCurrentGame().getAlivePlayers().size()) {
			Chat.sendMessage(player, "&aLockdown affected opponents!");
			PlayerLib.playSound(player, Sound.ENTITY_WITCH_CELEBRATE, 1.25f);
		}

		// Fx
		PlayerLib.playSound(location, Sound.ITEM_TOTEM_USE, 2.0f);
	}

	private LivingEntity createEntity() {
		return Entities.ARMOR_STAND.spawn(player.getLocation().subtract(0.0d, 1.25d, 0.0d), me -> {
			me.setMarker(false);
			me.setInvisible(true);
			me.setGravity(false);
			me.setMaxHealth(lockdownHealth);
			me.setHealth(lockdownHealth);
			me.addScoreboardTag("LockdownDevice");

			Utils.lockArmorStand(me);
			Nulls.runIfNotNull(me.getEquipment(), eq -> eq.setHelmet(new ItemStack(Material.DAYLIGHT_DETECTOR)));
		});
	}

	public static boolean isLockdownEntity(LivingEntity entity) {
		return entity.getScoreboardTags().contains("LockdownDevice");
	}


}
