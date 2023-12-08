package me.hapyl.fight.game.talents.archive.tamer;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;

public class TamingTheEarth extends Talent implements TamerTalent {

    @DisplayField private final double radius = 5.0d;

    public TamingTheEarth() {
        super("Taming the Earth");

        setDescription("""
                Lower nearby enemies below the baseboard, &eimpairing&7 their movement.
                """);

        setType(Type.IMPAIR);
        setItem(Material.PISTON);
        setDuration(30);
        setCooldownSec(20);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final int duration = getDuration(player);

        Collect.nearbyEntities(player.getLocation(), radius).forEach(entity -> {
            if (player.isSelfOrTeammate(entity)) {
                return;
            }

            final Location location = entity.getLocationAnchored();
            final Location locationBelow = location.subtract(0, 1, 0);
            final Block blockBelowBelow = locationBelow.getBlock().getRelative(BlockFace.DOWN);

            if (!blockBelowBelow.isEmpty()) {
                entity.teleport(locationBelow);
            }

            entity.addPotionEffect(PotionEffectType.SLOW, duration, 255);

            // Mojang, why?
            if (entity instanceof GamePlayer) {
                entity.addPotionEffect(PotionEffectType.LEVITATION, duration, 255);
            }

            // Fx
            entity.playWorldSound(Sound.BLOCK_PISTON_EXTEND, 0.0f);
        });

        return Response.OK;
    }
}