package me.hapyl.fight.game.heroes.ronin;

import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.task.GameTask;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public class ChargeAttack extends GameTask {

    public static final int MAX_STRENGTH = 15;
    public static final long THRESHOLD = 500;

    private static final String CHAR_ON = "┋";
    private static final String CHAR_OFF = "┃";

    private final Ronin ronin;
    private final GamePlayer player;

    private long lastHeld;
    private double strength;

    public ChargeAttack(Ronin ronin, GamePlayer player) {
        this.ronin = ronin;
        this.player = player;

        player.playSound(Sound.ITEM_ARMOR_EQUIP_CHAIN, 0.0f);
        runTaskTimer(1, 1);
    }

    @Override
    public final void run() {
        // Stopped charging
        if (lastHeld != 0 && System.currentTimeMillis() - lastHeld >= THRESHOLD) {
            ronin.failChargeAttack(player);
            cancel();
            return;
        }

        // Display
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < MAX_STRENGTH; i++) {
            final boolean isCurrent = strength >= i && strength < i + 1;
            final Strength str = getStrength(i);

            builder.append(isCurrent ? str.colorCurrent : str.color).append(isCurrent ? CHAR_ON : CHAR_OFF);
        }

        // Fx
        player.sendSubtitle(builder.toString(), 0, 5, 5);
        player.addEffect(Effects.SLOW, 5, 1);
    }

    @Nonnull
    public Strength getStrength() {
        return getStrength(this.strength);
    }

    @Nonnull
    public Strength getStrength(double strength) {
        if (strength >= Strength.NORMAL.startIndex && strength < Strength.PERFECT.startIndex) {
            return Strength.NORMAL;
        }
        else if (strength >= Strength.PERFECT.startIndex && strength < Strength.WEAK.startIndex) {
            return Strength.PERFECT;
        }

        return Strength.WEAK;
    }

    public void increment() {
        lastHeld = System.currentTimeMillis();
        strength += 1.5d;

        if (strength >= MAX_STRENGTH) {
            ronin.failChargeAttack(player);
        }

        // Perfect Fx
        if (strength >= Strength.PERFECT.startIndex && strength < Strength.WEAK.startIndex) {
            player.playSound(Sound.ENTITY_ARROW_HIT_PLAYER, 0.75f);
            player.playSound(Sound.ENTITY_ARROW_HIT_PLAYER, 1.25f);
        }

        // Normal Fx
        if (strength <= Strength.WEAK.startIndex) {
            final float pitch = (float) (0.5f + (2.0f / Strength.PERFECT.startIndex * strength));

            player.playSound(Sound.BLOCK_NOTE_BLOCK_BASS, pitch);
            player.playSound(Sound.BLOCK_NOTE_BLOCK_BASEDRUM, pitch);
        }
        else {
            player.playSound(Sound.BLOCK_NOTE_BLOCK_BASS, 0.0f);
        }
    }

}
