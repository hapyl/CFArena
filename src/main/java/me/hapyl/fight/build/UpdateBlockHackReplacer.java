package me.hapyl.fight.build;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.MovingObjectPositionBlock;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.hapyl.fight.CF;
import me.hapyl.fight.Main;
import me.hapyl.fight.game.Debug;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.util.IProtocolListener;
import me.hapyl.fight.ux.Notifier;
import me.hapyl.spigotutils.module.command.SimplePlayerAdminCommand;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;

public class UpdateBlockHackReplacer implements Listener {

    private final Map<UUID, ReplacerData> dataMap = Maps.newHashMap();
    private final Set<Material> blocks = Sets.newHashSet();

    public UpdateBlockHackReplacer() {
        for (Material material : Material.values()) {
            if (material.isBlock()) {
                blocks.add(material);
            }
        }

        CF.registerEvents(this);

        // Have to use a packet to detect right click because
        // the event is not fired when clicking at a disabled block ¯\_(ツ)_/¯
        CF.registerProtocolListener(new IProtocolListener() {
            @Nonnull
            @Override
            public PacketType getPacketType() {
                return PacketType.Play.Client.USE_ITEM;
            }

            @Override
            public void onPacketReceiving(@Nonnull PacketEvent ev) {
                final Player player = ev.getPlayer();
                final PacketContainer packet = ev.getPacket();
                final EnumWrappers.Hand hand = packet.getHands().read(0);

                if (hand == EnumWrappers.Hand.OFF_HAND) {
                    return;
                }

                if (Manager.current().isGameInProgress()) {
                    return;
                }

                final MovingObjectPositionBlock position = packet.getMovingBlockPositions().read(0);
                final Location location = position.getBlockPosition().toLocation(player.getWorld());

                // Sync
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        workBlock(player, location.getBlock(), (data, block) -> {
                            final BlockData blockData = data.getBlockData();

                            if (blockData != null) {
                                block.setBlockData(blockData, false);
                            }
                        });
                    }
                }.runTask(Main.getPlugin());

            }

            @Override
            public void onPacketSending(@Nonnull PacketEvent ev) {
            }
        });

        CF.registerCommand(makeCommand());
    }

    @EventHandler()
    public void handlePlayerInteractEvent(PlayerInteractEvent ev) {
        if (ev.getHand() == EquipmentSlot.OFF_HAND) {
            return;
        }

        if (ev.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        final Player player = ev.getPlayer();

        workBlock(player, ev.getClickedBlock(), (data, block) -> {
            ev.setCancelled(true);
            data.setBlockData(block.getBlockData());
        });
    }

    @Nonnull
    private SimplePlayerAdminCommand makeCommand() {
        final SimplePlayerAdminCommand command = new SimplePlayerAdminCommand("replacer") {
            @Override
            protected void execute(Player player, String[] args) {
                // replacer (block)
                final Material material = getArgument(args, 0).toEnum(Material.class);

                if (material == null) {
                    Notifier.error(player, "Invalid material!");
                    return;
                }

                if (!material.isBlock()) {
                    Notifier.error(player, "Material must be a block!");
                    return;
                }

                getData(player).setBlockData(material.createBlockData());
            }

            @Nullable
            @Override
            protected List<String> tabComplete(CommandSender sender, String[] args) {
                return completerSort(blocks, args);
            }
        };

        command.setAliases("re");
        return command;
    }

    private void workBlock(Player player, Block block, BiConsumer<ReplacerData, Block> consumer) {
        if (Manager.current().isGameInProgress()) {
            return;
        }

        if (block == null) {
            return;
        }

        final ItemStack item = player.getInventory().getItemInMainHand();
        final Material itemType = item.getType();

        if (itemType.isAir()) {
            return;
        }

        final ReplacerData data = getData(player);

        if (data.getTool() != itemType) {
            return;
        }

        if (player.hasCooldown(itemType)) {
            return;
        }

        player.setCooldown(itemType, 1);

        consumer.accept(data, block);
    }

    private ReplacerData getData(Player player) {
        final UUID uuid = player.getUniqueId();

        return dataMap.computeIfAbsent(uuid, k -> new ReplacerData(uuid));
    }

}
