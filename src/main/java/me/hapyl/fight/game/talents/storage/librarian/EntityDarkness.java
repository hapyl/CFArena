package me.hapyl.fight.game.talents.storage.librarian;

import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.fight.game.heroes.HeroHandle;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.storage.extra.GrimoireTalent;
import me.hapyl.fight.util.Utils;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class EntityDarkness extends Talent implements GrimoireTalent {
	public EntityDarkness() {
		super("Darkness Spell");
		this.setDescription(String.format(
                "Launches a sprite of darkness, dealing %s damage and applying paranoia. This spell has 3 charges.",
                formatValues()
        ));
		this.setCd(10);
		this.setItem(Material.WITHER_ROSE);
		this.setAutoAdd(false);
	}

	@Override
	public Response execute(Player player) {
		if (HeroHandle.LIBRARIAN.hasICD(player)) {
			return ERROR;
		}

		final Location location = player.getEyeLocation();
		final Vector direction = location.getDirection();

		for (double i = 0.0d; i < 5.0d; i += 0.5d) {
			final double x = direction.getX() * i;
			final double y = direction.getY() * i;
			final double z = direction.getZ() * i;

			location.add(x, y, z);
			if (location.getBlock().getType().isOccluding()) {
				PlayerLib.playSound(location, Sound.BLOCK_STONE_STEP, 0.0f);
				break;
			}

			PlayerLib.spawnParticle(location, Particle.SQUID_INK, 1, 0.1, 0.05, 0.1, 0);
			Utils.getEntitiesInRange(location, 1.25d).forEach(victim -> {
				if (victim == player) {
					return;
				}

				GamePlayer.damageEntity(victim, getCurrentValue(player), player, EnumDamageCause.DARKNESS);
				if (victim instanceof Player playerVictim) {
					GamePlayer.getPlayer(playerVictim).addEffect(GameEffectType.PARANOIA, 20);
				}

				// Fx
				PlayerLib.playSound(location, Sound.BLOCK_STONE_STEP, 0.0f);
			});
		}

		HeroHandle.LIBRARIAN.removeSpellItems(player, Talents.ENTITY_DARKNESS);

		// Fx
		PlayerLib.playSound(player.getLocation(), Sound.ENTITY_SQUID_SQUIRT, 1.25f);
		return Response.OK;
	}

	@Override
	public double[] getValues() {
		return new double[]{6, 8, 10, 12};
	}

	@Override
	public int getGrimoireCd() {
		return 25;
	}
}
