package me.hapyl.fight.game.talents.storage.techie;

import io.netty.util.internal.ConcurrentSet;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.talents.ChargedTalent;
import me.hapyl.fight.game.talents.storage.extra.CyberCage;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TrapCage extends ChargedTalent implements Listener {

	private final int rechargeCd = 80;
	private final Map<Player, Set<CyberCage>> cageMap = new HashMap<>();

	public TrapCage() {
		super("CYber Cage", 3);
		this.setInfo(
				// Toss a cage in front of you, masking itself upon landing as a block below it. If an opponent steps on it, it will create explosion in small AoE that stuns, reveals location and applies &6&lVulnerability&7.____&e&lSNEAK &7near your cage to pick it up.
				// Toss a cage in front of you, masking itself upon landing as a block below it. Activates upon opponents touch and explodes in small AoE applying &bCYber Hack&7.____&e&lSNEAK &7near your cage to pick it up.
				"Toss a cage in front of you, masking itself upon landing as a block below it. Activates upon opponents touch and explodes in small AoE applying &bCYber Hack&7.____&e&lSNEAK &7near your cage to pick it up."
		);
		this.setItem(Material.IRON_TRAPDOOR);
		this.setCdSec(2);

		this.addExtraInfo("&aRecharge Time: &l%ss", BukkitUtils.roundTick(rechargeCd));
		this.addExtraInfo(" &8&oRecharges upon activation.");
	}

	@Override
	public void onDeath(Player player) {
		getCages(player).forEach(CyberCage::remove);
		cageMap.remove(player);
	}

	@Override
	public void onStop() {
		cageMap.values().forEach(set -> {
			set.forEach(CyberCage::remove);
			set.clear();
		});
		cageMap.clear();
	}

	@Override
	public Response execute(Player player) {
		getCages(player).add(new CyberCage(player));
		return Response.OK;
	}

	@EventHandler()
	public void handleMovement(PlayerMoveEvent ev) {
		final Player player = ev.getPlayer();
		if (!Manager.current().isPlayerInGame(player)) {
			return;
		}

		getNearbyCages(player, 2.0d).forEach(cage -> {
			if (cage.isOwner(player)) {
				return;
			}

			cage.activate(player);
			removeCage(cage);
			startCd(player, rechargeCd);
		});

	}

	@Override
	public void onStart() {
		new GameTask() {
			@Override
			public void run() {
				cageMap.values().forEach(set -> {
					set.forEach(CyberCage::drawParticle);
				});
			}
		}.runTaskTimer(10, 10);
	}

	@EventHandler()
	public void handleSneaking(PlayerToggleSneakEvent ev) {
		final Player player = ev.getPlayer();
		if (!Manager.current().isPlayerInGame(player)) {
			return;
		}

		final Set<CyberCage> cages = getCages(player);

		cages.forEach(cage -> {
			if (cage.compareDistance(player.getLocation(), 2.5d)) {
				removeCage(cage);

				// Fx
				Chat.sendMessage(player, "&aPicked up cage.");
				PlayerLib.playSound(player, Sound.BLOCK_IRON_TRAPDOOR_CLOSE, 0.5f);
				startCd(player, rechargeCd);
			}
		});
	}

	private void removeCage(CyberCage cage) {
		final Player player = cage.getPlayer();
		getCages(player).remove(cage);
		grantCharge(player);
		cage.remove();
	}

	private Set<CyberCage> getNearbyCages(Player player, double distance) {
		final Set<CyberCage> cages = new HashSet<>();
		final Location location = player.getLocation();

		cageMap.values().forEach(hashSet -> hashSet.stream()
				.filter(cage -> cage.compareDistance(location, distance))
				.forEach(cages::add));

		return cages;
	}

	public Set<CyberCage> getCages(Player player) {
		return cageMap.computeIfAbsent(player, k -> new ConcurrentSet<>());
	}
}
