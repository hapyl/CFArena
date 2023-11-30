package me.hapyl.fight.game.gamemode;

import me.hapyl.fight.CF;
import me.hapyl.fight.game.EntityState;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.setting.Settings;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.scoreboard.Scoreboarder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.text.SimpleDateFormat;
import java.util.stream.Collectors;

public abstract class CFGameMode {

    private final String name;
    private final int timeLimit; // in seconds

    private Material material;
    private String description;
    private int playerRequirements;
    private boolean allowRespawn;
    private int respawnTime;

    public CFGameMode(String name, int timeLimitSec) {
        this.name = name;
        this.timeLimit = timeLimitSec;
        this.description = "";
        this.material = Material.BEDROCK;
        this.playerRequirements = 2;
        this.allowRespawn = false;
    }

    public void setRespawnTime(int respawnTime) {
        this.respawnTime = respawnTime;
    }

    public int getRespawnTime() {
        return respawnTime;
    }

    public void setAllowRespawn(boolean allowRespawn) {
        this.allowRespawn = allowRespawn;
    }

    public boolean isAllowRespawn() {
        return allowRespawn;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public Material getMaterial() {
        return material;
    }

    public void setDescription(String info) {
        this.description = info;
    }

    public String getDescription() {
        return description;
    }

    public void setPlayerRequirements(int playerRequirements) {
        this.playerRequirements = playerRequirements;
    }

    public int getPlayerRequirements() {
        return playerRequirements;
    }

    // FIXME: 024, Feb 24, 2023 -> Might need to check for teams, not players
    public boolean isPlayerRequirementsMet() {
        return Bukkit.getOnlinePlayers()
                .stream()
                .filter(player -> !Settings.SPECTATE.isEnabled(player))
                .collect(Collectors.toSet())
                .size() >= getPlayerRequirements();
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    @Nonnull
    public String getTimeLimitFormatted() {
        return new SimpleDateFormat("mm:ss").format(timeLimit * 1000);
    }

    public String getName() {
        return name;
    }

    public abstract boolean testWinCondition(@Nonnull GameInstance instance);

    public void formatScoreboard(Scoreboarder builder, GameInstance instance, GamePlayer player) {
    }

    public void onDeath(@Nonnull GameInstance instance, @Nonnull GamePlayer player) {
    }

    public void tick(@Nonnull GameInstance instance, int tick) {
    }

    /**
     * Called upon player leaving while the game is in progress.
     *
     * @param instance - Game Instance player left from.
     * @param player   - Player who left.
     */
    public void onLeave(@Nonnull GameInstance instance, @Nonnull Player player) {
        final GamePlayer gamePlayer = CF.getPlayer(player);

        if (gamePlayer == null) {
            return;
        }

        Chat.broadcast("");
        Chat.broadcast("&c%s left while fighting and was removed from the game!", player.getName());
        Chat.broadcast("");

        gamePlayer.setState(EntityState.DEAD);
        instance.checkWinCondition();
    }

    /**
     * Called upon player joining while the game is in progress.
     *
     * @param instance - Game Instance player joined to.
     * @param player   - Player, who left.
     */
    public void onJoin(@Nonnull GameInstance instance, @Nonnull Player player) {
        final PlayerProfile profile = PlayerProfile.getProfile(player);

        if (profile == null) {
            return;
        }

        final GamePlayer gamePlayer = profile.getOrCreateGamePlayer();

        gamePlayer.setSpectator(true);
        Chat.broadcast("&a%s joined the game as a spectator!", player.getName());

        GameTask.runLater(() -> {
            gamePlayer.sendSubtitle("&aYou are currently spectating!", 0, 20, 0);
            gamePlayer.teleport(instance.getRandomPlayerLocationOrMapLocationIfThereAreNoPlayers());
        }, 1);
    }

    public void onStart(@Nonnull GameInstance instance) {
    }

    /**
     * Use this to calculate winners if not default.
     *
     * @param instance - game instance.
     * @return false to mark all living players as winners.
     */
    public boolean onStop(@Nonnull GameInstance instance) {
        return false;
    }

    public boolean shouldRespawn(@Nonnull GamePlayer gamePlayer) {
        return true;
    }
}
