package me.hapyl.fight.gui.styled.hotbar;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.inventory.gui.CancelType;
import me.hapyl.eterna.module.inventory.gui.EventListener;
import me.hapyl.eterna.module.inventory.gui.GUI;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.fight.CF;
import me.hapyl.fight.Message;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.loadout.HotBarLoadout;
import me.hapyl.fight.game.loadout.HotBarSlot;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.gui.styled.Size;
import me.hapyl.fight.gui.styled.StyledGUI;
import me.hapyl.fight.gui.styled.StyledTexture;
import me.hapyl.fight.gui.styled.profile.PlayerProfileGUI;
import me.hapyl.fight.util.CFUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public class HotbarLoadoutGUI extends StyledGUI implements EventListener {
    
    private static final Material EMPTY_SLOT_MATERIAL = Material.LIME_STAINED_GLASS_PANE;
    private static final ItemStack EMPTY_SLOT = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE)
            .setName("&aEmpty Slot!")
            .setSmartLore("There will be nothing in this slot.")
            .asIcon();
    private final int[] HOTBAR_SLOTS = { 27, 28, 29, 30, 31, 32, 33, 34, 35 };
    private final int UNMODIFIABLE_SLOT = 35;
    private final PlayerProfile profile;
    private final HotBarLoadout loadout;
    private final Hero hero;
    private final Map<ItemStack, HotBarSlot> itemToSlotMap;
    private int wrongClicks;
    
    public HotbarLoadoutGUI(Player player) {
        super(player, "Customize Loadout", Size.FIVE);
        
        profile = CF.getProfile(player);
        hero = profile.getHero();
        loadout = profile.getHotbarLoadout();
        itemToSlotMap = Maps.newHashMap();
        
        putToMap(HotBarSlot.WEAPON, hero.getWeapon().createItem());
        putToMap(HotBarSlot.TALENT_1, hero.getTalentItem(player, HotBarSlot.TALENT_1));
        putToMap(HotBarSlot.TALENT_2, hero.getTalentItem(player, HotBarSlot.TALENT_2));
        putToMap(HotBarSlot.TALENT_3, hero.getTalentItem(player, HotBarSlot.TALENT_3));
        putToMap(HotBarSlot.TALENT_4, hero.getTalentItem(player, HotBarSlot.TALENT_4));
        putToMap(HotBarSlot.TALENT_5, hero.getTalentItem(player, HotBarSlot.TALENT_5));
        putToMap(HotBarSlot.HERO_ITEM, hero.getItem());
        putToMap(HotBarSlot.ARTIFACT, null);
        
        setEventListener(this);
        setCancelType(CancelType.INVENTORY);
        openInventory();
    }
    
    @Override
    public void onUpdate() {
        setHeader(StyledTexture.ICON_LOADOUT.asIcon());
        fillMiddleRow();
        
        setItem(12, getItemBySlot(HotBarSlot.WEAPON));
        setItem(13, getItemBySlot(HotBarSlot.ARTIFACT));
        setItem(14, getItemBySlot(HotBarSlot.HERO_ITEM));
        
        setItem(20, getItemBySlot(HotBarSlot.TALENT_1));
        setItem(21, getItemBySlot(HotBarSlot.TALENT_2));
        setItem(22, getItemBySlot(HotBarSlot.TALENT_3));
        setItem(23, getItemBySlot(HotBarSlot.TALENT_4));
        setItem(24, getItemBySlot(HotBarSlot.TALENT_5));
        
        setItem(
                UNMODIFIABLE_SLOT,
                new ItemBuilder(HotBarSlot.MAP_ITEM.getMaterial())
                        .setName(HotBarSlot.MAP_ITEM.getName())
                        .addLore()
                        .addSmartLore(HotBarSlot.MAP_ITEM.getDescription(), "&7&o")
                        .addLore()
                        .addLore(Color.ERROR + "This slot cannot be modified!")
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
            Message.error(player, (wrongClicks > 0 && wrongClicks % 5 == 0) ? "LEFT CLICK!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" : "Left click!");
            event.setCancelled(true);
            return;
        }
        
        if (slot == UNMODIFIABLE_SLOT) {
            Message.error(player, "This slot cannot be modified!");
            event.setCancelled(true);
            return;
        }
        
        if (slot < 9 || slot > 44) {
            event.setCancelled(true);
            return;
        }
        
        final ItemStack item = gui.getItem(slot);
        final ItemStack cursor = event.getCursor();
        
        if (item == null || item.getType() == EMPTY_SLOT.getType()) {
            event.setCancelled(true);
            event.setCursor(null);
            
            GameTask.runLater(
                    () -> {
                        gui.setItem(slot, cursor);
                        fillMiddleRow();
                    }, 1
            );
        }
    }
    
    private void tryConfirm() {
        final HotBarSlot[] newLoadout = new HotBarSlot[9];
        
        for (int i = 0; i < HOTBAR_SLOTS.length; i++) {
            final int slot = HOTBAR_SLOTS[i];
            final ItemStack item = getItem(slot);
            final HotBarSlot hotbarSlot = itemToSlotMap.get(item);
            
            newLoadout[i] = hotbarSlot;
        }
        
        // unmodifiable
        newLoadout[8] = HotBarSlot.MAP_ITEM;
        
        for (HotBarSlot value : HotBarSlot.values()) {
            if (arrayContains(newLoadout, value)) {
                continue;
            }
            
            Message.error(player, "Cannot save! Loadout is missing '{%s}'!".formatted(value.getName()));
            PlayerLib.villagerNo(player);
            return;
        }
        
        if (loadout.isIdentical(newLoadout)) {
            Message.error(player, "The provided layout is identical to the current one!");
            PlayerLib.villagerNo(player);
            return;
        }
        
        loadout.setLoadout(newLoadout);
        Message.success(player, "Successfully set new loadout!");
        
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
    
    private void putToMap(HotBarSlot slot, ItemStack item) {
        final ItemBuilder builder = new ItemBuilder(slot.getMaterial()).setName(slot.getName());
        
        if (item != null) {
            final Material itemType = item.getType();
            builder.setType(itemType);
            
            if (itemType == Material.PLAYER_HEAD) {
                final SkullMeta skull = (SkullMeta) item.getItemMeta();
                
                builder.modifyMeta(
                        SkullMeta.class, meta -> {
                            meta.setPlayerProfile(skull.getPlayerProfile());
                        }
                );
            }
            else {
                builder.addLore("&8" + CFUtils.getItemName(item));
            }
        }
        
        builder.addLore();
        builder.addTextBlockLore(slot.getDescription(), "&7&o", ItemBuilder.DEFAULT_SMART_SPLIT_CHAR_LIMIT);
        builder.addLore();
        builder.addSmartLore(Color.DEFAULT + "Move this item to the designated slot!");
        
        final int itemAmount = slot.getItemAmount();
        builder.setMaximumStackSize(itemAmount);
        builder.setAmount(itemAmount);
        
        itemToSlotMap.put(builder.asIcon(), slot);
    }
    
    @Nonnull
    private ItemStack getItemBySlot(@Nullable HotBarSlot slot) {
        if (slot == null) {
            return EMPTY_SLOT;
        }
        
        for (Map.Entry<ItemStack, HotBarSlot> entry : itemToSlotMap.entrySet()) {
            final ItemStack key = entry.getKey();
            final HotBarSlot value = entry.getValue();
            
            if (value == slot) {
                return key;
            }
        }
        
        return EMPTY_SLOT;
    }
    
    private void fillMiddleRow() {
        for (int slot = 27; slot <= 35; slot++) {
            final ItemStack item = getItem(slot);
            
            if (item == null || item.getType().isAir()) {
                setItem(slot, emptySlotItem(slot - 27 + 1));
            }
        }
    }
    
    private static ItemStack emptySlotItem(int slot) {
        return new ItemBuilder(EMPTY_SLOT_MATERIAL)
                .setName("&aEmpty Slot!")
                .addTextBlockLore("""
                                  &8Hotbar Slot %1$s
                                  
                                  There is currently nothing on hotbar slot %1$s.
                                  
                                  &8&o;;Move a designated item here to this slot.
                                  """.formatted(slot))
                .asIcon();
    }
    
}
