package me.hapyl.fight.game.entity;

import com.google.common.collect.Maps;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.loadout.HotbarLoadout;
import me.hapyl.fight.game.loadout.HotbarSlots;
import me.hapyl.fight.game.talents.ChargedTalent;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.util.Ticking;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.math.Numbers;
import me.hapyl.spigotutils.module.util.CollectionUtils;
import me.hapyl.spigotutils.module.util.Compute;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TalentLock implements Ticking {

    private static final ItemStack[] DYE_MATERIALS = {
            createDyeItem(Material.LIME_STAINED_GLASS_PANE),
            createDyeItem(Material.YELLOW_STAINED_GLASS_PANE),
            createDyeItem(Material.RED_STAINED_GLASS_PANE),
            createDyeItem(Material.ORANGE_STAINED_GLASS_PANE),
            createDyeItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE),
            createDyeItem(Material.BLUE_STAINED_GLASS_PANE),
            createDyeItem(Material.PINK_STAINED_GLASS_PANE),
            createDyeItem(Material.MAGENTA_STAINED_GLASS_PANE),
            createDyeItem(Material.GREEN_STAINED_GLASS_PANE),
            createDyeItem(Material.PURPLE_STAINED_GLASS_PANE)
    };

    private final GamePlayer player;
    private final Hero hero;
    private final Map<HotbarSlots, Integer> lock;
    private final HotbarLoadout loadout;

    public TalentLock(GamePlayer player, Hero hero) {
        this.player = player;
        this.hero = hero;
        this.lock = Maps.newHashMap();
        this.loadout = player.getProfile().getHotbarLoadout();

        // Init talents since it should only apply to existing hero talents
        for (HotbarSlots slot : HotbarSlots.TALENT_SLOTS) {
            final Talent talent = hero.getTalent(slot);

            if (talent == null) {
                continue;
            }

            lock.put(slot, 0);
        }
    }

    public void reset() {
        lock.forEach((slot, tick) -> lock.put(slot, 0));
    }

    public int getLock(@Nonnull HotbarSlots slot) {
        return lock.getOrDefault(slot, 0);
    }

    public boolean isLocked(@Nonnull HotbarSlots slot) {
        return getLock(slot) > 0;
    }

    @Nonnull
    public Set<HotbarSlots> getAvailableSlots() {
        return new HashSet<>(lock.keySet());
    }

    public boolean setLock(@Nonnull HotbarSlots slot, int tick) {
        if (!lock.containsKey(slot)) {
            return false;
        }

        final Integer oldLock = lock.get(slot);

        if (tick < oldLock) {
            return true;
        }

        lock.put(slot, tick);
        return true;
    }

    public void setLockAll(int duration) {
        for (HotbarSlots slot : HotbarSlots.values()) {
            setLock(slot, duration);
        }
    }

    @Nullable
    public HotbarSlots setLockRandomly(int duration) {
        final HotbarSlots slot = CollectionUtils.randomElement(lock.keySet());

        if (slot == null) {
            return null;
        }

        setLock(slot, duration);
        return slot;
    }

    @Override
    public void tick() {
        lock.forEach((slot, tick) -> {
            if (tick <= 0) {
                return;
            }

            final Integer newValue = lock.compute(slot, Compute.intSubtract());

            // Use the real texture
            if (newValue == 0) {
                final Talent talent = hero.getTalent(slot);
                player.giveTalentItem(slot);

                // Fx here because charged talent thingy
                player.playSound(Sound.ENTITY_ENDERMAN_HURT, 0.0f);
                player.playSound(Sound.ENTITY_ENDER_DRAGON_FLAP, 0.0f);

                if (talent instanceof ChargedTalent chargedTalent) {
                    final int chargesAvailable = chargedTalent.getChargesAvailable(player);
                    final ItemStack item = player.getItem(slot);

                    if (item == null || chargesAvailable == 0) {
                        player.setItem(slot, chargedTalent.noChargesItem());
                        return;
                    }

                    item.setAmount(chargesAvailable);
                }
                return;
            }

            // Fx
            if (tick % 5 == 0) {
                final ItemStack item = getRandomDyeItem();
                item.setAmount(Numbers.clamp(tick / 20, 1, 64));

                player.setItem(slot, item);
            }
        });
    }

    @Nonnull
    private ItemStack getRandomDyeItem() {
        return CollectionUtils.randomElement(DYE_MATERIALS, DYE_MATERIALS[0]);
    }

    private static ItemStack createDyeItem(Material material) {
        return new ItemBuilder(material)
                .setName("&5&l&kTalent Locked")
                .addSmartLore("Some ancient power of preventing this talent to be used!", "&d&l&k")
                .asIcon();
    }
}
