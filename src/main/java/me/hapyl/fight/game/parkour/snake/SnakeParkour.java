package me.hapyl.fight.game.parkour.snake;

import me.hapyl.fight.game.parkour.CFParkour;
import me.hapyl.fight.game.parkour.ParkourLeaderboard;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Material;

public class SnakeParkour extends CFParkour {

    private final Snake snake;

    public SnakeParkour() {
        super("Snake Parkour", 27, 65, -9, -180, 0, 22, 65, -22);

        addCheckpoint(26, 67, -19, 90, 0);
        addCheckpoint(23, 67, -14, 0, 0);

        setQuitLocation(BukkitUtils.defLocation(25, 65, -7, -180, 0));

        setLeaderboard(new ParkourLeaderboard(this, 22.5, 66, -8.5));

        snake = Snake.builder()
                .setLength(8)
                .setBlock(Material.CHISELED_STONE_BRICKS)
                .next(27, 64, -12)
                .next(27, 64, -13)
                .next(28, 64, -13)
                .next(29, 64, -13)
                .next(29, 65, -13)
                .next(29, 65, -14)
                .next(29, 65, -15)
                .next(29, 66, -15)
                .next(28, 66, -15)
                .next(27, 66, -15)
                .next(27, 67, -15)
                .next(27, 67, -16)
                .next(27, 67, -17)
                .next(27, 66, -17)
                .next(27, 65, -17)
                .next(27, 65, -18)
                .next(27, 65, -19)
                .next(27, 66, -19)
                .next(26, 66, -19)
                .next(24, 66, -19)
                .next(23, 66, -19)
                .next(23, 65, -19)
                .next(23, 64, -19)
                .next(23, 64, -18)
                .next(23, 64, -17)
                .next(23, 65, -17)
                .next(23, 66, -17)
                .next(23, 66, -16)
                .next(23, 66, -14)
                .next(23, 66, -13)
                .next(23, 66, -12)
                .next(22, 66, -12)
                .next(21, 66, -12)
                .next(20, 66, -12)
                .next(20, 66, -13)
                .next(20, 65, -13)
                .next(20, 64, -13)
                .next(20, 64, -14)
                .next(20, 64, -15)
                .next(20, 65, -15)
                .next(20, 65, -16)
                .next(20, 65, -17)
                .next(20, 65, -18)
                .next(20, 65, -19)
                .end(20, 65, -20);

        snake.createEntities();
        snake.start();
    }

    public Snake getSnake() {
        return snake;
    }
}
