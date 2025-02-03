package me.hapyl.fight.game.parkour.snake;

import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.fight.CF;
import me.hapyl.fight.event.ProfileInitializationEvent;
import me.hapyl.fight.game.parkour.CFParkour;
import me.hapyl.fight.game.parkour.ParkourLeaderboard;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.util.Collection;

public class SnakeParkour extends CFParkour implements Listener {

    private final Snake snake;

    public SnakeParkour() {
        super(
                "Snake Parkour",
                27, 65, -8, 180, 0,
                22, 65, -22
        );

        addCheckpoint(30, 63, -15, 90, 0);
        addCheckpoint(22, 62, -10, -180, 0);
        addCheckpoint(19, 62, -17, -90, 0);
        addCheckpoint(22, 65, -19, -180, 0);

        setQuitLocation(BukkitUtils.defLocation(25, 65, -7, -180, 0));

        setLeaderboard(new ParkourLeaderboard(this, 22.5, 66, -7.5));

        snake = Snake.builder()
                .setBlock(Material.CHISELED_STONE_BRICKS)
                .setLength(8)
                .next(27, 64, -10)
                .next(27, 64, -11)
                .next(27, 64, -12)
                .next(28, 64, -12)
                .next(29, 64, -12)
                .next(29, 63, -12)
                .next(29, 62, -12)
                .next(30, 62, -12)
                .next(30, 62, -13)
                .next(30, 62, -14)
                .next(30, 62, -15)
                .next(29, 62, -15)
                .next(28, 62, -15)
                .next(28, 63, -15)
                .next(27, 63, -15)
                .next(27, 64, -15)
                .next(26, 64, -15)
                .next(25, 64, -15)
                .next(25, 64, -14)
                .next(25, 64, -13)
                .next(25, 64, -12)
                .next(25, 65, -12)
                .next(25, 65, -11)
                .next(25, 66, -11)
                .next(25, 66, -10)
                .next(25, 65, -10)
                .next(25, 64, -10)
                .next(25, 63, -10)
                .next(25, 62, -10)
                .next(25, 61, -10)
                .next(24, 61, -10)
                .next(23, 61, -10)
                .next(22, 61, -10)
                .next(22, 61, -11)
                .next(22, 61, -12)
                .next(22, 61, -13)
                .next(23, 61, -13)
                .next(24, 61, -13)
                .next(24, 61, -14)
                .next(24, 61, -15)
                .next(23, 61, -15)
                .next(23, 60, -15)
                .next(22, 60, -15)
                .next(21, 60, -15)
                .next(20, 60, -15)
                .next(19, 60, -15)
                .next(19, 61, -15)
                .next(19, 61, -16)
                .next(19, 61, -17)
                .next(20, 61, -17)
                .next(21, 61, -17)
                .next(21, 62, -17)
                .next(22, 62, -17)
                .next(22, 63, -17)
                .next(23, 63, -17)
                .next(24, 63, -17)
                .next(25, 63, -17)
                .next(25, 62, -17)
                .next(25, 61, -17)
                .next(25, 61, -18)
                .next(25, 61, -19)
                .next(25, 61, -20)
                .next(26, 61, -20)
                .next(27, 61, -20)
                .next(27, 61, -19)
                .next(27, 61, -18)
                .next(27, 61, -17)
                .next(28, 61, -17)
                .next(28, 62, -17)
                .next(29, 62, -17)
                .next(29, 63, -17)
                .next(29, 63, -18)
                .next(29, 64, -18)
                .next(29, 64, -19)
                .next(29, 65, -19)
                .next(28, 65, -19)
                .next(28, 66, -19)
                .next(27, 66, -19)
                .next(27, 67, -19)
                .next(26, 67, -19)
                .next(25, 67, -19)
                .next(25, 66, -19)
                .next(25, 65, -19)
                .next(25, 64, -19)
                .next(24, 64, -19)
                .next(23, 64, -19)
                .next(22, 64, -19)
                .end(22, 64, -20);

        snake.createEntities();
        snake.start();

        CF.registerEvents(this);
    }

    @EventHandler
    public void handlePlayerJoinEvent(ProfileInitializationEvent ev) {
        final Player player = ev.getPlayer();

        snake.entities.forEach(entity -> entity.show(player));
    }

    public void showSnakeEntities() {
        final Collection<? extends Player> players = Bukkit.getOnlinePlayers();

        snake.entities.forEach(entity -> {
            players.forEach(entity::hide);
            players.forEach(entity::show);
        });
    }

    @Nonnull
    public Snake getSnake() {
        return snake;
    }
}
