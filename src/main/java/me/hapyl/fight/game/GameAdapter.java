package me.hapyl.fight.game;

import com.google.common.collect.Sets;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Set;
import java.util.function.Consumer;

public class GameAdapter {

    // has to be static
    private static final Set<GameAdapter> ADAPTERS = Sets.newConcurrentHashSet();

    public GameAdapter() {
        ADAPTERS.add(this);
    }

    /**
     * Called <b>once</b> at the start of the game.
     */
    public void onStart() {
    }

    /**
     * Called <b>once</b> at the start of the game for each player who is in game instance.
     *
     * @param player - Player.
     */
    public void onStart(Player player) {
    }

    /**
     * Called <b>once</b> at the end of the game.
     */
    public void onStop() {
    }

    /**
     * Called <b>once</b> at the end of the game for each player who is in the game instance.
     *
     * @param player - Player.
     */
    public void onStop(Player player) {
    }

    /**
     * Called <b>every time</b> a player dies in a game instance.
     *
     * @param player - Player who died.
     */
    public void onDeath(Player player) {
    }

    /**
     * Called <b>once</b> whenever players are revealed.
     */
    public void onPlayersRevealed() {
    }

    /**
     * Removes itself from adapters.
     *
     * @throws IllegalArgumentException if already removed.
     */
    public void dispose() throws IllegalArgumentException {
        if (!ADAPTERS.contains(this)) {
            throw new IllegalArgumentException("already disposed");
        }

        ADAPTERS.remove(this);
    }

    /**
     * Performs a for each iteration for all adapters.
     *
     * @param consumer - Consumer.
     */
    public static void forEach(@Nonnull Consumer<GameAdapter> consumer) {
        ADAPTERS.forEach(consumer);
    }

    /**
     * Returns a copy of adapters.
     *
     * @return a copy of adapters.
     */
    @Nonnull
    public static Set<GameAdapter> getAdapters() {
        return Sets.newHashSet(ADAPTERS);
    }

}
