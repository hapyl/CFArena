package me.hapyl.fight.game.cosmetic.gui;

import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.entry.CosmeticEntry;
import me.hapyl.fight.game.cosmetic.Cosmetics;
import me.hapyl.fight.game.cosmetic.Type;
import me.hapyl.fight.gui.PlayerProfileGUI;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.inventory.gui.PlayerGUI;
import me.hapyl.spigotutils.module.inventory.gui.SlotPattern;
import me.hapyl.spigotutils.module.inventory.gui.SmartComponent;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class CollectionGUI extends PlayerGUI {

    //public static final SlotPattern PATTERN = new SlotPattern(new byte[][] {
    //        { 0, 0, 1, 0, 0, 0, 0, 0, 0 },
    //        { 0, 1, 0, 1, 0, 0, 0, 0, 0 },
    //        { 0, 1, 1, 1, 0, 0, 0, 0, 0 }
    //});

    //public static final int[] ANIMATION_SLOTS = new int[] {
    //        15, 16, 17, 26, 35, 34, 33, 24
    //};

    public CollectionGUI(Player player) {
        super(player, "Collection", 3);
        setOpenEvent(e -> {
            PlayerLib.playSound(player, Sound.BLOCK_CHEST_OPEN, 1.0f);
        });

        //setPeriod(5);
        //setMaxTicks(ANIMATION_SLOTS.length);

        update();
        openInventory();
    }

    //@Override
    //public void onTick(long l) {
    //    final int pixelSlot = ANIMATION_SLOTS[(int) l];
    //
    //    for (int slot : ANIMATION_SLOTS) {
    //        setItem(slot, ItemBuilder.of(Material.GREEN_STAINED_GLASS_PANE).asIcon());
    //    }
    //
    //    setItem(pixelSlot, ItemBuilder.of(Material.LIME_STAINED_GLASS_PANE).asIcon());
    //}

    public void update() {
        final SmartComponent component = newSmartComponent();
        final CosmeticEntry cosmetics = PlayerDatabase.getDatabase(getPlayer()).getCosmetics();

        setArrowBack(18, new PlayerProfileGUI(getPlayer()));

        //fillLine(0, ItemStacks.BLACK_BAR);
        //fillLine(4, ItemStacks.BLACK_BAR);

        for (Type type : Type.values()) {
            final String name = Chat.capitalize(type) + " Cosmetics";
            final Cosmetics selected = cosmetics.getSelected(type);

            component.add(ItemBuilder.of(type.getMaterial(), name)
                    .addSmartLore("&7" + type.getDescription())
                    .addLore()
                    .addLore("&aCurrently Selected: &e" + (selected == null ? "&8None!" : selected.getCosmetic().getName()))
                    .addLore()
                    .addLore("&eClick to browse " + name + "!")
                    .asIcon(), player -> new CosmeticGUI(player, type));
        }

        //setItem(
        //        25,
        //        ItemBuilder.of(Material.EMERALD, "Experience")
        //                .addSmartLore("Earn experience in game and unlock unique reward!")
        //                .addLore()
        //                .addLore("&eClick to open Experience Menu!")
        //                .asIcon(),
        //        ExperienceGUI::new
        //);

        component.apply(this, SlotPattern.CHUNKY, 1);
    }
}
