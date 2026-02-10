package me.hapyl.fight.game.lobby;

import me.hapyl.eterna.module.chat.CenterChat;
import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.chat.LazyEvent;
import me.hapyl.eterna.module.math.Numbers;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.fight.game.task.GameTask;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

/**
 * Represents a vote to start the game.
 */
public class StartCountdown extends GameTask {

    public static final String[] CHARACTERS = { "&a⓪", "&a①", "&2②", "&e③", "&6④", "&c⑤", "&c⑥", "&c⑦", "&c⑧", "&c⑨", "&c⑩" };

    private int countdown = 5;
    private boolean paused;

    public StartCountdown() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            Chat.sendMessage(player, "");
            CenterChat.sendCenteredMessage(player, "&2&lsᴛᴀʀᴛɪɴɢ");

            CenterChat.sendCenteredClickableMessage(
                    player,
                    "&e&lCLICK HERE&a if you are &lnot&a ready!",
                    LazyEvent.showText("&eClick to cancel countdown!"),
                    LazyEvent.runCommand("/cancelcountdown")
            );

            Chat.sendMessage(player, "");
        });

        runTaskTimer(0, 20);
    }

    @Override
    public final void run() {
        if (countdown <= 0) {
            cancel();
            onCountdownFinish();
            return;
        }

        if (paused) {
            return;
        }

        // Fx
        Chat.sendTitles(ChatColor.of("#bbf0d6") + "ꜱᴛᴀʀᴛɪɴɢ ɪɴ", CHARACTERS[countdown], 0, 30, 0);

        final float pitch = Numbers.clamp(2f - (0.2f * countdown), 0.0f, 2.0f);

        PlayerLib.playSound(Sound.BLOCK_NOTE_BLOCK_PLING, pitch);
        PlayerLib.playSound(Sound.BLOCK_NOTE_BLOCK_FLUTE, pitch);
        PlayerLib.playSound(Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE, pitch);

        countdown--;
    }

    public void onCountdownFinish() {
    }

    public void pause() {
        paused = !paused;
    }

    public boolean isPaused() {
        return paused;
    }

    public void cancelByPlayer(@Nonnull Player player) {
        cancel(player.getName() + " doesn't want to play!");
    }

    public void cancel(@Nonnull String reason) {
        cancel();

        // Fx
        Chat.sendTitles(ChatColor.of("#DC143C") + "&lᴄᴀɴᴄᴇʟʟᴇᴅ", ChatColor.WHITE + reason, 0, 40, 20);
        PlayerLib.playSound(Sound.BLOCK_LEVER_CLICK, 0.0f);
    }
}
