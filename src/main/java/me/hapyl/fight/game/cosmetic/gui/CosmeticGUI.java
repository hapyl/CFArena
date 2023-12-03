package me.hapyl.fight.game.cosmetic.gui;

import me.hapyl.fight.game.cosmetic.Cosmetics;
import me.hapyl.fight.game.cosmetic.ItemActionPair;
import me.hapyl.fight.game.cosmetic.Type;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.inventory.gui.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class CosmeticGUI extends PlayerGUI {

    private final Type type;

    public CosmeticGUI(Player player, Type type) {
        super(player, "Cosmetic " + GUI.ARROW_FORWARD + " " + Chat.capitalize(type.name()), 5);
        this.type = type;

        updateInventory();
    }

    public void updateInventory() {
        clearEverything();

        final List<Cosmetics> cosmetics = Cosmetics.getByType(type);

        // Don't show unobtainable cosmetics unless magically selected
        cosmetics.removeIf(cosmetic -> {
            if (!cosmetic.getCosmetic().isPurchaseable()) {
                return !cosmetic.isUnlocked(getPlayer()) && !cosmetic.isSelected(getPlayer());
            }

            return false;
        });

        // Set back button at slot 18
        setItem(18, ItemBuilder.of(Material.ARROW, "Go Back").asIcon(), CollectionGUI::new);

        if (cosmetics.isEmpty()) {
            setItem(22, ItemBuilder.of(Material.MINECART, "&7No Cosmetics :(").asIcon());
        }
        else {
            // Sort by owned
            cosmetics.sort((a, b) -> {
                if (a.isUnlocked(getPlayer()) && !b.isUnlocked(getPlayer())) {
                    return -1;
                }
                else if (!a.isUnlocked(getPlayer()) && b.isUnlocked(getPlayer())) {
                    return 1;
                }
                return 0;
            });

            final SmartComponent component = newSmartComponent();
            for (Cosmetics cosmetic : cosmetics) {
                final ItemActionPair pair = cosmetic.getCosmetic().createItem(getPlayer(), cosmetic, this);

                component.add(pair.getItemStack(), player -> {
                    final Action action = pair.getAction();

                    if (action != null) {
                        action.invoke(player);
                    }
                });
            }

            component.apply(this, SlotPattern.INNER_LEFT_TO_RIGHT, 1);
        }

        openInventory();
    }

}
