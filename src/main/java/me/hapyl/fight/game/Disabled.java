package me.hapyl.fight.game;

import me.hapyl.fight.Message;
import me.hapyl.fight.game.cosmetic.Cosmetic;
import me.hapyl.fight.game.entity.SoundEffect;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.skin.Skin;
import me.hapyl.fight.game.talents.InsteadOfNull;
import me.hapyl.fight.gui.styled.StyledGUI;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

/**
 * Indicates that the implementing class feature is to be disabled.
 * <p>Supported Classes:</p>
 * <ul>
 *     <li>{@link Hero}
 *     <li>{@link StyledGUI}
 *     <li>{@link Cosmetic}
 *     <li>{@link Skin}
 * </ul>
 * <p>
 * Implementation of other classes implementing this interface is unknown.
 */
public interface Disabled {
    
    /**
     * An optional reason to be displayed whenever a player tries to use the disabled item.
     */
    @Nonnull
    @InsteadOfNull("empty string")
    default String disableReason() {
        return "";
    }
    
    /**
     * Sends the default error message to the given player.
     * <pre>{@code
     *  This 'TYPE' is currently disabled, sorry! 'REASON'
     * }</pre>
     *
     * @param player - The player.
     * @param type   - The 'type' to be displayed.
     */
    default void errorMessage(@Nonnull Player player, @Nonnull String type) {
        Message.error(player, "This %s is currently disabled, sorry! {%s}".formatted(type, disableReason()));
        Message.sound(player, SoundEffect.FAILURE);
    }
}
