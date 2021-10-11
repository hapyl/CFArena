package kz.hapyl.fight.game.heroes.storage;

import kz.hapyl.fight.game.Manager;
import kz.hapyl.fight.game.effect.GameEffectType;
import kz.hapyl.fight.game.heroes.ClassEquipment;
import kz.hapyl.fight.game.heroes.Hero;
import kz.hapyl.fight.game.heroes.Heroes;
import kz.hapyl.fight.game.talents.Talent;
import kz.hapyl.fight.game.talents.Talents;
import kz.hapyl.fight.game.talents.UltimateTalent;
import kz.hapyl.fight.game.weapons.Weapon;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

public class Nightmare extends Hero implements Listener {

	public Nightmare() {
		super("Nightmare");
		this.setInfo("A spirit from the worst dreams and nightmares, blinds enemies and strikes from behind!");
		this.setItem(Material.WITHER_SKELETON_SKULL);

		this.setWeapon(new Weapon(Material.NETHERITE_SWORD)
				.setName("Omen")
				.setLore("A sword that is capable of splitting dreams in half.")
				.setDamage(7.0d));

		final ClassEquipment eq = this.getEquipment();
		eq.setHelmet(
				"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzljNTVlMGU0YWY3MTgyNGU4ZGE2OGNkZTg3ZGU3MTdiMjE0ZjkyZTk5NDljNGIxNmRhMjJiMzU3Zjk3YjFmYyJ9fX0=");
		eq.setChestplate(50, 0, 153);
		eq.setLeggings(40, 0, 153);
		eq.setBoots(30, 0, 153);

		this.setUltimate(new UltimateTalent(
				"Your Worst Nightmare",
				"Applies the &e&lParanoia &7effect to all alive opponents for {duration}.",
				55
		).setDuration(240).setCdSec(30).setItem(Material.BLACK_DYE).setSound(Sound.ENTITY_WITCH_CELEBRATE, 0.0f));

	}

	@Override
	public void useUltimate(Player player) {
		Manager.current().getCurrentGame().getAlivePlayers().forEach(alive -> {
			if (alive.compare(player)) {
				return;
			}
			alive.addEffect(GameEffectType.PARANOIA, getUltimateDuration(), true);
		});
	}

	@EventHandler()
	public void handleMovement(PlayerMoveEvent ev) {
		final Player player = ev.getPlayer();
		final Location from = ev.getFrom();
		final Location to = ev.getTo();
		if (!validatePlayer(player, Heroes.NIGHTMARE) || to == null) {
			return;
		}

		// check if only we moved a full block
		if (to.getBlock().getLightLevel() <= 7) {
			PlayerLib.spawnParticle(player.getLocation(), Particle.LAVA, 2, 0.15d, 0.15d, 0.15d, 0);
			PlayerLib.addEffect(player, PotionEffectType.SPEED, 20, 1);
			PlayerLib.addEffect(player, PotionEffectType.INCREASE_DAMAGE, 20, 0);
		}

	}

	@Override
	public Talent getFirstTalent() {
		return Talents.PARANOIA.getTalent();
	}

	@Override
	public Talent getSecondTalent() {
		return Talents.SHADOW_SHIFT.getTalent();
	}

	@Override
	public Talent getPassiveTalent() {
		return Talents.IN_THE_SHADOWS.getTalent();
	}
}
