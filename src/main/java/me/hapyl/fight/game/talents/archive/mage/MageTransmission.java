package me.hapyl.fight.game.talents.archive.mage;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.archive.techie.Talent;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;

public class MageTransmission extends Talent {

    @DisplayField(suffix = "blocks") private final double maxDistance = 30.0d;

    public MageTransmission() {
        super(
                "Transmission",
                "Instantly teleport to your target block, but lose the ability to move for a short duration."
        );

        setType(Type.MOVEMENT);
        setItem(Material.ENDER_PEARL);
        setCooldownSec(30);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final Location location = getTargetLocation(player);

        if (location == null) {
            return Response.error("No valid block in sight!");
        }

        location.setYaw(player.getLocation().getYaw());
        location.setPitch(player.getLocation().getPitch());

        if (location.distance(player.getLocation()) >= maxDistance) {
            return Response.error("Too far away!");
        }

        if (!location.getBlock().getType().isAir() || location.getBlock().getRelative(BlockFace.UP).getType().isOccluding()) {
            return Response.error("Location is not safe!");
        }

        player.teleport(location);
        player.addPotionEffect(PotionEffectType.SLOW, 20, 10);
        player.playSound(Sound.ENTITY_ENDERMAN_TELEPORT, 0.65f);

        if (location.getWorld() != null) {
            location.getWorld().playEffect(location, Effect.ENDER_SIGNAL, 0);
        }

        return Response.OK;
    }

    private Location getTargetLocation(GamePlayer player) {
        final Block block = player.getTargetBlockExact(30);

        if (block == null) {
            return null;
        }

        return block.getRelative(BlockFace.UP).getLocation();
    }


}
