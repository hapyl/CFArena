package me.hapyl.fight.mini.lampgame;

import com.google.common.collect.Maps;
import me.hapyl.fight.game.achievement.Achievements;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.hologram.Hologram;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import me.hapyl.spigotutils.module.util.Runnables;
import me.hapyl.spigotutils.module.util.ThreadRandom;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Lightable;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Map;

public class Data {

    private final BlockFace[] relativeFaces = { BlockFace.UP, BlockFace.DOWN, BlockFace.WEST, BlockFace.EAST };
    private final Player player;
    private final Map<Block, Boolean> board;
    private final Hologram hologram;

    private int clicks;
    private long startedAt;

    public Data(Player player) {
        this.player = player;
        this.board = Maps.newHashMap();

        for (Block block : LampGame.BOUNDING_BOX.getBlocks()) {
            board.put(block, false);
        }

        this.hologram = new Hologram().create(BukkitUtils.defLocation(0, 64, -16)).show(player);

        reset();
    }

    public void updateHologram() {
        hologram.setLinesAndUpdate(
                "&6&lLamp Puzzle",
                "&aLight all the lamps &lon&a!",
                "",
                "&e&lCLICK &7to toggle lamps",
                "&e&lBREAK &7to reset puzzle",
                "",
                "&aCurrent Clicks: &l" + clicks
        );
    }

    public void reset() {
        clicks = 0;
        startedAt = System.currentTimeMillis();

        updateHologram();

        board.replaceAll((l, b) -> false);

        final int randomLitCount = ThreadRandom.nextInt(5, 12);
        while (countLit() < randomLitCount) {
            litRandomly();
        }

        updateBlocks(5);
    }

    public void litRandomly() {
        final int random = ThreadRandom.nextInt(board.size());
        int index = 0;

        for (final Block block : board.keySet()) {
            if (index++ == random) {
                if (board.get(block)) {
                    litRandomly();
                }

                board.put(block, true);
            }
        }
    }

    public int countLit() {
        return (int) board.values().stream().filter(v -> v).count();
    }

    public boolean isComplete() {
        return countLit() == board.size();
    }

    public void handleClick(Block block) {
        // Don't update if finished
        if (isComplete()) {
            return;
        }

        changeLit(block);

        for (BlockFace face : relativeFaces) {
            changeLit(block.getRelative(face));
        }

        clicks++;

        updateBlocks(1);
        updateHologram();
        checkCompletion();
    }

    public void updateBlocks(int delay) {
        Runnables.runLater(() -> {
            board.forEach((block, status) -> {
                final BlockData blockData = Material.REDSTONE_LAMP.createBlockData();

                if (blockData instanceof Lightable lightable) {
                    lightable.setLit(status);
                }

                player.sendBlockChange(block.getLocation(), blockData);
            });
        }, delay);
    }

    public void checkCompletion() {
        if (!isComplete()) {
            return;
        }

        Chat.broadcast(
                "&6&lLAMP PUZZLE &a%s &7has completed the puzzle with &a%s&7 clicks in &a%s&7!",
                player.getName(),
                clicks,
                new SimpleDateFormat("mm:ss.SSS").format(System.currentTimeMillis() - startedAt)
        );
        Achievements.COMPLETE_LAMP_PUZZLE.complete(player);

        reset();
    }

    public void remove() {
        hologram.destroy();
    }

    private void changeLit(Block block) {
        if (block.getType() != Material.REDSTONE_LAMP || !(block.getBlockData() instanceof Lightable lightable)) {
            return;
        }

        final boolean value = board.get(block);
        board.put(block, !value);
    }
}
