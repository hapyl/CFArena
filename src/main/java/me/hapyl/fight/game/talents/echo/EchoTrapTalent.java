package me.hapyl.fight.game.talents.echo;

import com.google.common.collect.Sets;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EchoTrapTalent extends EchoTalent {

    private final Map<GamePlayer, Set<EchoTrap>> echoTraps = new HashMap<>();

    @DisplayField private final short maxTraps = 3;

    public EchoTrapTalent(@Nonnull Key key) {
        super(key, "Echo Trap");

        setDescription("""
                place an invisible trap at the target block.
                &8&o;;The trap can be placed on any surface, as long as it's a valid block.
                
                The trap will automatically activate whenever an enemy steps in its line of sight, dealing damage and applying %s to hit enemies.
                """.formatted(Named.DECAY)
        );

        setItem(Material.NETHER_BRICK_FENCE);
        setType(TalentType.IMPAIR);

        setCooldownSec(6);
    }

    @Nonnull
    @Override
    public Response executeEcho(@Nonnull GamePlayer player) {
        final List<Block> lastTwoBlocks = player.getPlayer().getLastTwoTargetBlocks(null, 10);

        if (lastTwoBlocks.size() != 2) {
            return Response.error("Nowhere to place the trap!");
        }

        final Block first = lastTwoBlocks.getFirst();
        final Block last = lastTwoBlocks.getLast();

        if (first.isEmpty() && last.isEmpty()) {
            return Response.error("Nowhere to anchor the trap!");
        }

        final BlockFace face = last.getFace(first);

        if (face == null) {
            return Response.error("Cannot anchor here!");
        }

        final Set<EchoTrap> playerTraps = echoTraps.computeIfAbsent(player, _player -> Sets.newHashSet());

        if (playerTraps.size() >= maxTraps) {
            return Response.error("Already placed the maximum number of traps!");
        }

        return Response.AWAIT;
    }
}
