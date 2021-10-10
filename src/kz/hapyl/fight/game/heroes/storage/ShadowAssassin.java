package kz.hapyl.fight.game.heroes.storage;

import kz.hapyl.fight.event.DamageInput;
import kz.hapyl.fight.event.DamageOutput;
import kz.hapyl.fight.game.EnumDamageCause;
import kz.hapyl.fight.game.GamePlayer;
import kz.hapyl.fight.game.heroes.ClassEquipment;
import kz.hapyl.fight.game.heroes.Hero;
import kz.hapyl.fight.game.heroes.Heroes;
import kz.hapyl.fight.game.talents.Talent;
import kz.hapyl.fight.game.talents.Talents;
import kz.hapyl.fight.game.talents.UltimateTalent;
import kz.hapyl.fight.game.task.GameTask;
import kz.hapyl.fight.game.weapons.Weapon;
import kz.hapyl.fight.util.Utils;
import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.particle.ParticleBuilder;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import kz.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ShadowAssassin extends Hero implements Listener {

	private final int backstabCd = 400;
	private final int ultimateDuration = 200;

	public ShadowAssassin() {
		super("Shadow Assassin");
		this.setInfo("Well trained assassin from dimension of shadows.");
		this.setItem(Material.ENDERMAN_SPAWN_EGG);

		final ClassEquipment eq = this.getEquipment();
		eq.setHelmet(
				"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWI4YWY1MmVmMmY3MmMzYmY1ZWNlNmU3MGE4MmYxMzcxOTU5Y2UzZmNiNzM2YzUwMDMwNWNhZGRjNTA1YzVlMiJ9fX0=");
		eq.setChestplate(Color.BLACK);
		eq.setLeggings(Color.BLACK);
		eq.setBoots(Color.BLACK);

		this.setWeapon(new Weapon(Material.IRON_SWORD)
				.setName("Livid Dagger")
				.setLore(
						"A dagger filled with bad memories.__&e&lBACKSTAB &7to perform a charged attack that knows enemies and stuns them for a short time.__&9Cooldown: &l%ss"
								.formatted(BukkitUtils.roundTick(backstabCd)))
				.setDamage(8.0d));

		this.setUltimate(new UltimateTalent(
				"Extreme Focus",
				"Enter &bExtreme Focus &7for &b%ss&7. While active, you will not miss your hits if target is close enough and has no cover."
						.formatted(BukkitUtils.roundTick(ultimateDuration)),
				80
		) {
			@Override
			public void useUltimate(Player player) {
				setUsingUltimate(player, true, ultimateDuration);
				player.setCooldown(getWeapon().getMaterial(), 0);

				// fx
				PlayerLib.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1.75f);
				PlayerLib.playSound(player.getLocation(), Sound.BLOCK_BEACON_AMBIENT, 1.75f);
				PlayerLib.addEffect(player, PotionEffectType.SLOW, ultimateDuration, 0);

				GameTask.runLater(() -> {
					PlayerLib.playSound(Sound.BLOCK_BEACON_DEACTIVATE, 1.85f);
				}, ultimateDuration);

			}
		}.setCdSec(40).setItem(Material.GOLDEN_CARROT));

	}

	@EventHandler()
	public void handleUltimate(PlayerInteractEvent ev) {
		final Player player = ev.getPlayer();
		if (ev.getHand() == EquipmentSlot.OFF_HAND
				|| ev.getAction() == Action.PHYSICAL
				|| !validatePlayer(player, Heroes.SHADOW_ASSASSIN)
				|| !isUsingUltimate(player)
				|| player.hasCooldown(this.getWeapon().getMaterial())) {
			return;
		}

		final LivingEntity livingEntity = getNearestEntity(player);
		if (livingEntity == null) {
			Chat.sendMessage(player, "&cNo valid opponent!");
			return;
		}

		GamePlayer.damageEntity(livingEntity, this.getWeapon().getDamage(), player, EnumDamageCause.NEVERMISS);
		player.setCooldown(this.getWeapon().getMaterial(), 20);

		// fx
		PlayerLib.playSound(player.getLocation(), Sound.BLOCK_NETHER_ORE_BREAK, 1.75f);

	}

	@Nullable
	private LivingEntity getNearestEntity(Player player) {
		final Location location = player.getLocation();
		LivingEntity closest = null;
		double distance = 0.0d;
		for (final LivingEntity living : Utils.getEntitiesInRange(location, 10)) {
			if (player == living) {
				continue;
			}
			final double currentDistance = living.getLocation().distance(location);
			if (closest == null || currentDistance < distance) {
				closest = living;
				distance = currentDistance;
			}
		}
		return closest;
	}

	@Override
	public DamageOutput processDamageAsDamager(DamageInput input) {
		final Player player = input.getPlayer();
		if (player.isSneaking() && (input.getEntity() != null && input.getEntity() != player)) {
			PlayerLib.playSoundMessage(player, Sound.ENTITY_ENDERMAN_TELEPORT, 0.0f, "&cCannot deal damage while in &lDark Cover&c!");
			return new DamageOutput().setCancelDamage(true);
		}

		// calculate back stab
		final LivingEntity entity = input.getEntity();
		if (validateCanBackStab(player, entity)) {
			if (player.getLocation().getDirection().dot(entity.getLocation().getDirection()) > 0) {
				performBackStab(player, entity);
			}
		}

		return null;
	}

	@Override
	public DamageOutput processDamageAsVictim(DamageInput input) {
		final Player player = input.getPlayer();
		if (player.isSneaking()) {
			player.setSneaking(false);
			kickFromDarkCover(player);
		}

		return null;
	}

	public void setDarkCoverCd(Player player, int cd) {
		player.setCooldown(Talents.SECRET_SHADOW_WARRIOR_TECHNIQUE.getTalent().getMaterial(), cd);
	}

	public int getDarkCoverCd(Player player) {
		return player.getCooldown(Talents.SECRET_SHADOW_WARRIOR_TECHNIQUE.getTalent().getMaterial());
	}

	public void setDarkCover(Player player, boolean flag) {
		final boolean sneaking = !player.isSneaking();

		// Enter
		if (flag && sneaking) {
			if (getDarkCoverCd(player) > 0) {
				Chat.sendMessage(player, "&cCannot enter &lDark Cover &cfor another &l%ss&c.", BukkitUtils.roundTick(getDarkCoverCd(player)));
				player.setSneaking(false);
				return;
			}

			PlayerLib.addEffect(player, PotionEffectType.INVISIBILITY, 999999, 5);
			Utils.hidePlayer(player);

			playDarkCoverFx(player, true);

		}
		else if (!sneaking) {
			PlayerLib.removeEffect(player, PotionEffectType.INVISIBILITY);
			Utils.showPlayer(player);

			playDarkCoverFx(player, false);
		}

	}

	public void displayFootprints(Location location) {
		ParticleBuilder.blockDust(location.getBlock().getRelative(BlockFace.DOWN).getType())
				.setAmount(3)
				.setOffX(0.25d)
				.setOffZ(0.25d)
				.display(location);
	}

	public void playDarkCoverFx(Player player, boolean flag) {
		final Location location = player.getEyeLocation();
		if (flag) {
			PlayerLib.spawnParticle(location, Particle.CRIT, 20, 0, 0.2, 0, 1.0f);
			PlayerLib.spawnParticle(location, Particle.CRIT_MAGIC, 20, 0, 0.2, 0, 0.5f);
			PlayerLib.spawnParticle(location, Particle.WARPED_SPORE, 10, 0, 0.5, 0, 0);
			PlayerLib.playSound(location, Sound.ENTITY_ENDERMAN_TELEPORT, 1.75f);
		}
		else {
			PlayerLib.spawnParticle(location, Particle.ENCHANTMENT_TABLE, 10, 0, 0, 0, 2);
			PlayerLib.playSound(location, Sound.ENTITY_ENDERMAN_TELEPORT, 1.25f);
		}
	}

	public void kickFromDarkCover(Player player) {
		final Location location = player.getEyeLocation();
		setDarkCoverCd(player, 60);
		setDarkCover(player, false);

		// fx
		Chat.sendMessage(player, "&cYou took damage and lost your &lDark Cover&c!");
		PlayerLib.spawnParticle(location, Particle.DRAGON_BREATH, 30, 0, 0, 0, 0.5f);
		PlayerLib.spawnParticle(location, Particle.LAVA, 35, 0, 0, 0, 0);
		PlayerLib.playSound(location, Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 1.75f);
		PlayerLib.playSound(location, Sound.ENTITY_ENDERMAN_SCREAM, 1.25f);
	}

	private boolean validateCanBackStab(Player player, LivingEntity entity) {
		return entity != null && !isUsingUltimate(player) && player != entity && !player.hasCooldown(this.getWeapon()
				.getMaterial()) && player.getInventory().getHeldItemSlot() == 0;
	}

	public void performBackStab(Player player, @Nonnull LivingEntity entity) {
		final Location location = entity.getLocation();
		final Vector vector = location.getDirection();
		entity.setVelocity(new Vector(vector.getX(), 0.1d, vector.getZ()).multiply(2.13f));

		entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 5));
		entity.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 40, 5));

		if (entity instanceof Player playerEntity) {
			Chat.sendMessage(playerEntity, "&a%s stabbed you!", player.getName());
		}

		player.setCooldown(this.getWeapon().getMaterial(), backstabCd);

		// fx
		PlayerLib.playSound(location, Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 0.65f);
		PlayerLib.spawnParticle(location, Particle.CRIT, 10, 0.25d, 0.0d, 0.25d, 0.076f);
	}

	@EventHandler()
	public void handlePlayerToggleSneakEvent(PlayerToggleSneakEvent ev) {
		final Player player = ev.getPlayer();
		if (!validatePlayer(player, Heroes.SHADOW_ASSASSIN)) {
			return;
		}

		setDarkCover(player, ev.isSneaking());
	}

	@EventHandler()
	public void handlePlayerMoveEvent(PlayerMoveEvent ev) {
		final Player player = ev.getPlayer();
		final Location from = ev.getFrom();
		final Location to = ev.getTo();
		if (to == null || !validatePlayer(player, Heroes.SHADOW_ASSASSIN) || !player.isSneaking()) {
			return;
		}

		// make sure we moved, not mouse movement
		if (from.getX() == to.getX() || from.getY() == to.getY() || from.getZ() == to.getZ()) {
			return;
		}

		displayFootprints(to);
	}

	@Override
	public Talent getFirstTalent() {
		return Talents.SHADOW_PRISM.getTalent();
	}

	@Override
	public Talent getSecondTalent() {
		return Talents.SHROUDED_STEP.getTalent();
	}

	@Override
	public Talent getPassiveTalent() {
		return Talents.SECRET_SHADOW_WARRIOR_TECHNIQUE.getTalent();
	}
}
