package me.hapyl.fight.game.type.types;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.scoreboard.Scoreboarder;
import me.hapyl.eterna.module.util.Compute;
import me.hapyl.eterna.module.util.collection.LinkedValue2IntegerReverseMap;
import me.hapyl.fight.CF;
import me.hapyl.fight.game.EntityState;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.Outline;
import me.hapyl.fight.game.type.EnumGameType;
import me.hapyl.fight.game.type.GameType;
import org.bukkit.Material;

import javax.annotation.Nonnull;
import java.util.Map;

public class FrenzyMode extends GameType {

    private final int maxLives = 9;
    private final Map<GamePlayer, Integer> playerLivesMap;

    public FrenzyMode(@Nonnull EnumGameType handle) {
        super(handle, "Frenzy", 1200);

        setDescription("""
                A free for all with limited lives!
                
                Max Lives: &a%s
                """.formatted(maxLives));

        setMaterial(Material.RED_DYE);
        setPlayerRequirements(2);
        setAllowRespawn(true);
        setRespawnTime(30);

        playerLivesMap = Maps.newHashMap();
    }

    @Override
    public boolean shouldRespawn(@Nonnull GamePlayer gamePlayer) {
        return playerLivesMap.containsKey(gamePlayer);
    }

    @Override
    public void formatScoreboard(@Nonnull Scoreboarder builder, @Nonnull GameInstance instance, @Nonnull GamePlayer player) {
        final int playerLives = playerLivesMap.getOrDefault(player, -1);
        final LinkedValue2IntegerReverseMap<GamePlayer> reverse = LinkedValue2IntegerReverseMap.of(playerLivesMap);

        builder.addLine("&c&l%s &8(&c&l%s &c❤&8)".formatted(nameSmallCaps(), playerLives));

        reverse.forEach(
                5, (index, gamePlayer, live) -> {
                    if (gamePlayer == null) {
                        builder.addLine(" &f#&l%s &8...".formatted(index + 1));
                    }
                    else {
                        builder.addLine(gamePlayer.formatTeamNameScoreboardPosition(index + 1, " &c&l%s &c❤".formatted(live)));
                    }
                }
        );
    }

    @Override
    public void onStart(@Nonnull GameInstance instance) {
        playerLivesMap.clear();

        CF.getPlayers().forEach(player -> playerLivesMap.put(player, maxLives));
    }

    @Override
    public void onDeath(@Nonnull GameInstance instance, @Nonnull GamePlayer player) {
        // If not in a map it means they have died.
        if (!playerLivesMap.containsKey(player)) {
            return;
        }

        final int remainingLives = playerLivesMap.compute(player, Compute.intSubtract());

        switch (remainingLives) {
            case 0 -> {
                playerLivesMap.remove(player);
                player.sendMessage("&7[&4☠&7] &4It was nice knowing you.");
                player.setState(EntityState.DEAD);
            }
            case 1 -> {
                player.sendMessage("&7[&4☠&7] &cThis is your final life, don't waste it!");
                player.setOutline(Outline.RED);
            }
            default -> {
                player.sendMessage("&7[&4☠&7] &a%s lives remaining!".formatted(remainingLives));
                player.setOutline(Outline.CLEAR);
            }
        }
    }

    @Override
    public boolean onStop(@Nonnull GameInstance instance) {
        for (GamePlayer gamePlayer : playerLivesMap.keySet()) {
            instance.getGameResult().getWinners().add(gamePlayer);
            return true;
        }

        return true;
    }

    @Override
    public boolean testWinCondition(@Nonnull GameInstance instance) {
        return playerLivesMap.size() == 1;
    }
}
