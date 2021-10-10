package kz.hapyl.fight.game.talents.storage;

import kz.hapyl.fight.game.Response;
import kz.hapyl.fight.game.talents.Talent;
import kz.hapyl.fight.game.talents.storage.extra.AstralStars;
import kz.hapyl.fight.game.task.GameTask;
import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VortexStar extends Talent {

	private final int maxStars = 5;
	private final Map<Player, AstralStars> stars = new HashMap<>();

	public VortexStar() {
		super("Astral Star");
		this.setDescription(
				"Summons an Astral Star at you current location. If used nearby placed Astral Star, the star will be picked up.____You may have maximum of &b%s &7stars at the same time.",
				maxStars
		);
		this.setItem(Material.NETHER_STAR);
		this.setCd(DYNAMIC);
	}

	@Override
	public void onDeath(Player player) {
		getStars(player).clear();
		stars.remove(player);
	}

	@Override
	public void onStop() {
		stars.values().forEach(AstralStars::clear);
		stars.clear();
	}

	public int getStarsAmount(Player player) {
		return getStars(player).getStarsAmount();
	}

	public AstralStars getStars(Player player) {
		return stars.computeIfAbsent(player, AstralStars::new);
	}

	@Override
	public void onStart() {
		new GameTask() {
			@Override
			public void run() {
				stars.values().forEach(as -> {
					as.tickStars();
					as.updateColors();
				});
			}
		}.runTaskTimer(0, 10);
	}

	@Override
	protected Response execute(Player player) {
		final int starsAmount = getStarsAmount(player);
		final AstralStars stars = getStars(player);
		final List<LivingEntity> twoStars = stars.getLastTwoStars();

		if (twoStars.size() >= 1) {
			final LivingEntity lastStar = twoStars.get(0);
			if (lastStar.getLocation().distance(player.getLocation()) <= stars.getPickupDistance()) {
				stars.removeStar(lastStar);
				startCd(player, 80);

				PlayerLib.playSound(player, Sound.BLOCK_BELL_RESONATE, 1.95f);
				Chat.sendMessage(player, "&aPick up an Astral Star.");

				return Response.OK;
			}
		}

		if (starsAmount >= maxStars) {
			PlayerLib.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 0.0f);
			return Response.error("Out of stars!");
		}

		startCd(player, 200);
		stars.summonStar(player.getEyeLocation());
		PlayerLib.playSound(player, Sound.BLOCK_BELL_USE, 1.75f);
		Chat.sendMessage(player, "&aCreated new Astral Star.");

		return Response.OK;
	}
}
