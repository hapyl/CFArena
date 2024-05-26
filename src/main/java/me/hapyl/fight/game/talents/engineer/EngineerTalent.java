package me.hapyl.fight.game.talents.engineer;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.engineer.Engineer;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.util.Blocks;
import me.hapyl.spigotutils.module.block.display.BlockStudioParser;
import me.hapyl.spigotutils.module.block.display.DisplayData;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import javax.annotation.Nonnull;

public abstract class EngineerTalent extends Talent {

    private final int ironCost;
    private int upgradeCost = 2;
    private final DisplayData[] displayData = new DisplayData[Construct.MAX_LEVEL];

    protected double yOffset = 2.0d;

    public EngineerTalent(@Nonnull String name, int ironCost) {
        super(name);

        this.ironCost = Math.max(1, ironCost);
    }

    public void setUpgradeCost(int upgradeCost) {
        this.upgradeCost = upgradeCost;
    }

    public int getUpgradeCost() {
        return upgradeCost;
    }

    @Nonnull
    public abstract Construct create(@Nonnull GamePlayer player, @Nonnull Location location);

    @Nonnull
    public Response predicate(@Nonnull GamePlayer player, @Nonnull Location location) {
        return Response.OK;
    }

    @Override
    public final Response execute(@Nonnull GamePlayer player) {
        final Engineer hero = Heroes.ENGINEER.getHero(Engineer.class);
        final Block targetBlock = player.getTargetBlockExact(7);
        final int playerIron = hero.getIron(player);

        if (targetBlock == null) {
            return Response.error("&cNo valid block in sight!");
        }

        if (!Blocks.isValid(targetBlock)) {
            return Response.error("Cannot place turret on this block!");
        }

        final Block block = targetBlock.getRelative(BlockFace.UP);
        final Location location = block.getLocation().add(0.5d, 0.0d, 0.5d);

        if (block.getType().isOccluding()) {
            return Response.error("Cannot fit %s!", getName());
        }

        if (playerIron < ironCost) {
            return Response.error("Not enough iron!");
        }

        final Response response = predicate(player, location);

        if (response.isError()) {
            return response;
        }

        Construct construct = hero.getConstruct(player);
        if (construct != null) {
            return Response.error("%s already exists!", construct.getName());
        }

        hero.removeConstruct(player);
        hero.subtractIron(player, ironCost);
        hero.setConstruct(player, create(player, location).setCost(ironCost));

        return Response.OK;
    }

    @Nonnull
    public DisplayData getDisplayData(int level) {
        DisplayData data = displayData[level];

        // default to first level entity
        if (data == null) {
            data = displayData[0];
        }

        // if no default level entity you are dumb
        if (data == null) {
            throw new IllegalStateException("Construct must have at least one display data! %s has none!".formatted(getName()));
        }

        return data;
    }

    protected void setDisplayData(int level, @Nonnull DisplayData data) {
        displayData[level] = data;
    }

    protected void setDisplayData(int level, @Nonnull String data) {
        setDisplayData(level, BlockStudioParser.parse(data));
    }

}
