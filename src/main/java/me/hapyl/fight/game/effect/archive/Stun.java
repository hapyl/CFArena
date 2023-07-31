package me.hapyl.fight.game.effect.archive;

import me.hapyl.fight.CF;
import me.hapyl.fight.game.effect.EffectParticle;
import me.hapyl.fight.game.effect.GameEffect;
import me.hapyl.fight.game.entity.GameEntity;
import me.hapyl.fight.game.entity.GamePlayer;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

public class Stun extends GameEffect {

    private final Map<LivingEntity, Float> oldSpeed = new HashMap<>();

    public Stun() {
        super("Stun");
        setDescription("Stunned players cannot move or use their abilities. Effect will be cleared upon taking damage.");
        setPositive(false);
        setTalentBlocking(true);
        setEffectParticle(new EffectParticle(Particle.VILLAGER_ANGRY, 1));
    }

    @Override
    public void onTick(GameEntity entity, int tick) {
        displayParticles(entity.getLocation().add(0.0d, 1.0d, 0.0d), entity.getEntity());
    }

    @Override
    public void onStart(GameEntity entity) {
        if (!(entity instanceof Player player)) {
            return;
        }

        oldSpeed.put(entity.getEntity(), player.getWalkSpeed());

        player.setWalkSpeed(0.0f);
        player.addPotionEffect(PotionEffectType.WEAKNESS.createEffect(999999, 250));

        CF.getOrCreatePlayer(player).setCanMove(false);
    }

    @Override
    public void onStop(GameEntity entity) {
        if (!(entity instanceof Player player)) {
            return;
        }

        player.setWalkSpeed(oldSpeed.getOrDefault(entity, 0.1f));
        player.removePotionEffect(PotionEffectType.WEAKNESS);

        GamePlayer.getPlayer(player).setCanMove(true);
    }
}
