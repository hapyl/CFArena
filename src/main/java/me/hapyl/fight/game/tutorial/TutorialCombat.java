package me.hapyl.fight.game.tutorial;

import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.inventory.gui.SlotPattern;
import me.hapyl.spigotutils.module.inventory.gui.SmartComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class TutorialCombat extends TutorialGUI {

    public TutorialCombat(Player player) {
        super(player, "Combat");
    }

    @Override
    public void updateInventory() {
        setItem(4, ItemBuilder.of(Material.IRON_SWORD, "Combat")
                .addLore()
                .addSmartLore("Read about all combat changes for the game.")
                .asIcon());

        final SmartComponent component = newSmartComponent();

        component.add(ItemBuilder.of(Material.WOODEN_SWORD, "Melee Changes")
                .addLore("")
                .addLore("&b- No critical hits.")
                .addSmartLore("There is no need to jump to crit because, well, there are no critical hits.", " &7")
                .addLore("")

                .addLore("&b- Static Damage")
                .addSmartLore(
                        "It does not matter if you're fighting with a stick, or a netherite sword, the damage is not based on the vanilla item type.",
                        " &7"
                )

                .addSmartLore("Yet you still can't spam your weapon, the vanilla 'wait' mechanic is still present.", " &7")
                .asIcon());

        component.add(ItemBuilder.of(Material.BOW, "Range Changes")
                .addLore("&b- Strength")
                .addSmartLore("Unlike vanilla, strength affects &f&lALL&7 outgoing damage, including bows!", " &7")
                .asIcon());

        component.add(ItemBuilder.of(Material.BLAZE_POWDER, "Effect Changes")
                .addLore("&b- Strength Changes")
                .addSmartLore(
                        "Speaking of strength, it uses different formula to calculate damage to not make it OP as it is in vanilla.",
                        " &7"
                )
                .addLore("The current formula: &c+20% damage per Strength level")

                .addLore("")
                .addLore("&b- Weakness Changes")
                .addSmartLore("Weakness always reduces your damage by half, no matter the level of the effect.", " &7")

                .addLore("")
                .addLore("&b- Resistance Changes")
                .addSmartLore("Resistance always reduces incoming damage by &f&l85%&7, no matter the level of the effect.", " &7")

                .addLore("")
                .addLore("&b- Vulnerability")
                .addSmartLore("Vulnerability is new effect that &f&ldoubles&7 the damage you &ntake&7.", " &7")
                .asIcon());

        component.apply(this, SlotPattern.DEFAULT, 2);
    }

}
