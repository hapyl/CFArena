package me.hapyl.fight.game.talents.storage;

import me.hapyl.fight.game.effect.EffectParticle;
import me.hapyl.fight.game.effect.GameEffect;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class ParanoiaEffect extends GameEffect {

	private final Sound[] decoySounds = {Sound.BLOCK_STONE_STEP, Sound.BLOCK_WOOD_STEP, Sound.ENTITY_PLAYER_HURT, Sound.AMBIENT_CAVE, Sound.ENTITY_PLAYER_BIG_FALL, Sound.ENTITY_PLAYER_SMALL_FALL};

	public ParanoiaEffect() {
		super("Paranoia");
		this.setAbout("Blinds players and plays decoy sounds around them.");
		this.setPositive(false);
		this.setEffectParticle(new EffectParticle(Particle.SQUID_INK, 5, 0.175d, 0.175d, 0.175d, 0.02f));
	}

	@Override
	public void onTick(Player player, int tick) {
		// play once per second
		if (tick == 0) {
			// Display paranoia for all players but the viewer
			final Location spawnLocation = player.getLocation().clone().add(0, 1.7d, 0);
			this.displayParticles(spawnLocation, player);

			// Get random location to play decoy sound
			Location location = player.getLocation();
			location.add(new Random().nextDouble() * 3, 0, new Random().nextDouble() * 3);
			location.subtract(new Random().nextDouble() * 3, 0, new Random().nextDouble() * 3);

			final Sound sound = decoySounds[new Random().nextInt(decoySounds.length)];
			player.playSound(location, sound, SoundCategory.MASTER, 50, sound == Sound.AMBIENT_CAVE ? 2 : 1);
		}
	}

	@Override
	public void onStart(Player player) {
		PlayerLib.addEffect(player, PotionEffectType.BLINDNESS, 99999, 1);
	}

	@Override
	public void onStop(Player player) {
		// This needed for smooth fade-out
		PlayerLib.removeEffect(player, PotionEffectType.BLINDNESS);
		PlayerLib.addEffect(player, PotionEffectType.BLINDNESS, 20, 1);
	}
}
