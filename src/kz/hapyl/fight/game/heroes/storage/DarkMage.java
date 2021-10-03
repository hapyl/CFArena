package kz.hapyl.fight.game.heroes.storage;

import kz.hapyl.fight.effect.EnumEffect;
import kz.hapyl.fight.game.EnumDamageCause;
import kz.hapyl.fight.game.GamePlayer;
import kz.hapyl.fight.game.effect.GameEffectType;
import kz.hapyl.fight.game.heroes.ClassEquipment;
import kz.hapyl.fight.game.heroes.ComplexHero;
import kz.hapyl.fight.game.heroes.Hero;
import kz.hapyl.fight.game.heroes.Heroes;
import kz.hapyl.fight.game.talents.Talent;
import kz.hapyl.fight.game.talents.Talents;
import kz.hapyl.fight.game.talents.UltimateTalent;
import kz.hapyl.fight.game.task.GameTask;
import kz.hapyl.fight.game.weapons.Weapon;
import kz.hapyl.fight.util.KeepNull;
import kz.hapyl.fight.util.Utils;
import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.entity.Entities;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import kz.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;

import static org.bukkit.Sound.ENTITY_WITHER_DEATH;
import static org.bukkit.Sound.ENTITY_WITHER_SHOOT;

public class DarkMage extends Hero implements ComplexHero, Listener {

	private final int ultimateDuration = 240;

	public DarkMage() {
		super("Dark Mage");
		this.setInfo("A mage that was cursed by &8&lDark &8&lMagic&7&o. But even it couldn't kill him...");
		this.setItem(Material.CHARCOAL);

		final ClassEquipment eq = this.getEquipment();
		eq.setHelmet(
				"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTZjYTYzNTY5ZTg3Mjg3MjJlY2M0ZDEyMDIwZTQyZjA4NjgzMGUzNGU4MmRiNTVjZjVjOGVjZDUxYzhjOGMyOSJ9fX0="
		);
		eq.setChestplate(102, 255, 255);
		eq.setLeggings(Material.IRON_LEGGINGS);
		eq.setBoots(153, 51, 51);

		this.setWeapon(new Weapon(Material.WOODEN_HOE).setName("Wand").setDamage(5.0d));

		/**
		 * Transform to the wither for &b%ss&7. While transformed, &e&lCLICK &7to shoot wither skulls that deals massive damage on impact. After wither disappears, you perform plunging attack that deals damage in AoE upon hitting the ground.
		 */

		this.setUltimate(new UltimateTalent(
				"Wither Rider",
				"Transform into the wither for &b%ss&7. While transformed, &e&lCLICK &7to shoot wither skulls that deals massive damage on impact. After wither disappears, you perform plunging attack that deals damage in AoE upon hitting the ground."
						.formatted(BukkitUtils.roundTick(ultimateDuration)),
				70
		) {
			@Override
			public void useUltimate(Player player) {
				setUsingUltimate(player, true, ultimateDuration);

				player.setAllowFlight(true);
				player.setFlying(true);

				final double playerHealth = GamePlayer.getPlayer(player).getHealth();
				Utils.hidePlayer(player);

				final Wither wither = Entities.WITHER.spawn(player.getLocation(), me -> {
					me.setAI(false);
					me.setMaxHealth(playerHealth);
					me.setHealth(playerHealth);
					me.setCustomName(player.getName());
					me.setCustomNameVisible(true);
					me.setGlowing(true);
					me.setInvulnerable(false); // killable eya
				});

				updateWitherName(player, wither);
				Utils.hideEntity(wither, player);

				new GameTask() {
					private int tick = ultimateDuration;

					@Override
					public void run() {

						if (wither.isDead() || GamePlayer.getPlayer(player).isDead()) {
							killWither(!wither.isDead() ? null : player, wither);
							this.cancel();
							return;
						}

						if (tick < 0) {
							killWither(player, wither);
							plungeAttack(player);
							this.cancel();
							return;
						}

						if (tick % 10 == 0) {
							updateWitherName(player, wither);
						}

						wither.teleport(player);
						--tick;
					}

					private void plungeAttack(Player player) {
						final int maxPlungeTime = 100;
						GamePlayer.getPlayer(player).addEffect(GameEffectType.FALL_DAMAGE_RESISTANCE, maxPlungeTime, true);
						player.setVelocity(new Vector(0.0d, -0.5d, 0.0d));

						new GameTask() {
							private int maxAirTicks = maxPlungeTime;

							@Override
							public void run() {
								if (maxAirTicks-- <= 0 || player.isOnGround()) {
									this.cancel();
									EnumEffect.tempDisplayGroundPunch(player);
									Utils.getPlayersInRange(player.getLocation(), 4).forEach(target -> {
										if (target == player) {
											return;
										}
										GamePlayer.damageEntity(target, 5.0d, player);
									});
								}

							}
						}.runTaskTimer(0, 1);
					}

					private void killWither(@Nullable Player player, Wither wither) {
						if (player != null) {
							player.setFlying(false);
							player.setAllowFlight(false);
							Utils.showPlayer(player);
						}
						PlayerLib.playSound(wither.getLocation(), ENTITY_WITHER_DEATH, 1.0f);
						wither.remove();
					}

				}.runTaskTimer(0, 1);

			}
		}.setItem(Material.WITHER_SKELETON_SKULL).setCdSec(30).setSound(Sound.ENTITY_WITHER_SPAWN, 2.0f));

	}

	private void updateWitherName(Player player, Wither wither) {
		wither.setCustomName(Chat.format("&4&l☠ &c%s &8| &a&l%s ❤", player.getName(), BukkitUtils.decimalFormat(wither.getHealth())));
	}

	@EventHandler()
	public void handleProjectileHit(ProjectileHitEvent ev) {
		if (!(ev.getEntity() instanceof WitherSkull skull) || !(skull.getShooter() instanceof Player player)) {
			return;
		}

		Utils.getPlayersInRange(skull.getLocation(), 3.0d).forEach(victim -> {
			GamePlayer.damageEntity(victim, 10.0d, player, EnumDamageCause.WITHER_SKULLED);
		});

	}

	@EventHandler()
	public void handleProjectileLaunch(PlayerInteractEvent ev) {
		final Player player = ev.getPlayer();
		final Action action = ev.getAction();

		if (!validatePlayer(player, Heroes.DARK_MAGE)
				|| !isUsingUltimate(player)
				|| ev.getHand() == EquipmentSlot.OFF_HAND
				|| action == Action.PHYSICAL) {
			return;
		}

		if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
			if (player.hasCooldown(this.getWeapon().getMaterial())) {
				return;
			}

			final WitherSkull skull = player.launchProjectile(WitherSkull.class, player.getLocation().getDirection().multiply(3.0d));
			skull.setCharged(true);
			skull.setYield(0.0f);
			skull.setShooter(player);

			player.setCooldown(this.getWeapon().getMaterial(), 20);
			PlayerLib.playSound(player, ENTITY_WITHER_SHOOT, 1.0f);

		}

	}

	@Override
	public Talent getFirstTalent() {
		return Talents.BLINDING_CURSE.getTalent();
	}

	@Override
	public Talent getSecondTalent() {
		return Talents.SLOWING_AURA.getTalent();
	}

	@Override
	public Talent getThirdTalent() {
		return Talents.HEALING_AURA.getTalent();
	}

	@Override
	public Talent getFourthTalent() {
		return Talents.SHADOW_CLONE.getTalent();
	}

	@Override
	public Talent getPassiveTalent() {
		return null;
	}

	@Override
	@KeepNull
	public Talent getFifthTalent() {
		return null;
	}

}
