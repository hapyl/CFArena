package kz.hapyl.fight.game.heroes.storage;

import kz.hapyl.fight.event.DamageInput;
import kz.hapyl.fight.event.DamageOutput;
import kz.hapyl.fight.game.GamePlayer;
import kz.hapyl.fight.game.effect.GameEffectType;
import kz.hapyl.fight.game.heroes.ClassEquipment;
import kz.hapyl.fight.game.heroes.Hero;
import kz.hapyl.fight.game.heroes.Heroes;
import kz.hapyl.fight.game.talents.Talent;
import kz.hapyl.fight.game.talents.Talents;
import kz.hapyl.fight.game.talents.UltimateTalent;
import kz.hapyl.fight.game.task.GameTask;
import kz.hapyl.fight.game.ui.UIComponent;
import kz.hapyl.fight.game.weapons.Weapon;
import kz.hapyl.fight.util.Utils;
import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import kz.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class Ninja extends Hero implements Listener, UIComponent {

	private final double damage = 8.0d;
	private final int stunCd = 200;
	private final Weapon normalSword = new Weapon(Material.STONE_SWORD).setName("斬馬刀").setDamage(damage / 2.0d);

	private final double ultimateDamage = 20.0d;

	private final ItemStack throwingStar = new ItemBuilder(Material.NETHER_STAR, "THROWING_STAR")
			.setName("Throwing Star")
			.setAmount(5)
			.addClickEvent(this::shootStar)
			.withCooldown(10)
			.build();

	public Ninja() {
		super("Ninja");
		this.setInfo("Extremely well trained fighter with a gift from the wind, that allows him to Dash, Double Jump and take no fall damage.");
		this.setItem(Material.IRON_BOOTS);

		final ClassEquipment equipment = this.getEquipment();
		equipment.setLeggings(Material.CHAINMAIL_LEGGINGS);
		equipment.setBoots(Material.CHAINMAIL_BOOTS);

		this.setWeapon(new Weapon(Material.STONE_SWORD)
				.setName("斬馬刀")
				.setInfo(
						String.format(
								"Light but sharp sword that stuns opponents upon charge hit. After using the charge hit, your weapon damage is reduced by &b50%%&7.__&9Cooldown: &l%ss",
								BukkitUtils.decimalFormat(ultimateDamage)
						)
				).setDamage(damage));

		this.setUltimate(new UltimateTalent(
				"Throwing Stars",
				"Equip 5 dead-accurate throwing stars that deals &c%s &7damage upon hitting an enemy.".formatted(ultimateDamage),
				70
		).setItem(Material.NETHER_STAR).setSound(Sound.ITEM_TRIDENT_RIPTIDE_1, 0.75f));

	}

	@Override
	public void useUltimate(Player player) {
		setUsingUltimate(player, true);
		final PlayerInventory inventory = player.getInventory();
		inventory.setItem(4, throwingStar);
		inventory.setHeldItemSlot(4);
		player.setCooldown(throwingStar.getType(), 20);
	}

	private void shootStar(Player player) {
		final ItemStack item = player.getInventory().getItemInMainHand();
		item.setAmount(item.getAmount() - 1);

		if (item.getAmount() <= 0) {
			setUsingUltimate(player, false);
		}

		Utils.rayTraceLine(
				player,
				40,
				0.5d,
				ultimateDamage,
				move -> PlayerLib.spawnParticle(move, Particle.FIREWORKS_SPARK, 1, 0.0d, 0.0d, 0.0d, 0.0f),
				hit -> PlayerLib.playSound(hit.getLocation(), Sound.ITEM_TRIDENT_HIT, 2.0f)
		);

		PlayerLib.playSound(player.getLocation(), Sound.ITEM_TRIDENT_THROW, 1.5f);

	}

	@Override
	public void onStart(Player player) {
		PlayerLib.addEffect(player, PotionEffectType.SPEED, 999999, 0);
		PlayerLib.addEffect(player, PotionEffectType.JUMP, 999999, 255);
		player.setAllowFlight(true);
	}

	@EventHandler()
	public void handleDoubleJump(PlayerToggleFlightEvent ev) {
		final Player player = ev.getPlayer();
		if (!validatePlayer(player, Heroes.NINJA) || player.hasCooldown(this.getItem().getType()) || player.isFlying()) {
			return;
		}

		ev.setCancelled(true);

		final Location location = player.getLocation();
		player.setVelocity(new Vector(0.0d, 1.0d, 0.0d));
		player.setFlying(false);
		player.setAllowFlight(false);
		player.setCooldown(this.getItem().getType(), 100);
		GameTask.runLater(() -> {
			player.setAllowFlight(true);
		}, 100);

		// fx
		PlayerLib.playSound(player.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 1.2f);
		PlayerLib.spawnParticle(location, Particle.EXPLOSION_NORMAL, 5, 0.2d, 0.0d, 0.2d, 0.03f);
	}

	@Override
	public String getString(Player player) {
		return player.hasCooldown(this.getItem().getType())
				? "&f🌊 &l%ss".formatted(BukkitUtils.roundTick(player.getCooldown(this.getItem().getType())))
				: "";
	}

	@Override
	public DamageOutput processDamageAsDamager(DamageInput input) {
		final Player player = input.getPlayer();
		final LivingEntity entity = input.getEntity();
		if (entity == null || entity == player || player.hasCooldown(this.getWeapon().getMaterial())) {
			return null;
		}

		// remove smoke bomb invisibility if exists
		if (GamePlayer.getPlayer(player).hasEffect(GameEffectType.INVISIBILITY)) {
			GamePlayer.getPlayer(player).removeEffect(GameEffectType.INVISIBILITY);
			Chat.sendMessage(player, "&aYour invisibility is gone because you dealt damage.");
			PlayerLib.playSound(player, Sound.ITEM_SHIELD_BREAK, 2.0f);
		}

		if (player.getInventory().getHeldItemSlot() != 0) {
			return null;
		}

		player.getInventory().setItem(0, normalSword.getItem());
		player.setCooldown(this.getWeapon().getMaterial(), stunCd);

		// Fx
		PlayerLib.playSound(entity.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1.25f);
		PlayerLib.spawnParticle(entity.getEyeLocation(), Particle.VILLAGER_ANGRY, 5, 0.2d, 0.2d, 0.2d, 0.0f);

		GameTask.runLater(() -> {
			player.getInventory().setItem(0, this.getWeapon().getItem());
		}, stunCd);

		return null;
	}

	@Override
	public DamageOutput processDamageAsVictim(DamageInput input) {
		if (input.getDamageCause() == EntityDamageEvent.DamageCause.FALL) {
			return new DamageOutput(true);
		}
		return null;
	}

	@Override
	public void onStart() {

	}

	@Override
	public Talent getFirstTalent() {
		return Talents.NINJA_DASH.getTalent();
	}

	@Override
	public Talent getSecondTalent() {
		return Talents.NINJA_SMOKE.getTalent();
	}

	@Override
	public Talent getPassiveTalent() {
		return Talents.FLEET_FOOT.getTalent();
	}
}
