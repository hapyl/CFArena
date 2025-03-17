package me.hapyl.fight.game.effect.effects;

import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.fight.game.effect.Effect;
import me.hapyl.fight.game.effect.EffectType;
import me.hapyl.fight.game.effect.Type;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.entity.cooldown.EntityCooldown;
import org.bukkit.Input;
import org.bukkit.Sound;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class Amnesia extends Effect {

    private static final EntityCooldown COOLDOWN = EntityCooldown.of("amnesia");

    public Amnesia() {
        super("Amnesia", Type.NEGATIVE);

        setDescription("""
                Players will move randomly and their vision is disturbed.
                """);
    }

    @Override
    public void onStart(@Nonnull LivingGameEntity entity, int amplifier, int duration) {
        entity.addPotionEffectIndefinitely(PotionEffectType.NAUSEA, 1);
    }

    @Override
    public void onStop(@Nonnull LivingGameEntity entity, int amplifier) {
        entity.removePotionEffect(PotionEffectType.NAUSEA);
    }

    @Override
    public void onTick(@Nonnull LivingGameEntity entity, int tick) {
        // Affect
        if (!(entity instanceof GamePlayer player) || isHorizontalInput(player.input())) {
            pushRandomly(entity);
        }

        // Fx
        if (entity.aliveTicks() % 25 == 0) {
            entity.playSound(Sound.ENTITY_WARDEN_AMBIENT, 2.0f);
        }
    }

    private void pushRandomly(LivingGameEntity entity) {
        if (entity.hasCooldown(COOLDOWN) || !entity.hasEffect(EffectType.AMNESIA)) {
            return;
        }

        final double x = entity.random.nextBoolean() ? 0.2 : -0.2;
        final double z = entity.random.nextBoolean() ? 0.2 : -0.2;

        entity.setVelocity(new Vector(x, -BukkitUtils.GRAVITY, z));
        entity.startCooldown(COOLDOWN, 100);
    }

    private boolean isHorizontalInput(Input input) {
        return input.isForward() || input.isBackward() || input.isRight() || input.isLeft();
    }
}
