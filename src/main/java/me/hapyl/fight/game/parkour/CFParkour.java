package me.hapyl.fight.game.parkour;

import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.parkour.*;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.UUID;

public class CFParkour extends Parkour implements ParkourHandler {

    private ParkourLeaderboard leaderboard;
    private final ParkourDatabase database;

    public CFParkour(String name, int startX, int startY, int startZ, float yaw, float pitch, int finishX, int finishY, int finishZ) {
        super(
                name,
                new Location(Bukkit.getWorlds().get(0), startX, startY, startZ, yaw, pitch),
                new Location(Bukkit.getWorlds().get(0), finishX, finishY, finishZ)
        );

        this.database = new ParkourDatabase(this);
        this.setFormatter(new ParkourFormatter() {

            @Override
            public void sendCheckpointPassed(Data data) {
                final Player player = data.get();
                Chat.sendMessage(
                        player,
                        "&6&lCHECKPOINT! &eYou passed a checkpoint! &7(%s/%s)",
                        data.passedCheckpointsCount(), data.getParkour().getCheckpoints().size()
                );

                PlayerLib.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 2.0f);
            }

            public void sendResetTime(Player player, Parkour parkour) {
                Chat.sendMessage(player, "&eReset time for %s!", parkour.getName());
                PlayerLib.playSound(player, Sound.UI_BUTTON_CLICK, 1.0F);
            }

            public void sendParkourStarted(Player player, Parkour parkour) {
                Chat.sendMessage(player, "&aStarted %s!", parkour.getName());
                PlayerLib.playSound(player, Sound.UI_BUTTON_CLICK, 1.0F);
            }

            public void sendParkourFinished(Data data) {
            }

            public void sendParkourFailed(Data data, FailType type) {
                Player player = data.get();
                Chat.sendMessage(player, "&cParkour failed, &4%s&c!", type.getReason());
                PlayerLib.playSound(player, Sound.ENTITY_VILLAGER_NO, 1.0F);
            }

            public void sendHaventPassedCheckpoint(Data data) {
                Player player = data.get();
                Chat.sendMessage(player, "&cYou haven't passed any checkpoints yet!");
                PlayerLib.Sounds.ENDERMAN_TELEPORT.play(player, 0.0F);
            }

            public void sendQuit(Data data) {
                Chat.sendMessage(data.get(), "&cQuit %s!", data.getParkour().getName());
            }

            public void sendTickActionbar(Data data) {
                Chat.sendActionbar(
                        data.get(),
                        "&a&l%s: &b%ss",
                        data.getParkour().getName(), data.getTimePassedFormatted()
                );
            }

            public void sendCheckpointTeleport(Data data) {
                PlayerLib.Sounds.ENDERMAN_TELEPORT.play(data.get(), 1.25F);
            }

            public void sendErrorParkourNotStarted(Player player, Parkour parkour) {
                Chat.sendMessage(player, "&cYou must first start this parkour!");
                PlayerLib.Sounds.ENDERMAN_TELEPORT.play(player, 0.0F);
            }

            public void sendErrorMissedCheckpointCannotFinish(Data data) {
                Chat.sendMessage(data.get(), "&cYou missed &l%s&c checkpoints!", data.missedCheckpointsCount());
            }

            public void sendErrorMissedCheckpoint(Data data) {
                Player player = data.get();
                Chat.sendMessage(player, "&cYou missed a checkpoint!");
                PlayerLib.Sounds.ENDERMAN_TELEPORT.play(player, 0.0F);
            }
        });
    }

    protected void updateLeaderboardIfExists() {
        if (leaderboard != null) {
            leaderboard.update();
        }
    }

    @Nullable
    public ParkourLeaderboard getLeaderboard() {
        return leaderboard;
    }

    public void setLeaderboard(ParkourLeaderboard leaderboard) {
        this.leaderboard = leaderboard;
    }

    public CFParkour(String name, int startX, int startY, int startZ, int finishX, int finishY, int finishZ) {
        this(name, startX, startY, startZ, 0.0f, 0.0f, finishX, finishY, finishZ);
    }

    public void addCheckpoint(int x, int y, int z, float yaw, float pitch) {
        super.addCheckpoint(Bukkit.getWorlds().get(0), x, y, z, yaw, pitch);
    }

    public String parkourPath() {
        return this.getName().replace(" ", "_").toLowerCase();
    }

    public ParkourDatabase getDatabase() {
        return database;
    }

    @Nullable
    @Override
    public Response onStart(Player player, Data data) {
        return null;
    }

    @Nullable
    @Override
    public Response onFinish(Player player, Data data) {
        final UUID uuid = player.getUniqueId();

        final long completionTime = data.getCompletionTime();
        final long bestTime = database.getBestTime(uuid);
        final String completionTimeFormatted = data.getCompletionTimeFormatted();

        Chat.sendMessage(player, "&a&lFINISHED! &2You finished %s in %s!", getName(), completionTimeFormatted);

        if (bestTime == 0L || !database.hasCompleted(uuid)) {
            database.syncData(data);
        }
        else if (completionTime < bestTime) {
            database.syncData(data);
            Chat.sendMessage(
                    player,
                    "&b&lNEW RECORD! &aYou best time is now %s.",
                    completionTimeFormatted
            );
        }

        // TODO: 012, Mar 12, 2023 -> Test for world record

        return null;
    }

    @Nullable
    @Override
    public Response onFail(Player player, Data data, FailType failType) {
        return null;
    }

    @Nullable
    @Override
    public Response onCheckpoint(Player player, Data data, Position position, Type type) {
        return null;
    }
}
