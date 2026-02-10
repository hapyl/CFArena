package me.hapyl.fight.game.talents.juju;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Climb extends Talent {

    @DisplayField(suffix = " Maximum Bounce Time") private final int cdDeadline = 80;
    @DisplayField private final int cooldown = 160;
    @DisplayField private final double magnitude = 0.6d;

    private final PlayerMap<GameTask> tasks = PlayerMap.newMap();

    public Climb(@Nonnull Key key) {
        super(key, "Climb");

        addDescription(
                "Use the wall you're hugging to climb it and perform back-flip, gaining speed boost. Cooldown of this ability stars upon landing or after &b{cdDeadline}&7."
        );

        setMaterial(Material.LEATHER_BOOTS);
    }

    @Override
    public void onStop(@Nonnull GameInstance instance) {
        tasks.clear();
    }

    public void cancelTask(GamePlayer player) {
        final GameTask task = tasks.get(player);

        if (task != null) {
            task.cancel();
            tasks.remove(player);
        }
    }

    @Override
    public @Nullable Response execute(@Nonnull GamePlayer player) {
        return Response.DEPRECATED;
    }

    private void taskController(GamePlayer player) {
        final GameTask oldTask = tasks.get(player);
        if (oldTask != null) {
            return;
        }

        tasks.put(player, GameTask.runTaskTimerTimes((self, tick) -> {
            if (tick == 0 || player.isOnGround()) {
                startCooldown(player, cooldown);
                self.cancel();
                tasks.remove(player);
            }
        }, 1, cdDeadline));
    }

}
