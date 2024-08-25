package me.hapyl.fight.game.talents.moonwalker;

import me.hapyl.eterna.module.block.display.BDEngine;
import me.hapyl.eterna.module.block.display.DisplayData;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.heroes.moonwalker.MoonwalkerData;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.registry.Key;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import javax.annotation.Nonnull;

public class MoonPillarTalent extends Talent {

    @DisplayField(suffix = "blocks") private final double radius = 5.0d;
    @DisplayField private final short energy = 250;

    protected final DisplayData displayData = BDEngine.parse(
            "{Passengers:[{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone\",Properties:{}},transformation:[1.0000f,0.0000f,0.0000f,0.0000f,0.0000f,1.0000f,0.0000f,0.0000f,0.0000f,0.0000f,1.0000f,0.0000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone\",Properties:{}},transformation:[1.0000f,0.0000f,0.0000f,0.0000f,0.0000f,1.0000f,0.0000f,1.0000f,0.0000f,0.0000f,1.0000f,0.0000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone\",Properties:{}},transformation:[1.0000f,0.0000f,0.0000f,0.0000f,0.0000f,1.0000f,0.0000f,2.0000f,0.0000f,0.0000f,1.0000f,0.0000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_wall\",Properties:{up:\"true\"}},transformation:[-0.0000f,-1.3125f,0.0000f,1.1875f,1.0000f,-0.0000f,0.0000f,0.2500f,0.0000f,0.0000f,1.1250f,0.3438f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_wall\",Properties:{up:\"true\",north:\"low\",west:\"low\"}},transformation:[1.0000f,0.0000f,0.0000f,0.3750f,0.0000f,1.0000f,0.0000f,1.0000f,0.0000f,0.0000f,1.0000f,0.3750f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_wall\",Properties:{up:\"true\",north:\"low\",west:\"low\"}},transformation:[-0.0000f,0.0000f,-1.0000f,0.6250f,0.0000f,1.0000f,0.0000f,-0.5000f,1.0000f,0.0000f,-0.0000f,0.4375f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_wall\",Properties:{up:\"true\"}},transformation:[-0.0000f,-1.3125f,0.0000f,1.1875f,1.0000f,-0.0000f,0.0000f,1.7500f,0.0000f,0.0000f,1.1250f,0.3438f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_wall\",Properties:{up:\"true\"}},transformation:[0.0000f,1.3125f,-0.0000f,-0.1875f,1.0000f,-0.0000f,0.0000f,0.2500f,-0.0000f,-0.0000f,-1.1250f,0.6563f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_wall\",Properties:{up:\"true\",north:\"low\",west:\"low\"}},transformation:[-1.0000f,0.0000f,-0.0000f,0.6250f,0.0000f,1.0000f,0.0000f,1.0000f,0.0000f,0.0000f,-1.0000f,0.6250f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_wall\",Properties:{up:\"true\",north:\"low\",west:\"low\"}},transformation:[0.0000f,0.0000f,1.0000f,0.3750f,0.0000f,1.0000f,0.0000f,-0.5000f,-1.0000f,0.0000f,0.0000f,0.5625f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_stone_brick_wall\",Properties:{up:\"true\"}},transformation:[0.0000f,1.3125f,-0.0000f,-0.1875f,1.0000f,-0.0000f,0.0000f,1.7500f,-0.0000f,-0.0000f,-1.1250f,0.6563f,0.0000f,0.0000f,0.0000f,1.0000f]}]}"
    );

    public MoonPillarTalent(Key key) {
        super(key, "Moonlit Pillar");

        setDescription("""
                Erect a Moonlit Pillar in front of you to create a &eMoonlit Zone&7.
                """
        );

        setType(TalentType.ENHANCE);
        setItem(Material.BONE);
        setDurationSec(10);
        setCooldownSec(30);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final Block block = getTargetBlock(player);

        if (block == null) {
            return Response.error("No valid target block!");
        }

        final Location location = block.getLocation().add(0.5d, 0.0d, 0.5d);

        if (!canFit(location)) {
            return Response.error("Cannot fit the pillar!");
        }

        final MoonwalkerData data = HeroRegistry.MOONWALKER.getPlayerData(player);

        data.addZone(new MoonPillarZone(this, player, location, radius, energy));

        return Response.OK;
    }

    private boolean canFit(Location location) {
        final Block block = location.getBlock();

        return block.isEmpty()
                && block.getRelative(BlockFace.UP).isEmpty()
                && block.getRelative(BlockFace.UP, 2).isEmpty();
    }

    private Block getTargetBlock(GamePlayer player) {
        final Block block = player.getTargetBlockExact(7);

        return block == null ? null : block.getRelative(BlockFace.UP);
    }

}
