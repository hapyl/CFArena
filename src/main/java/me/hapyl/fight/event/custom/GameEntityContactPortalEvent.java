package me.hapyl.fight.event.custom;

import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.block.Block;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerTeleportEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Called upon an {@link LivingGameEntity} or a {@link me.hapyl.fight.game.entity.GamePlayer} touching a portal block.
 * <p>
 * This event has a <code>20</code> ticks cooldown.
 *
 * @see PortalType
 */
public class GameEntityContactPortalEvent extends GameEntityEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final PortalType type;

    public GameEntityContactPortalEvent(@Nonnull LivingGameEntity entity, @Nonnull PortalType type) {
        super(entity);

        this.type = type;
    }

    @Nonnull
    public PortalType getType() {
        return type;
    }

    @Nonnull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    @Nonnull
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    public enum PortalType {
        /**
         * Nether portal block.
         */
        NETHER,
        /**
         * End portal block.
         */
        END,
        /**
         * End gateway portal block.
         */
        END_GATEWAY;

        @Nullable
        public static PortalType fromBlock(@Nonnull Block block) {
            return switch (block.getType()) {
                case NETHER_PORTAL -> NETHER;
                case END_PORTAL -> END;
                case END_GATEWAY -> END_GATEWAY;
                default -> null;
            };
        }

        @Nullable
        public static PortalType fromCause(@Nonnull PlayerTeleportEvent.TeleportCause cause) {
            return switch (cause) {
                case NETHER_PORTAL -> NETHER;
                case END_PORTAL -> END;
                case END_GATEWAY -> END_GATEWAY;
                default -> null;
            };
        }
    }
}
