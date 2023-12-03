package me.hapyl.fight.game.talents.storage.healer;

import com.google.common.collect.Maps;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.talents.Talent;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import java.util.Map;

public class ReviveTotem extends Talent {

    private final Map<Player, ArmorStand> playerCatalysts;

    public ReviveTotem() {
        super("Revive Catalyst", "Place somewhere hidden for later use to revive yourself.");

        setItem(Material.TOTEM_OF_UNDYING); // fixme -> this might actually trigger totem, needs testing

        this.playerCatalysts = Maps.newHashMap();
    }

    // Death is when healer is actually dead and cannot revive.
    @Override
    public void onDeath(Player player) {
        final ArmorStand catalyst = getCatalyst(player);
        if (catalyst != null) {
            catalyst.remove();
        }
    }

    public ArmorStand getCatalyst(Player player) {
        return this.playerCatalysts.get(player);
    }

    @Override
    public Response execute(Player player) {
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
