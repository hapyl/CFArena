package me.hapyl.fight.game;

import com.google.common.collect.Sets;
import me.hapyl.fight.CF;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.util.Set;
import java.util.function.Consumer;

public class GameEventListener {

    // has to be static
    private static final Set<GameEventListener> LISTENERS = Sets.newConcurrentHashSet();

    public GameEventListener() {
        LISTENERS.add(this);

        if (this instanceof Listener listener) {
            Bukkit.getPluginManager().registerEvents(listener, CF.getPlugin());
        }
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
     * Performs a for each iteration for all adapters.
     *
     * @param consumer - Consumer.
     */
    public static void forEach(@Nonnull Consumer<GameEventListener> consumer) {
        LISTENERS.forEach(consumer);
    }

    /**
     * Returns a copy of listeners.
     *
     * @return a copy of listeners.
     */
    @Nonnull
    public static Set<GameEventListener> getListeners() {
        return Sets.newHashSet(LISTENERS);
    }

}
