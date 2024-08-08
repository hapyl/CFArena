package me.hapyl.fight.gui.styled.hotbar;

import com.google.common.collect.Maps;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.loadout.HotbarLoadout;
import me.hapyl.fight.game.loadout.HotbarSlot;
import me.hapyl.fight.game.loadout.HotbarSlots;
import me.hapyl.fight.game.loadout.HotbarTalentSlot;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.gui.styled.Size;
import me.hapyl.fight.gui.styled.StyledGUI;
import me.hapyl.fight.gui.styled.StyledItem;
import me.hapyl.fight.gui.styled.profile.PlayerProfileGUI;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.NoProfileException;
import me.hapyl.fight.ux.Notifier;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.inventory.gui.CancelType;
import me.hapyl.eterna.module.inventory.gui.EventListener;
import me.hapyl.eterna.module.inventory.gui.GUI;
import me.hapyl.eterna.module.player.PlayerLib;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public class HotbarLoadoutGUI extends StyledGUI implements EventListener {

    private static final ItemStack EMPTY_SLOT = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE)
            .setName("&aEmpty Slot!")
            .setSmartLore("There will be nothing in this slot.")
            .asIcon();

    private final int[] HOTBAR_SLOTS = { 27, 28, 29, 30, 31, 32, 33, 34, 35 };

    private final PlayerProfile profile;
    private final HotbarLoadout loadout;
    private final Hero hero;
    private final Map<ItemStack, HotbarSlots> itemToSlotMap;

    private int wrongClicks;

    public HotbarLoadoutGUI(Player player) {
        super(player, "Customize Loadout", Size.FIVE);

        profile = PlayerProfile.getProfile(player);

        if (profile == null) {
            throw new NoProfileException();
        }

        hero = profile.getHeroHandle();
        loadout = profile.getHotbarLoadout();
        itemToSlotMap = Maps.newHashMap();

        putToMap(HotbarSlots.WEAPON, hero.getWeapon().getItem());
        putToMap(HotbarSlots.TALENT_1, hero.getTalentItem(HotbarSlots.TALENT_1));
        putToMap(HotbarSlots.TALENT_2, hero.getTalentItem(HotbarSlots.TALENT_2));
        putToMap(HotbarSlots.TALENT_3, hero.getTalentItem(HotbarSlots.TALENT_3));
        putToMap(HotbarSlots.TALENT_4, hero.getTalentItem(HotbarSlots.TALENT_4));
        putToMap(HotbarSlots.TALENT_5, hero.getTalentItem(HotbarSlots.TALENT_5));
        putToMap(HotbarSlots.HERO_ITEM, null);

        setEventListener(this);
        setCancelType(CancelType.INVENTORY);
        openInventory();

        // Default to the current loadout
        loadout.forEach((slot, i) -> {
            if (slot == HotbarSlots.MAP_ITEM) {
                return;
            }

            final ItemStack item = getItemBySlot(slot);

            if (item.getType().isAir()) {
                setItem(27 + i, item);
                return;
            }

            moveItem(item, 27 + i);
        });
    }

    @Override
    public void onUpdate() {
        setHeader(StyledItem.ICON_LOADOUT.asIcon());
        fillMiddleRow();

        setItem(12, getItemBySlot(HotbarSlots.WEAPON));
        setItem(14, getItemBySlot(HotbarSlots.HERO_ITEM));
        setItem(20, getItemBySlot(HotbarSlots.TALENT_1));
        setItem(21, getItemBySlot(HotbarSlots.TALENT_2));
        setItem(22, getItemBySlot(HotbarSlots.TALENT_3));
        setItem(23, getItemBySlot(HotbarSlots.TALENT_4));
        setItem(24, getItemBySlot(HotbarSlots.TALENT_5));

        final HotbarSlot mapItem = HotbarSlots.MAP_ITEM.get();

        setItem(
                35,
                new ItemBuilder(mapItem.getMaterial()).setName(mapItem.getName())
                        .addLore()
                        .addSmartLore(mapItem.getDescription())
                        .addLore()
                        .addLore("&cThis slot cannot be modified!")
                        .asIcon()
        );

        // Cancel
        setPanelItem(
                2,
                new ItemBuilder(Material.REDSTONE_BLOCK)
                        .setName("&cCancel")
                        .addLore(Color.BUTTON + "Click to cancel!")
                        .asIcon(),
                PlayerProfileGUI::new
        );

        // Confirm
        setPanelItem(
                6,
                new ItemBuilder(Material.EMERALD_BLOCK)
                        .setName("&aConfirm!")
                        .addLore(Color.BUTTON + "Click to confirm!")
                        .asIcon(),
                player -> tryConfirm()
        );
    }

    @Override
    public void listen(Player player, GUI gui, InventoryClickEvent event) {
        final int slot = event.getRawSlot();

        if (!event.getClick().isLeftClick()) {
            wrongClicks++;
            Notifier.error(player, (wrongClicks > 0 && wrongClicks % 5 == 0) ? "LEFT CLICK!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" : "Left click!");
            event.setCancelled(true);
            return;
        }

        if (slot == 35) {
            Notifier.error(player, "This slot cannot be modified!");
            event.setCancelled(true);
            return;
        }

        if (slot < 9 || slot > 44) {
            event.setCancelled(true);
            return;
        }

        final ItemStack item = gui.getItem(slot);
        final ItemStack cursor = event.getCursor();

        if (cursor == null) {
            return;
        }

        if (item == null || item.getType() == EMPTY_SLOT.getType()) {
            event.setCancelled(true);
            event.setCursor(null);

            GameTask.runLater(() -> {
                gui.setItem(slot, cursor);
                fillMiddleRow();
            }, 1);
        }
    }

    private void tryConfirm() {
        final HotbarSlots[] newLoadout = new HotbarSlots[9];

        for (int i = 0; i < HOTBAR_SLOTS.length; i++) {
            final int slot = HOTBAR_SLOTS[i];
            final ItemStack item = getItem(slot);
            final HotbarSlots hotbarSlot = itemToSlotMap.get(item);

            newLoadout[i] = hotbarSlot;
        }

        // unmodifiable
        newLoadout[8] = HotbarSlots.MAP_ITEM;

        for (HotbarSlots value : HotbarSlots.values()) {
            if (arrayContains(newLoadout, value)) {
                continue;
            }

            Notifier.error(player, "Cannot save! Loadout is missing '{}'!", value.getName());
            PlayerLib.villagerNo(player);
            return;
        }

        if (loadout.isIdentical(newLoadout)) {
            Notifier.error(player, "The provided layout is identical to the current one!");
            PlayerLib.villagerNo(player);
            return;
        }

        loadout.setLoadout(newLoadout);
        Notifier.success(player, "Successfully set new loadout!");

        player.closeInventory();
    }

    private <T> boolean arrayContains(T[] array, T item) {
        for (T t : array) {
            if (t == item) {
                return true;
            }
        }

        return false;
    }

    private void moveItem(@Nonnull ItemStack item, int newSlot) {
        for (int i = 0; i < getSize(); i++) {
            final ItemStack itemOnSlot = getItem(i);

            if (itemOnSlot == null || !itemOnSlot.isSimilar(item)) {
                continue;
            }

            setItem(i, null);
            setItem(newSlot, item);
            return;
        }
    }

    private void putToMap(HotbarSlots slot, ItemStack item) {
        final HotbarSlot hotbarSlot = slot.get();
        final ItemBuilder builder = new ItemBuilder(hotbarSlot.getMaterial()).setName(hotbarSlot.getName());

        if (item != null) {
            builder.setType(item.getType());
            builder.addLore("&8" + CFUtils.getItemName(item));
        }

        builder.addLore();
        builder.addTextBlockLore(hotbarSlot.getDescription());
        builder.addLore();
        builder.addSmartLore("Move this item to the designated slot!", " &7&o");

        if (hotbarSlot instanceof HotbarTalentSlot talentSlot) {
            builder.setAmount(talentSlot.getTalentIndex());
        }

        itemToSlotMap.put(builder.asIcon(), slot);
    }

    @Nonnull
    private ItemStack getItemBySlot(@Nullable HotbarSlots slot) {
        if (slot == null) {
            return EMPTY_SLOT;
        }

        for (Map.Entry<ItemStack, HotbarSlots> entry : itemToSlotMap.entrySet()) {
            final ItemStack key = entry.getKey();
            final HotbarSlots value = entry.getValue();

            if (value == slot) {
                return key;
            }
        }

        return EMPTY_SLOT;
    }

    private void fillMiddleRow() {
        for (int i = 27; i <= 35; i++) {
            final ItemStack item = getItem(i);

            if (item == null || item.getType().isAir()) {
                setItem(i, EMPTY_SLOT);
            }
        }
    }

}
