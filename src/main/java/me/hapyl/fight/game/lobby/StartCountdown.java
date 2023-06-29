package me.hapyl.fight.game.lobby;

import me.hapyl.fight.game.task.GameTask;
import me.hapyl.spigotutils.module.chat.Chat;
import net.md_5.bungee.api.ChatColor;

/**
 * Represents a vote to start the game.
 */
public class StartCountdown extends GameTask {

    public static final String[] CHARACTERS = { "&a⓪", "&a①", "&2②", "&e③", "&6④", "&c⑤", "&c⑥", "&c⑦", "&c⑧", "&c⑨", "&c⑩" };

    private int countdown = 5;
    private boolean paused;

    public StartCountdown() {
        runTaskTimer(20, 20);
    }

    @Override
    public final void run() {
        if (countdown <= 0) {
            Chat.clearTitles();
            onCountdownFinish();
            cancel();
            return;
        }

        if (paused) {
            return;
        }

        // Fx
        Chat.sendTitles(ChatColor.of("#bbf0d6") + "ꜱᴛᴀʀᴛɪɴɢ ɪɴ", CHARACTERS[countdown], 0, 1000, 0);

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
}
