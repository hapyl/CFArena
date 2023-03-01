package me.hapyl.fight.game.talents.storage.km;

import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Utils;
import me.hapyl.spigotutils.module.particle.ParticleBuilder;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class LaserEye extends Talent {

	private final int duration = 60;

	public LaserEye() {
		super("Laser Eye");
        this.setDescription(
                "Become immovable and activate laser for &b%ss&7 that rapidly damages enemies.",
                BukkitUtils.roundTick(duration)
        );
        this.setItem(Material.ENDER_EYE);
		this.setCdSec(15);
	}

	@Override
	public Response execute(Player player) {
		GamePlayer.getPlayer(player).addEffect(GameEffectType.IMMOVABLE, duration, true);
		PlayerLib.addEffect(player, PotionEffectType.JUMP, duration, 250);
		PlayerLib.addEffect(player, PotionEffectType.SLOW, duration, 255);

		GameTask.runTaskTimerTimes((task, tick) -> {
			Utils.rayTraceLine(player, 50, 0.5d, 0.0d, move -> {
				if (move.getBlock().isPassable()) {
					ParticleBuilder.redstoneDust(Color.RED).display(move);
				}
			}, entity -> {
				GamePlayer.damageEntityTick(entity, 1.0d, player, EnumDamageCause.LASER, 10);
				PlayerLib.spawnParticle(entity.getLocation(), Particle.LAVA, 2, 0, 0, 0, 0);
			});

			if (tick == 0) {
				PlayerLib.stopSound(Sound.ENTITY_BEE_LOOP_AGGRESSIVE);
			}
		}, 1, duration);

		// Fx
		PlayerLib.playSound(player.getLocation(), Sound.ENTITY_BEE_LOOP_AGGRESSIVE, 1.25f);

		return Response.OK;
	}
}
