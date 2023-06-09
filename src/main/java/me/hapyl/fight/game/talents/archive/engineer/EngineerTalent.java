package me.hapyl.fight.game.talents.archive.engineer;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.archive.engineer.Engineer;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public abstract class EngineerTalent extends Talent {

    private final int ironCost;

    public EngineerTalent(@Nonnull String name, int ironCost) {
        super(name);

        this.ironCost = Math.max(1, ironCost);
    }

    public abstract Construct create(Player player, Location location);

    @Nonnull
    public abstract Response predicate(Player player, Location location);

    @Override
    public final Response execute(Player player) {
        final Engineer hero = Heroes.ENGINEER.getHero(Engineer.class);
        final Block targetBlock = player.getTargetBlockExact(7);
        final int playerIron = hero.getIron(player);

        if (targetBlock == null) {
            return Response.error("&cNo valid block in sight!");
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

        final Construct oldConstruct = hero.removeConstruct(player);

        if (oldConstruct != null) {
            oldConstruct.remove();
            Chat.sendMessage(player, "&aYour previous %s was removed!", getName());
        }

        hero.subtractIron(player, ironCost);
        hero.setConstruct(player, create(player, location).setCost(ironCost));

        return Response.OK;
    }
}