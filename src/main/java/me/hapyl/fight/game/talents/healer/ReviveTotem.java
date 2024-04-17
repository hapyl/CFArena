package me.hapyl.fight.game.talents.healer;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.techie.Talent;
import me.hapyl.fight.util.collection.player.PlayerMap;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;

import javax.annotation.Nonnull;

public class ReviveTotem extends Talent {

    private final PlayerMap<ArmorStand> playerCatalysts;

    public ReviveTotem() {
        super("Revive Catalyst", "Place somewhere hidden for later use to revive yourself.");

        setItem(Material.TOTEM_OF_UNDYING); // fixme -> this might actually trigger totem, needs testing

        this.playerCatalysts = PlayerMap.newMap();
    }

    // Death is when a healer is actually dead and cannot revive.
    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        final ArmorStand catalyst = getCatalyst(player);
        if (catalyst != null) {
            catalyst.remove();
        }
    }

    public ArmorStand getCatalyst(GamePlayer player) {
        return this.playerCatalysts.get(player);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final Block targetBlock = player.getTargetBlockExact(5);

        if (targetBlock == null) {
            return Response.error("Not looking at block.");
        }

        final Block relativeBlock = targetBlock.getRelative(BlockFace.UP);
        if (!relativeBlock.getType().isAir()) {
            return Response.error("Cannot fit catalyst!");
        }

        return null;
    }
}
