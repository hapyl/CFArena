package me.hapyl.fight.command;

import me.hapyl.eterna.module.command.SimpleCommand;
import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.CF;
import me.hapyl.fight.game.task.TickingGameTask;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;

public class BetterMace extends SimpleCommand implements Listener {

    private final Key key;
    private final ItemStack mace;

    public BetterMace(@Nonnull String name) {
        super(name);

        this.key = Key.ofString("bettermace");
        this.mace = new ItemBuilder(Material.FISHING_ROD, key)
                .modifyMeta(meta -> meta.setItemModel(Material.MACE.getKey()))
                .setName("&aMace &6&lRIGHT CLICK")
                .setUnbreakable()
                .build();

        CF.registerEvents(this);
    }

    @EventHandler
    public void handleReel(PlayerFishEvent ev) {
        final PlayerFishEvent.State state = ev.getState();
        final Player player = ev.getPlayer();
        final PlayerInventory inventory = player.getInventory();
        final ItemStack handItem = inventory.getItemInMainHand();

        if (!ItemBuilder.getItemKey(handItem).equals(key)) {
            return;
        }

        switch (state) {
            case FISHING -> {
                setModel(handItem, Material.BREEZE_ROD);

                final FishHook hook = ev.getHook();
                hook.setInvisible(true);

                final ItemDisplay display = Entities.ITEM_DISPLAY.spawn(
                        hook.getLocation(), self -> {
                            self.setItemStack(ItemBuilder.playerHeadUrl("ea7427e04445e2488877da016764ede2e7cf533c42ca9fc1fb9df66117c9d91c").asIcon());
                            self.setTeleportDuration(1);
                        }
                );

                player.setCooldown(handItem, 20);

                new TickingGameTask() {
                    @Override
                    public void run(int tick) {
                        if (hook.isDead()) {
                            setModel(handItem, Material.MACE);
                            display.remove();

                            // Cooldown
                            cancel();
                            return;
                        }

                        final Location location = hook.getLocation();
                        location.add(0.0d, 0.5d, 0.0d);
                        location.setPitch(0.0f);
                        location.setYaw(tick * 5);

                        display.teleport(location);
                    }
                }.runTaskTimer(0, 1);
            }
        }
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            player.getInventory().addItem(mace);
            player.sendMessage(ChatColor.GREEN + "There you go!");
        }
    }

    private void setModel(ItemStack stack, Material model) {
        final ItemMeta meta = stack.getItemMeta();
        meta.setItemModel(model.getKey());

        stack.setItemMeta(meta);
    }
}
