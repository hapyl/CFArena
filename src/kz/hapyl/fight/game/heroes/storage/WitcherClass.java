package kz.hapyl.fight.game.heroes.storage;

import kz.hapyl.fight.event.DamageInput;
import kz.hapyl.fight.event.DamageOutput;
import kz.hapyl.fight.game.PlayerElement;
import kz.hapyl.fight.game.heroes.ClassEquipment;
import kz.hapyl.fight.game.heroes.ComplexHero;
import kz.hapyl.fight.game.heroes.Hero;
import kz.hapyl.fight.game.heroes.storage.extra.Combo;
import kz.hapyl.fight.game.talents.Talent;
import kz.hapyl.fight.game.talents.Talents;
import kz.hapyl.fight.game.talents.UltimateTalent;
import kz.hapyl.fight.game.talents.storage.Irden;
import kz.hapyl.fight.game.talents.storage.Kven;
import kz.hapyl.fight.game.task.GameTask;
import kz.hapyl.fight.game.ui.UIComponent;
import kz.hapyl.fight.game.weapons.Weapon;
import kz.hapyl.fight.util.Handle;
import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import kz.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

public class WitcherClass extends Hero implements ComplexHero, UIComponent, PlayerElement {

	private final Map<Player, Combo> combos = new HashMap<>();
	private final int ultimateDuration = 200;

	public WitcherClass() {
		super("The Witcher");
		this.setInfo("Some say, that he's the most trained Witcher ever; Well versed in any kind of magic...");
		this.setItem(Material.CRIMSON_ROOTS);

		final ClassEquipment equipment = this.getEquipment();
		equipment.setHelmet(
				"e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTEwOTA1YmU0ZjY3ZTJmY2FkMjkxY2RmOGFlYjJlOWZmNTVmZTkzZjI3YjhjMWYwOTU5MDI0YTNjYjRhNzA1MiJ9fX0="
		);
		equipment.setChestplate(44, 48, 101);
		equipment.setLeggings(60, 66, 69);
		equipment.setBoots(29, 29, 33);

		this.setWeapon(new Weapon(Material.IRON_SWORD).setName("Aerondight").setDamage(5.0d));

		this.setUltimate(new UltimateTalent(
				"All the Trainings",
				"Remember all your trainings and unleash them at once. Creating infinite %1$s shield and %2$s aura that follows you for &b%3$ss&7. Both %1$s and %2$s starts their cooldowns."
						.formatted(
								Talents.KVEN.getName(),
								Talents.IRDEN.getName(),
								BukkitUtils.roundTick(ultimateDuration)
						),
				80
		) {
			@Override
			public void useUltimate(Player player) {
				setUsingUltimate(player, true, ultimateDuration);

				Talents.KVEN.startCd(player);
				Talents.IRDEN.startCd(player);

				PlayerLib.addEffect(player, PotionEffectType.DAMAGE_RESISTANCE, ultimateDuration, 1);

				new GameTask() {
					private int tick = ultimateDuration;

					@Override
					public void run() {
						if (tick-- < 0) {
							this.cancel();
							return;
						}

						((Irden)Talents.IRDEN.getTalent()).affect(player, player.getLocation(), tick);
					}
				}.runTaskTimer(0, 1);

			}
		});

	}

	@Override
	public void onStart(Player player) {
		combos.put(player, new Combo(player));
	}

	@Override
	public void onStop() {
		combos.clear();
	}

	public Combo getCombo(Player player) {
		return combos.computeIfAbsent(player, Combo::new);
	}

	@Override
	public DamageOutput processDamageAsDamager(DamageInput input) {
		final Player player = input.getPlayer();
		final Combo combo = getCombo(player);
		double damage = input.getDamage();
		final LivingEntity entity = input.getEntity();

		if (combo.getEntity() == null && entity != null && entity != player) {
			combo.setEntity(entity);
		}

		if (!combo.validateSameEntity(entity)) {
			combo.reset();
		}

		if (combo.validateCanCombo()) {
			combo.incrementCombo();
		}
		else {
			combo.reset();
			return null;
		}

		final int comboHits = combo.getCombo();

		// combo starts at 2 hits
		if (comboHits > 2) {
			damage += damage * ((comboHits - 2) * 0.15);

			// fx
			PlayerLib.playSound(player, Sound.ITEM_SHIELD_BREAK, 1.75f);
			Chat.sendTitle(player, "        &6Combo", "          &4&lx" + (comboHits - 2), 0, 25, 25);
		}

		return new DamageOutput(damage);

	}

	@Override
	public DamageOutput processDamageAsVictim(DamageInput input) {
		final Kven kven = (Kven)getThirdTalent();
		final Player player = input.getPlayer();
		if (kven.getShieldCharge(player) > 0) {
			kven.removeShieldCharge(player);
			return new DamageOutput().setCancelDamage(true);
		}
		return null;
	}

	@Override
	public Talent getFirstTalent() {
		return Talents.AARD.getTalent();
	}

	@Override
	public Talent getSecondTalent() {
		return Talents.IGNY.getTalent();
	}

	@Override
	public Talent getThirdTalent() {
		return Talents.KVEN.getTalent();
	}

	@Override
	public Talent getFourthTalent() {
		return Talents.AKCIY.getTalent();
	}

	@Override
	public Talent getFifthTalent() {
		return Talents.IRDEN.getTalent();
	}

	@Override
	public Talent getPassiveTalent() {
		return Talents.COMBO_SYSTEM.getTalent();
	}

	private final Handle<Kven> kvenHandle = () -> (Kven)Talents.KVEN.getTalent();

	@Override
	public String getString(Player player) {
		final int shieldLevel = kvenHandle.getHandle().getShieldCharge(player);
		return shieldLevel > 0 ? "&2🛡 &l" + shieldLevel : "";
	}
}
