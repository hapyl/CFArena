package me.hapyl.fight.game.heroes.troll;

import com.google.common.collect.Sets;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.*;
import org.bukkit.block.Block;

import java.util.Set;

public class StickyCobweb extends GameTask {

    private final GamePlayer player;
    private final Set<Block> blocks;

    public StickyCobweb(GamePlayer player) {
        this.player = player;
        this.blocks = Sets.newHashSet();

        final Location location = player.getLocation().subtract(2, 0, 2);

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if ((i == 0 || i == 4) && (j == 0 || j == 4)) {
                    continue;
                }

                location.add(i, 0, j);
                if (!location.getBlock().getType().isSolid()) {
                    blocks.add(location.getBlock());
                }
                location.subtract(i, 0, j);
            }
        }

        Bukkit.getOnlinePlayers().forEach(target -> {
            if (player.is(target)) {
                return;
            }

            for (Block block : blocks) {
                target.sendBlockChange(block.getLocation(), Material.COBWEB.createBlockData());
            }

            Chat.sendMessage(target, "&aAh... Sticky! &e&lPUNCH &athe cobweb to remove it!");
        });

        runTaskTimer(0, 20);
    }

    @Override
    public void run() {
        blocks.forEach(block -> {
            PlayerLib.spawnParticle(BukkitUtils.centerLocation(block.getLocation()), Particle.CLOUD, 1);
        });
    }

    public void clear(GamePlayer player, Block block) {
        if (player.equals(this.player)) {
            return;
        }

        final Location location = BukkitUtils.centerLocation(block.getLocation());

        if (blocks.contains(block)) {
            blocks.remove(block);
            block.getState().update(true, false); // sync between players

            // Fx
            PlayerLib.spawnParticle(location, Particle.EXPLOSION_NORMAL, 3, 0.1, 0.1, 0.1, 0.02f);
            PlayerLib.playSound(location, Sound.BLOCK_WOOL_PLACE, 0.0f);
            PlayerLib.playSound(location, Sound.BLOCK_WOOL_BREAK, 0.0f);

            if (blocks.isEmpty()) {
                remove();
            }
        }
    }

    public void remove() {
        CFUtils.clearCollection(blocks);
        cancel();
    }
}
