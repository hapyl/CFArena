package me.hapyl.fight.game;

import com.google.common.collect.Sets;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Set;
import java.util.function.Consumer;

public class GameAdapter {

    // has to be static
    private static final Set<GameAdapter> ADAPTERS = Sets.newConcurrentHashSet();

    private boolean disposeOnStop;

    public GameAdapter() {
        ADAPTERS.add(this);
        disposeOnStop = false;
    }

    public void setDisposeOnStop(boolean disposeOnStop) {
        this.disposeOnStop = disposeOnStop;
    }

    public void onStart() {
    }

    public void onStart(Player player) {
    }

    public void onStop() {
    }

    public void onStop(Player player) {
        if (disposeOnStop) {
            dispose();
        }
    }

    public void onDeath(Player player) {
    }

    public void onPlayersRevealed() {
    }

    public final void dispose() {
        ADAPTERS.remove(this);
    }

    public static void forEach(@Nonnull Consumer<GameAdapter> consumer) {
        ADAPTERS.forEach(consumer);
    }

    @Nonnull
    public static Set<GameAdapter> getAdapters() {
        return ADAPTERS;
    }

}
