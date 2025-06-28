package me.hapyl.fight.activity;

import me.hapyl.fight.game.task.TickingGameTask;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.List;

public final class ActivityInstance extends TickingGameTask implements Iterable<Player> {
    
    private final List<Player> players;
    private final Activity activity;
    
    ActivityInstance(List<Player> players, Activity activity) {
        this.players = players;
        this.activity = activity;
        
        runTaskTimer(0, 1);
    }
    
    public boolean hasPlayer(@Nonnull Player player) {
        return players.contains(player);
    }
    
    @Nonnull
    public List<Player> players() {
        return players;
    }
    
    @Nonnull
    public Activity activity() {
        return activity;
    }
    
    @Override
    public void onTaskStart() {
        forEach(activity::onStart);
    }
    
    @Override
    public void onTaskStop() {
        forEach(activity::onStop);
    }
    
    @Override
    public void run(int tick) {
        for (Player player : this) {
            activity.onTick(player, tick);
        }
    }
    
    public void cancelKick(Player player) {
        activity.onKick(player);
        cancel0();
    }
    
    @Nonnull
    @Override
    public Iterator<Player> iterator() {
        return players.iterator();
    }
}
