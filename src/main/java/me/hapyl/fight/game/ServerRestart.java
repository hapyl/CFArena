package me.hapyl.fight.game;

import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.task.ShutdownAction;
import org.bukkit.Bukkit;

public class ServerRestart extends GameTask {

    private final RestartReason reason;

    public ServerRestart(RestartReason reason) {
        this.reason = reason;

        setShutdownAction(ShutdownAction.IGNORE);

        Bukkit.getOnlinePlayers().forEach(player -> {

        });
    }

    @Override
    public void run() {

    }


}
