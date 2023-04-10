package me.hapyl.fight.game.effect.storage;

import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.effect.EffectParticle;
import me.hapyl.fight.game.effect.GameEffect;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

public class Stun extends GameEffect {
	private final Map<Player, Float> oldSpeed = new HashMap<>();

	public Stun() {
		super("Stun");
        this.setDescription("Stunned players cannot move or use their abilities. Effect will be cleared upon taking damage.");
        this.setPositive(false);
		this.setEffectParticle(new EffectParticle(Particle.VILLAGER_ANGRY, 1));
	}

	@Override
	public void onTick(Player player, int tick) {
		displayParticles(player.getLocation().add(0.0d, 1.0d, 0.0d), player);
	}

	@Override
	public void onStart(Player player) {
		oldSpeed.put(player, player.getWalkSpeed());
		player.setWalkSpeed(0.0f);

		PlayerLib.addEffect(player, PotionEffectType.WEAKNESS, 999999, 250);
		GamePlayer.getPlayer(player).setCanMove(false);

		//PlayerLib.addEffect(player, PotionEffectType.JUMP, 999999, 250);
	}

	@Override
	public void onStop(Player player) {
		player.setWalkSpeed(oldSpeed.getOrDefault(player, 0.1f));
		PlayerLib.removeEffect(player, PotionEffectType.WEAKNESS);

		GamePlayer.getPlayer(player).setCanMove(true);

		//PlayerLib.removeEffect(player, PotionEffectType.JUMP);
	}
}
