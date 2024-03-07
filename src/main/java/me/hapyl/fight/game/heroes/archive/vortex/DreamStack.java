package me.hapyl.fight.game.heroes.archive.vortex;

import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.task.Cancellable;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.collection.player.PlayerMap;
import org.bukkit.ChatColor;
import org.bukkit.Sound;

public class DreamStack implements Cancellable {

    private static final int STACK_DURATION = 100;

    private final GamePlayer player;
    private final PlayerMap<DreamStack> owningMap;
    protected int stacks;
    private GameTask task;

    public DreamStack(GamePlayer player, PlayerMap<DreamStack> owningMap) {
        this.player = player;
        this.owningMap = owningMap;
        this.stacks = 0;

        increment();
        reschedule();
    }

    @Override
    public void cancel() {
        if (task != null) {
            task.cancel();
        }
    }

    public DreamStack increment() {
        stacks++;

        // Fx
        player.playSound(Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 1.75f);
        player.schedule(this::showStacks, 1);

        reschedule();
        return this;
    }

    private void showStacks() {
        player.sendSubtitle(ChatColor.YELLOW + Named.ASTRAL_SPARK.getCharacter().repeat(stacks), 3, 20, 10);
    }

    private void reschedule() {
        if (task != null) {
            task.cancel();
        }

        task = new GameTask() {
            @Override
            public void run() {
                if (--stacks > 0) {
                    reschedule();

                    // Fx
                    player.playSound(Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 0.75f);
                    showStacks();
                }
                else {
                    owningMap.remove(player);

                    // Fx
                    player.playSound(Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, 0.75f);
                    player.sendSubtitle(ChatColor.DARK_GRAY + Named.ASTRAL_SPARK.getCharacter() + " depleted!", 5, 25, 10);
                }
            }
        }.runTaskLater(STACK_DURATION);
    }
}
