package kz.hapyl.fight.game.talents.storage;

import com.google.common.collect.Sets;
import kz.hapyl.fight.game.Manager;
import kz.hapyl.fight.game.Response;
import kz.hapyl.fight.game.talents.ChargedTalent;
import kz.hapyl.fight.game.talents.storage.extra.Tripwire;
import kz.hapyl.fight.game.task.GameTask;
import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import kz.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import javax.annotation.Nullable;
import java.util.*;

public class TrapWire extends ChargedTalent implements Listener {

    private final Map<Player, Set<Tripwire>> trapMap = new HashMap<>();
    private final int pickupDelay = 8 * 20;

    public TrapWire() {
        super("Tripwire", 2);
        this.setInfo(
                // Place a tripwire between two points. Activates upon opponents touch and applies &bCYber Hack&7.____&e&lPUNCH &7the wire to pick it up.
                "Place a tripwire between two points. Activates upon opponents touch and applies &bCYber Hack&7.____&e&lPUNCH &7the wire to pick it up."
        );
        this.setItem(Material.STRING);
        this.setCdSec(3);

        this.addExtraInfo("&aRecharge Time: &l%ss", BukkitUtils.roundTick(pickupDelay));
        this.addExtraInfo(" &8&oRecharges upon activation or pickup.");
    }

    @Override
    public void onStop() {
        trapMap.values().forEach(set -> {
            set.forEach(Tripwire::clearBlocks);
            set.clear();
        });
        trapMap.clear();
    }

    @Override
    public void onDeath(Player player) {
        getTraps(player).forEach(Tripwire::clearBlocks);
        trapMap.remove(player);
    }

    @Override
    public void onStart() {
        new GameTask() {
            @Override
            public void run() {
                trapMap.values().forEach(set -> {
                    set.forEach(Tripwire::drawLine);
                });
            }
        }.runTaskTimer(10, 10);
    }

    @Override
    public Response execute(Player player) {
        final Set<Block> blocks = getTargetBlock(player);
        final Set<Tripwire> traps = getTraps(player);

        if (blocks == null) {
            return Response.ERROR;
        }

        final Tripwire trap = new Tripwire(player, blocks);
        trap.setBlocks();
        traps.add(trap);

        return Response.OK;
    }

    public Set<Tripwire> getTraps(Player player) {
        return trapMap.computeIfAbsent(player, k -> Sets.newConcurrentHashSet());
    }

    public void removeTrap(Tripwire trap) {
        trap.clearBlocks();
        getTraps(trap.getPlayer()).remove(trap);
    }

    @EventHandler()
    public void handleMoveEvent(PlayerMoveEvent ev) {
        final Player player = ev.getPlayer();
        final Location location = player.getLocation();

        if (!Manager.current().isGameInProgress() || this.trapMap.isEmpty() || !Manager.current()
                                                                                       .isPlayerInGame(player)) {
            return;
        }

        byte bit = 0;
        for (Block block = location.getBlock(); bit <= 1; block = block.getRelative(BlockFace.UP), ++bit) {
            if (block.getType() == Material.TRIPWIRE) {
                final Tripwire trap = byBlock(block);
                if (trap != null && trap.getPlayer() != player) {
                    trap.affectPlayer(player);
                    this.removeTrap(trap);
                    return;
                }
            }
        }
    }

    @EventHandler()
    public void handleInteract(PlayerInteractEvent ev) {
        final Player player = ev.getPlayer();
        if (!Manager.current().isGameInProgress() || ev.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        final Block block = ev.getClickedBlock();
        if (block == null || block.getType() != Material.TRIPWIRE) {
            return;
        }

        ev.setCancelled(true);
        final Tripwire tripwire = byBlock(block);

        if (tripwire != null && tripwire.getPlayer() == player) {
            removeTrap(tripwire);
            grantCharge(player);
            startCd(player, pickupDelay);

            // Fx
            Chat.sendMessage(player, "&aPicked up Tripwire.");
            PlayerLib.playSound(player, Sound.ENTITY_SPIDER_AMBIENT, 1.75f);
        }

    }

    @Nullable
    private Tripwire byBlock(Block block) {
        for (final Set<Tripwire> set : this.trapMap.values()) {
            for (final Tripwire tripwire : set) {
                if (tripwire.isBlockATrap(block)) {
                    return tripwire;
                }
            }
        }
        return null;
    }

    private Set<Block> getTargetBlock(Player player) {
        final List<Block> targetBlocks = player.getLastTwoTargetBlocks(null, 5);

        if (targetBlocks.size() != 2 || targetBlocks.get(1).getType().isAir()) {
            return null;
        }

        final Block targetBlock = targetBlocks.get(1);
        final Block adjacentBlock = targetBlocks.get(0);
        final BlockFace face = targetBlock.getFace(adjacentBlock);

        if (face == null || face == BlockFace.UP || face == BlockFace.DOWN) {
            errorMessage(player, "Cannot find a valid block!");
            return null;
        }

        // check for allowed block
        switch (targetBlock.getType()) {
            case BARRIER, TRIPWIRE -> {
                errorMessage(player, "Cannot place tripwire on this block!");
                return null;
            }
        }

        final Set<Block> blocks = new HashSet<>();
        Block next = adjacentBlock;

        for (int i = 0; i < 7; i++) {
            // Hit another block, break the loop
            if (!next.getType().isAir()) {
                break;
            }
            blocks.add(next);
            next = next.getRelative(face);
        }

        if (next.getType().isAir()) {
            errorMessage(player, "Couldn't find a block to connect!");
            return null;
        }

        return blocks;
    }

    private void errorMessage(Player player, String message) {
        Chat.sendMessage(player, "&c" + message);
        PlayerLib.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 0.0f);
    }

}
