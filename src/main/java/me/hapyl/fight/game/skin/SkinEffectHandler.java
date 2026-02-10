package me.hapyl.fight.game.skin;

import me.hapyl.fight.game.entity.GameEntity;
import me.hapyl.fight.game.entity.GamePlayer;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface SkinEffectHandler {

    /**
     * Called every tick.
     *
     * @param player - Player.
     * @param tick   - The current tick.
     */
    void onTick(@Nonnull GamePlayer player, int tick);

    /**
     * Called whenever a player kills something.
     *
     * @param player - Player.
     * @param victim - Victim.
     */
    void onKill(@Nonnull GamePlayer player, @Nonnull GameEntity victim);

    /**
     * Called whenever a player dies.
     *
     * @param player - Player.
     * @param killer - Last damager.
     */
    void onDeath(@Nonnull GamePlayer player, @Nullable GameEntity killer);

    /**
     * Called whenever a player moves. Either keyboard or mouse.
     *
     * @param player - Player.
     * @param to     - Location.
     */
    void onMove(@Nonnull GamePlayer player, @Nonnull Location to);

    /**
     * Called whenever a player stands still for at least {@link SkinEffectManager#STANDING_STILL_THRESHOLD}.
     *
     * @param player - Player.
     */
    void onStandingStill(@Nonnull GamePlayer player);

    /**
     * Called whenever a player wins.
     *
     * @param player - Player.
     */
    void onWin(@Nonnull GamePlayer player);

}
