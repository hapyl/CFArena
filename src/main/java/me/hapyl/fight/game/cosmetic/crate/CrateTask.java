package me.hapyl.fight.game.cosmetic.crate;

import me.hapyl.fight.game.cosmetic.Cosmetic;
import me.hapyl.fight.game.cosmetic.Rarity;
import me.hapyl.fight.game.task.RangeTask;
import me.hapyl.fight.game.task.ShutdownAction;
import me.hapyl.fight.util.collection.ImmutableTuple;
import me.hapyl.spigotutils.module.hologram.Hologram;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public abstract class CrateTask extends RangeTask {

    protected final CrateLoot loot;
    private ImmutableTuple<Item, Hologram> display;

    public CrateTask(@Nonnull CrateLoot loot) {
        this.loot = loot;

        setShutdownAction(ShutdownAction.IGNORE);
        runTaskTimer(0, 1);
    }

    public CrateTask tick(int min, int max, @Nonnull CrateTaskConsumer consumer) {
        at(min, max, s -> consumer.accept(this));
        return this;
    }

    public CrateTask tick(int value, @Nonnull CrateTaskConsumer consumer) {
        return tick(value, value, consumer);
    }

    @Nonnull
    public ImmutableTuple<Item, Hologram> display(@Nonnull Location location) {
        removeDisplay();

        final Cosmetic cosmetic = loot.getLoot().getCosmetic();
        final Rarity rarity = cosmetic.getRarity();
        final CrateChest chest = loot.getChest();
        final World world = chest.getWorld();

        final Item item = world.spawn(
                location,
                Item.class, self -> {
                    self.setVelocity(new Vector(0.0d, 0.0d, 0.0d));
                    self.setItemStack(new ItemStack(cosmetic.getIcon()));
                    self.setPickupDelay(10000);
                    self.setGravity(false);
                }
        );

        final Hologram hologram = new Hologram().setLines(
                ChatColor.GREEN + cosmetic.getName(),
                ChatColor.GRAY + cosmetic.getType().getName(),
                rarity.getFormatted()
        ).create(location.add(0.0d, 0.25d, 0.0d)).showAll();

        PlayerLib.playSound(location, Sound.ENTITY_VILLAGER_YES, 0.75f);

        if (loot.getLoot().getRarity().isFlexible()) {
            PlayerLib.playSound(location, Sound.ENTITY_ENDER_DRAGON_GROWL, 2.0f);
        }

        chest.broadcastLoot(loot);
        return display = new ImmutableTuple<>(item, hologram);
    }

    @Override
    public final void onTaskStop() {
        final CrateChest chest = loot.getChest();
        chest.setOccupied(null);
        chest.playCloseAnimation();
        chest.hologram.showAll();

        removeDisplay();
        rangeMap.clear();
    }

    public void removeDisplay() {
        if (display == null) {
            return;
        }

        display.getA().remove();
        display.getB().destroy();
        display = null;
    }

    public interface CrateTaskConsumer {

        void accept(@Nonnull CrateTask task);

    }
}
