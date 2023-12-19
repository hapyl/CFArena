package me.hapyl.fight.game.talents.archive.techie;

import com.google.common.collect.Sets;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.ChargedTalent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class TrapWire extends ChargedTalent implements Listener {

    private final Map<Player, Set<Tripwire>> trapMap = new HashMap<>();
    @DisplayField private final int pickupDelay = 8 * 20;
    @DisplayField(extra = "Recharges upon activation") private final int rechargeCd = 80;
    @DisplayField private final int destroyedCd = 160;
    @DisplayField(suffix = "blocks") private final short tripwireMaxLength = 10;
    @DisplayField private final int windupTime = 40;

    public TrapWire() {
        super("Tripwire", """
                Place a tripwire between two blocks. Activates upon contact with an opponent and applies &b&lCYber &b&lHack&7.
                                
                &e&lPUNCH &7the wire to pick it up.
                                
                &c;;This ability can be destroyed!
                """, 3);

        setItem(Material.STRING);
        setCooldownSec(3);
    }

    public long getWindupTimeAsMillis() {
        return windupTime * 50L;
    }

    @Override
    public void onStop() {
        super.onStop();

        trapMap.values().forEach(set -> {
            set.forEach(Tripwire::clearBlocks);
            set.clear();
        });
        trapMap.clear();
    }

    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        super.onDeath(player);
        //getTraps(player).forEach(Tripwire::clearBlocks);
        //trapMap.remove(player);
    }

    @Override
    public void onStart() {
        super.onStart();

        new GameTask() {
            @Override
            public void run() {
                trapMap.values().forEach(set -> set.forEach(tripwire -> {
                    if (!tripwire.isActive()) {
                        return;
                    }

                    tripwire.drawLine();
                }));
            }
        }.runTaskTimer(10, 10);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        //final Set<Block> blocks = getTargetBlock(player);
        //final Set<Tripwire> traps = getTraps(player);
        //
        //if (blocks == null) {
        //    return Response.ERROR;
        //}
        //
        //final Tripwire trap = new Tripwire(player, blocks);
        //trap.setBlocks();
        //traps.add(trap);

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
        //final Player player = ev.getPlayer();
        //final Location location = player.getLocation();
        //
        //if (!Manager.current().isGameInProgress()
        //        || trapMap.isEmpty()
        //        || !Manager.current().isPlayerInGame(player)) {
        //    return;
        //}
        //
        //if (!checkBlock(player, location.getBlock())) {
        //    checkBlock(player, location.getBlock().getRelative(BlockFace.UP));
        //}
    }

    private boolean checkBlock(Player player, Block block) {
        if (block.getType() != Material.TRIPWIRE) {
            return false;
        }

        final Tripwire tripwire = byBlock(block);

        if (tripwire == null || !tripwire.isActive() || tripwire.getPlayer() == player) {
            return false;
        }

        //tripwire.affectPlayer(player);

        // remove trap and grant charge
        removeTrap(tripwire);
        //grantCharge(tripwire.getPlayer(), rechargeCd);

        return true;
    }

    @EventHandler()
    public void handleInteract(PlayerInteractEvent ev) {
        //final Player player = ev.getPlayer();
        //if (!Manager.current().isGameInProgress() || ev.getAction() != Action.LEFT_CLICK_BLOCK) {
        //    return;
        //}
        //
        //final Block block = ev.getClickedBlock();
        //if (block == null || block.getType() != Material.TRIPWIRE) {
        //    return;
        //}
        //
        //ev.setCancelled(true);
        //final Tripwire tripwire = byBlock(block);
        //
        //if (tripwire == null || !tripwire.isActive()) {
        //    return;
        //}
        //
        //removeTrap(tripwire);
        //
        //final Player trapOwner = tripwire.getPlayer();
        //
        //if (trapOwner == player) {
        //    grantCharge(player, pickupDelay);
        //    //startCd(player, pickupDelay);
        //
        //    // Fx
        //    Chat.sendMessage(player, "&aPicked up tripwire.");
        //    PlayerLib.playSound(player, Sound.ENTITY_SPIDER_AMBIENT, 1.75f);
        //}
        //else {
        //    removeTrap(tripwire);
        //    grantCharge(trapOwner, destroyedCd);
        //
        //    // Fx
        //    Chat.sendMessage(player, "&cYou destroyed %s's tripwire.", trapOwner.getName());
        //    PlayerLib.playSound(player, Sound.ITEM_SHIELD_BREAK, 1.25f);
        //}
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

        for (int i = 0; i < tripwireMaxLength; i++) {
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
