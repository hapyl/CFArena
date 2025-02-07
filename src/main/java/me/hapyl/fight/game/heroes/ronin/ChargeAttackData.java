package me.hapyl.fight.game.heroes.ronin;

import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.ronin.ChargeAttack;
import me.hapyl.fight.game.task.GameTask;
import org.bukkit.Sound;

public class ChargeAttackData extends GameTask {

    private final GamePlayer player;
    private final ChargeAttack reference;

    private double strength;

    public ChargeAttackData(GamePlayer player, ChargeAttack reference) {
        this.player = player;
        this.reference = reference;
        this.strength = 0;

        runTaskTimer(0, 1);
    }

    @Override
    public void run() {
        strength++;

        if (strength > reference.maxChargeTime) {
            reference.failChargeAttack(player);
            return;
        }

        final boolean isPerfect = isPerfect();

        player.sendTitle(isPerfect ? "&c!!!" : "", "&7&lﾒ&8ﾒ&7&lﾒ&7&lﾒ&8ﾒ&7&lﾒ", 0, 5, 5);

        // Play sound
        final float pitch = (float) (0.5f + (1.0f * strength / reference.maxChargeTime));

        player.playSound(Sound.ENTITY_IRON_GOLEM_STEP, pitch);

        // Notify about perfect timing
        if (isPerfect) {
            player.playSound(
                    Sound.BLOCK_NOTE_BLOCK_HAT,
                    (float) (0.75f +
                            (0.75f * ((strength - reference.perfectAttackMin) / (reference.maxChargeTime - reference.perfectAttackMin))))
            );
        }

        // Slow player down a little
        player.addEffect(Effects.SLOW, 2, 2);
    }

    public boolean isPerfect() {
        return strength >= reference.perfectAttackMin && strength < reference.perfectAttackMax;
    }

    public double getStrength() {
        return strength;
    }
}
