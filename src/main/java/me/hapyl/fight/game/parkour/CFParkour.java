package me.hapyl.fight.game.parkour;

import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.parkour.*;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.challenge.ChallengeType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class CFParkour extends Parkour implements ParkourHandler {

    private ParkourLeaderboard leaderboard;
    protected final ParkourDatabase database;

    public CFParkour(String name, int startX, int startY, int startZ, float yaw, float pitch, int finishX, int finishY, int finishZ) {
        super(
                name,
                new Location(Bukkit.getWorlds().get(0), startX, startY, startZ, yaw, pitch),
                new Location(Bukkit.getWorlds().get(0), finishX, finishY, finishZ)
        );

        this.database = new ParkourDatabase(this);
        this.setFormatter(new ParkourFormatter() {

            @Override
            public void sendCheckpointPassed(@Nonnull Data data) {
                final Player player = data.get();
                Chat.sendMessage(
                        player,
                        "&6&lCHECKPOINT! &eYou passed a checkpoint! &7(%s/%s) &8(%ss)".formatted(
                                data.passedCheckpointsCount(),
                                data.getParkour().getCheckpoints().size(),
                                data.getTimePassedFormatted()
                        )
                );

                PlayerLib.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 2.0f);
            }

            public void sendResetTime(@Nonnull Player player, @Nonnull Parkour parkour) {
                Chat.sendMessage(player, "&eReset time for %s!".formatted(parkour.getName()));
                PlayerLib.playSound(player, Sound.UI_BUTTON_CLICK, 1.0F);
            }

            public void sendParkourStarted(@Nonnull Player player, @Nonnull Parkour parkour) {
                Chat.sendMessage(player, "&aStarted %s!".formatted(parkour.getName()));
                PlayerLib.playSound(player, Sound.UI_BUTTON_CLICK, 1.0F);
            }

            public void sendParkourFinished(@Nonnull Data data) {
                // handled in onFinish
            }

            public void sendParkourFailed(@Nonnull Data data, @Nonnull FailType type) {
                Player player = data.get();
                Chat.sendMessage(player, "&cParkour failed, &4%s&c!".formatted(type.getReason()));
                PlayerLib.playSound(player, Sound.ENTITY_VILLAGER_NO, 1.0F);
            }

            public void sendHaventPassedCheckpoint(@Nonnull Data data) {
                Player player = data.get();
                Chat.sendMessage(player, "&cYou haven't passed any checkpoints yet!");
                PlayerLib.endermanTeleport(player, 0.0F);
            }

            public void sendQuit(@Nonnull Data data) {
                Chat.sendMessage(data.get(), "&cQuit %s!".formatted(data.getParkour().getName()));
            }

            public void sendTickActionbar(@Nonnull Data data) {
                Chat.sendActionbar(
                        data.get(),
                        "&a&l%s: &b%ss".formatted(data.getParkour().getName(), data.getTimePassedFormatted())
                );
            }

            public void sendCheckpointTeleport(@Nonnull Data data) {
                PlayerLib.endermanTeleport(data.get(), 1.25F);
            }

            public void sendErrorParkourNotStarted(@Nonnull Player player, @Nonnull Parkour parkour) {
                Chat.sendMessage(player, "&cYou must first start this parkour!");
                PlayerLib.endermanTeleport(player, 0.0F);
            }

            public void sendErrorMissedCheckpointCannotFinish(Data data) {
                Chat.sendMessage(data.get(), "&cYou missed &l%s&c checkpoints!".formatted(data.missedCheckpointsCount()));
            }

            public void sendErrorMissedCheckpoint(Data data) {
                Player player = data.get();
                Chat.sendMessage(player, "&cYou missed a checkpoint!");
                PlayerLib.endermanTeleport(player, 0.0F);
            }
        });
    }

    public void updateLeaderboardIfExists() {
        if (leaderboard != null) {
            leaderboard.update();
        }
    }

    public void onDamage(Player player, EntityDamageEvent.DamageCause cause) {
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
        if (Manager.current().isGameInProgress()) {
            Chat.sendMessage(player, "&cCannot start parkour while a game is in progress!");
            player.teleport(getQuitLocation());
            return Response.CANCEL;
        }

        if (!Bukkit.getOnlineMode()) {
            Chat.sendMessage(player, "&cParkour is unavailable in offline mode!");
            Chat.sendMessage(player, "&cSet &e'online-mode'&c to &etrue&c in your &eserver.properties&c!");
            player.teleport(getQuitLocation());
            return Response.CANCEL;
        }

        return null;
    }

    @Nullable
    @Override
    public Response onFinish(Player player, Data data) {
        final UUID uuid = player.getUniqueId();

        final long completionTime = data.getCompletionTime();
        final long bestTime = database.getBestTime(uuid);
        final String completionTimeFormatted = data.getCompletionTimeFormatted();

        Chat.sendMessage(player, "&a&lFINISHED! &2You finished %s in %s!".formatted(getName(), completionTimeFormatted));

        if (bestTime == 0L || !database.hasCompleted(uuid)) {
            database.syncData(data);
        }
        else if (completionTime < bestTime) {
            database.syncData(data);
            Chat.sendMessage(player, "&b&lNEW RECORD! &aYou best time is now %s.".formatted(completionTimeFormatted));
        }

        if (leaderboard != null) {
            final long worldRecord = leaderboard.getWorldRecord();

            if (completionTime < worldRecord) {
                Chat.broadcast("&b&lNEW WORLD RECORD! &a%s set a new world record of %s for %s!".formatted(
                        player.getName(),
                        completionTimeFormatted,
                        getName()
                ));
            }
        }

        // Progress bond
        ChallengeType.COMPLETE_PARKOUR.progress(player);

        return null;
    }

    @Nullable
    @Override
    public Response onFail(Player player, Data data, FailType failType) {
        return null;
    }

    @Nullable
    @Override
    public Response onCheckpoint(Player player, Data data, ParkourPosition position, Type type) {
        return null;
    }
}
