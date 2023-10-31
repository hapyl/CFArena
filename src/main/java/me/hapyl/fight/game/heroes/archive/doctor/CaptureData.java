package me.hapyl.fight.game.heroes.archive.doctor;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import javax.annotation.Nonnull;

public class CaptureData {

    private final GamePlayer player;
    private final LivingGameEntity captured;
    private final boolean flight;

    public CaptureData(GamePlayer gamePlayer, LivingGameEntity captured, boolean flight) {
        this.player = gamePlayer;
        this.captured = captured;
        this.flight = flight;
    }

    @Nonnull
    public GamePlayer getPlayer() {
        return player;
    }

    @Nonnull
    public LivingGameEntity getCaptured() {
        return captured;
    }

    public boolean isFlight() {
        return flight;
    }

    public void dismount() {
        final Location location = captured.getLocation();
        final Block block = location.getBlock();

        if (!block.getType().isAir() || !block.getRelative(BlockFace.UP).getType().isAir()) {
            player.sendMessage("&a%s was teleported to your since they would suffocate.", captured.getName());
            captured.teleport(player);
        }

        boolean solid = false;
        // check for solid ground
        for (double y = 0; y <= location.getY(); ++y) {
            if (!location.clone().subtract(0.0d, y, 0.0d).getBlock().getType().isAir()) {
                solid = true;
                break;
            }
        }

        if (!solid) {
            player.sendMessage("&a%s was teleported to your since they would fall into void.", captured.getName());
            captured.teleport(player);
        }

        captured.asPlayer(asPlayer -> asPlayer.setAllowFlight(flight));
    }

    public boolean check(LivingGameEntity target) {
        return !captured.equals(target) || captured.isDeadOrRespawning();
    }
}
