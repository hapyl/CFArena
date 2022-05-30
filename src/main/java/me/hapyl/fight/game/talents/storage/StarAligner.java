package me.hapyl.fight.game.talents.storage;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.heroes.HeroHandle;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.storage.extra.AstralStars;
import me.hapyl.fight.util.Handle;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

public class StarAligner extends Talent {

	private final Handle<VortexStar> starHandle = () -> (VortexStar)Talents.VORTEX_STAR.getTalent();

	public StarAligner() {
		super("Star Aligner", "Align two nearby starts to teleport and launch an Astral Slash between them.");
		this.setItem(Material.BEETROOT_SEEDS);
		this.setCd(20);
	}

	@Override
	public Response execute(Player player) {
		final AstralStars stars = starHandle.getHandle().getStars(player);
		final List<LivingEntity> lastTwo = stars.getLastTwoStars();

		if (lastTwo.size() < 2) {
			return Response.error("There must be at least 2 stars!");
		}

		final LivingEntity starStart = lastTwo.get(0);
		final LivingEntity starEnd = lastTwo.get(1);

		if (starStart.getLocation().distance(player.getLocation()) > 3.5d) {
			return Response.error("You are too far away from an Astral Star!");
		}

		stars.removeStar(starStart);
		final Location location = starEnd.getLocation().clone();
		location.setYaw(player.getLocation().getYaw());
		location.setPitch(player.getLocation().getPitch());
		player.teleport(location);

		HeroHandle.VORTEX.performStarSlash(starStart.getEyeLocation(), starEnd.getEyeLocation(), player, false);

		PlayerLib.playSound(Sound.ENTITY_ENDERMAN_TELEPORT, 1.75f);
		return Response.OK;
	}
}
