package me.hapyl.fight.game.heroes.archive.ronin;

import me.hapyl.fight.game.HeroReference;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;

public class ChargeAttack extends GameTask implements HeroReference<Ronin> {

    public static final int MAX_STRENGTH = 15;
    public static final long THRESHOLD = 500;

    private static final String CHAR_ON = "┋";
    private static final String CHAR_OFF = "┃";

    private final Ronin ronin;
    private final Player player;

    private long lastHeld;
    private double strength;

    public ChargeAttack(Ronin ronin, Player player) {
        this.ronin = ronin;
        this.player = player;

        PlayerLib.playSound(player, Sound.ITEM_ARMOR_EQUIP_CHAIN, 0.0f);
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
        Chat.sendTitle(player, "", builder.toString(), 0, 5, 5);
        PlayerLib.addEffect(player, PotionEffectType.SLOW, 1, 5);
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
            PlayerLib.playSound(player, Sound.ENTITY_ARROW_HIT_PLAYER, 0.75f);
            PlayerLib.playSound(player, Sound.ENTITY_ARROW_HIT_PLAYER, 1.25f);
        }

        // Normal Fx
        if (strength <= Strength.WEAK.startIndex) {
            final float pitch = (float) (0.5f + (2.0f / Strength.PERFECT.startIndex * strength));

            PlayerLib.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASS, pitch);
            PlayerLib.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASEDRUM, pitch);
        }
        else {
            PlayerLib.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASS, 0.0f);
        }
    }

    @Nonnull
    @Override
    public Ronin getHero() {
        return ronin;
    }
}
