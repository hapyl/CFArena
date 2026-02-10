package me.hapyl.fight.game.parkour;

import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.parkour.*;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.fight.Message;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.challenge.ChallengeType;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class CFParkour extends Parkour implements ParkourHandler {

    protected final ParkourDatabase database;
    private ParkourLeaderboard leaderboard;

    public CFParkour(@Nonnull Key key, @Nonnull String name, int startX, int startY, int startZ, float yaw, float pitch, int finishX, int finishY, int finishZ) {
        super(
                key,
                name,
                ParkourPosition.of(BukkitUtils.defWorld(), startX, startY, startZ, yaw, pitch),
                ParkourPosition.of(BukkitUtils.defWorld(), finishX, finishY, finishZ)
        );

        this.database = new ParkourDatabase(this);
        
        this.setFormatter(new ParkourFormatter() {

            @Override
            public void sendCheckpointPassed(@Nonnull ParkourData data) {
                final Player player = data.getPlayer();
                
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

            @Override
            public void sendResetTime(@Nonnull Player player, @Nonnull Parkour parkour) {
                Chat.sendMessage(player, "&eReset time for %s!".formatted(parkour.getName()));
                PlayerLib.playSound(player, Sound.UI_BUTTON_CLICK, 1.0F);
            }

            @Override
            public void sendParkourStarted(@Nonnull Player player, @Nonnull Parkour parkour) {
                Chat.sendMessage(player, "&aStarted %s!".formatted(parkour.getName()));
                PlayerLib.playSound(player, Sound.UI_BUTTON_CLICK, 1.0F);
            }

            @Override
            public void sendParkourFinished(@Nonnull ParkourData data) {
                // handled in onFinish
            }

            @Override
            public void sendParkourFailed(@Nonnull ParkourData data, @Nonnull FailType type) {
                final Player player = data.getPlayer();
                
                Chat.sendMessage(player, "&cParkour failed, &4%s&c!".formatted(type.reason()));
                PlayerLib.playSound(player, Sound.ENTITY_VILLAGER_NO, 1.0F);
            }

            @Override
            public void sendHaventPassedCheckpoint(@Nonnull ParkourData data) {
                final Player player = data.getPlayer();
                
                Chat.sendMessage(player, "&cYou haven't passed any checkpoints yet!");
                PlayerLib.endermanTeleport(player, 0.0F);
            }

            @Override
            public void sendQuit(@Nonnull ParkourData data) {
                Chat.sendMessage(data.getPlayer(), "&cQuit %s!".formatted(data.getParkour().getName()));
            }

            @Override
            public void sendTickActionbar(@Nonnull ParkourData data) {
                Chat.sendActionbar(
                        data.getPlayer(),
                        "&a&l%s: &b%ss".formatted(data.getParkour().getName(), data.getTimePassedFormatted())
                );
            }

            @Override
            public void sendCheckpointTeleport(@Nonnull ParkourData data) {
                PlayerLib.endermanTeleport(data.getPlayer(), 1.25F);
            }

            @Override
            public void sendErrorParkourNotStarted(@Nonnull Player player, @Nonnull Parkour parkour) {
                Chat.sendMessage(player, "&cYou must first start this parkour!");
                PlayerLib.endermanTeleport(player, 0.0F);
            }

            @Override
            public void sendErrorMissedCheckpointCannotFinish(@Nonnull ParkourData data) {
                Chat.sendMessage(data.getPlayer(), "&cYou missed &l%s&c checkpoints!".formatted(data.missedCheckpointsCount()));
            }

            @Override
            public void sendErrorMissedCheckpoint(@Nonnull ParkourData data) {
                final Player player = data.getPlayer();
                
                Chat.sendMessage(player, "&cYou missed a checkpoint!");
                PlayerLib.endermanTeleport(player, 0.0F);
            }
            
            @Override
            public void sendErrorCannotBreakParkourBlocks(@Nonnull Player player) {
                Message.error(player, "Cannot break parkour blocks!");
            }
        });
    }

    public CFParkour(@Nonnull Key key, @Nonnull String name, int startX, int startY, int startZ, int finishX, int finishY, int finishZ) {
        this(key, name, startX, startY, startZ, 0.0f, 0.0f, finishX, finishY, finishZ);
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

    public void addCheckpoint(int x, int y, int z, float yaw, float pitch) {
        super.addCheckpoint(Bukkit.getWorlds().getFirst(), x, y, z, yaw, pitch);
    }

    public String parkourPath() {
        return this.getName().replace(" ", "_").toLowerCase();
    }

    public ParkourDatabase getDatabase() {
        return database;
    }

    @Nullable
    @Override
    public Response onStart(Player player) {
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
    public Response onFinish(Player player, ParkourData data) {
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
    public Response onFail(Player player, @Nonnull ParkourData data, @Nonnull FailType failType) {
        return null;
    }

    @Nullable
    @Override
    public Response onCheckpoint(Player player, @Nonnull ParkourData data, @Nonnull ParkourPosition position, @Nonnull Type type) {
        return null;
    }

    public void reload() {
        removeWorldEntities();

        if (leaderboard != null) {
            leaderboard.update();
        }

        createWorldEntities();
    }
}
