package me.hapyl.fight.game.heroes.bloodfield.impel;

import com.google.common.collect.Sets;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.task.TickingGameTask;
import org.bukkit.Sound;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.util.Set;

public class Impel extends TickingGameTask {

    private final Type type;
    private final int duration;
    private final Set<GamePlayer> targetPlayers;

    public Impel(Type type, int duration) {
        this.duration = duration;
        this.type = type;
        this.targetPlayers = Sets.newHashSet();
    }

    public void addTargetPlayer(GamePlayer player) {
        targetPlayers.add(player);
    }

    public void onFail(GamePlayer player) {
    }

    public void onComplete(GamePlayer player) {
        player.sendSubtitle("&eImpel: &a&l✔", 0, 20, 5);
        player.playSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.75f);
    }

    public void onImpelStop() {
    }

    @Override
    public void run(int tick) {
        targetPlayers.removeIf(GamePlayer::isDeadOrRespawning);

        if (tick > duration) {
            targetPlayers.forEach(this::onFail);
            stop();
            onImpelStop();
            return;
        }

        final String timeLeft = new DecimalFormat("0.00").format((duration - tick) / 20d);

        targetPlayers.forEach(player -> {
            player.sendSubtitle("&eImpel: &b&l%s &c%ss".formatted(type.getName(), timeLeft), 0, 5, 0);
        });
    }

    @Override
    public void onFirstTick() {
        // Fx
        targetPlayers.forEach(player -> {
            player.playSound(Sound.ENTITY_BAT_TAKEOFF, 0.0f);
            player.playSound(Sound.ENTITY_BAT_HURT, 0.0f);
        });
    }

    public void start(int cd) {
        runTaskTimer(cd, 1);
    }

    public void stop() {
        cancel();
        targetPlayers.clear();
    }

    public void complete(@Nonnull GamePlayer player, @Nonnull Type type) {
        if (this.type != type || !targetPlayers.contains(player)) {
            return;
        }

        targetPlayers.remove(player);
        onComplete(player);
    }

    @Nonnull
    public Set<GamePlayer> getPlayers() {
        return targetPlayers;
    }
}
