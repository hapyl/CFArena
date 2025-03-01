package me.hapyl.fight.game.entity.ping;

import me.hapyl.eterna.module.util.Buffer;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.cooldown.EntityCooldown;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Resettable;

import javax.annotation.Nonnull;

public class PlayerPing implements Resettable {

    private static final EntityCooldown COOLDOWN = EntityCooldown.of("player_ping", 500L);

    protected final GamePlayer player;
    protected final Buffer<Ping> buffer;

    private GameTask task;
    private int requests;

    public PlayerPing(GamePlayer player) {
        this.player = player;
        this.buffer = new Buffer<>(3) {
            @Override
            public void unbuffered(@Nonnull Ping ping) {
                ping.remove();
            }
        };

        this.requests = 0;
    }

    public boolean isOnCooldown() {
        return player.hasCooldown(COOLDOWN);
    }

    public void requestedPing() {
        requests++;

        if (task != null) {
            task.cancel();
        }

        // Warning
        if (requests >= 2) {
            ping(PingType.WARNING);
            return;
        }

        final int ping = player.getPing();
        final int later = 5 + ping / 50;

        task = new GameTask() {
            @Override
            public void run() {
                ping(PingType.NORMAL);
            }
        }.runTaskLater(later);
    }

    @Override
    public void reset() {
        buffer.forEach(Ping::cancel);
        buffer.clear();
    }

    private void ping(@Nonnull PingType type) {
        player.startCooldown(COOLDOWN);
        requests = 0;

        buffer.add(new Ping(this, type));
    }

}
