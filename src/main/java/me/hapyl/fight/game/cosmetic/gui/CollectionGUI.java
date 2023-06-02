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
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class CollectionGUI extends PlayerGUI {

    public CollectionGUI(Player player) {
        super(player, "Collection", 4);
        setOpenEvent(e -> {
            PlayerLib.playSound(player, Sound.BLOCK_CHEST_OPEN, 1.0f);
        });

        update();
        openInventory();
    }

    public void update() {
        final SmartComponent component = newSmartComponent();
        final CosmeticEntry cosmetics = PlayerDatabase.getDatabase(getPlayer()).getCosmetics();

        setItem(4, ItemBuilder.of(Material.CHEST, "Collection", "View and purchase cosmetics!").asIcon());
        setArrowBack(31, "Profile", t -> new PlayerProfileGUI(getPlayer()));

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

        component.apply(this, SlotPattern.CHUNKY, 2);
    }
}
