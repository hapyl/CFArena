package me.hapyl.fight.game.tutorial;

import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.inventory.gui.SlotPattern;
import me.hapyl.spigotutils.module.inventory.gui.SmartComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class TutorialHeroes extends TutorialGUI {

    private final int SPLIT_WIDTH = 40;

    public TutorialHeroes(Player player) {
        super(player, "Heroes");
    }

    @Override
    public void updateInventory() {
        setItem(4, ItemBuilder.of(Material.PLAYER_HEAD, "Heroes", "Read about main mechanics of the heroes.").asIcon());

        final SmartComponent component = newSmartComponent();

        component.add(ItemBuilder.of(Material.STICK, "Weapon")
                .addSmartLore("Every hero has a weapon to fight with!__It is &f&lalways&7 located to your first slot.")
                .asIcon());

        component.add(ItemBuilder.of(Material.GOLDEN_CARROT, "Talents")
                .addSmartLore("Every hero also has at least 3 talents.", SPLIT_WIDTH)
                .addLore()

                .addLore("&bCombat Talents")
                .addSmartLore(
                        "Combat talents are what makes each heroes unique. They provide damage and utility to benefit you in the battle.",
                        SPLIT_WIDTH
                )
                .addLore()
                .addSmartLore("They are located in the hotbar and activated by pressing the corresponding number key.", SPLIT_WIDTH)
                .addSmartLore("As example: To activate your first talent, press 2.", "&8", SPLIT_WIDTH)
                .addSmartLore("You can rebind your hotbar keys to make it easier to use your talents.", "&8", SPLIT_WIDTH)
                .addLore()
                .addSmartLore(
                        "Usually, hero has 2 combat talents and 1 passive talent, where some heroes have more than 2, those heroes are called &6Complex&7, and are not recommended for a beginner.",
                        SPLIT_WIDTH
                )
                .addLore("")

                .addLore("&bPassive Talent")
                .addSmartLore(
                        "Passive talent is always combat and can be a small boost to hero hero, or a main mechanic of the hero.",
                        SPLIT_WIDTH
                )
                .addSmartLore("Make sure to read about hero talents in the hero details GUI!", "&8", SPLIT_WIDTH)

                .asIcon());

        component.add(ItemBuilder.of(Material.NETHER_STAR, "Ultimate")
                .addSmartLore("Each hero has an ultimate ability that can be used to turn the tide of the battle.", SPLIT_WIDTH)
                .addLore()
                .addSmartLore(
                        "To unleash your ultimate, you must accumulate &b&l※ Ultimate Points&7, which are gained by using talent and simply waiting.",
                        SPLIT_WIDTH
                )

                .addLore()
                .addSmartLore("Once your ultimate is ready, press &f&lF&7 (swap hands) to unleash it!", SPLIT_WIDTH)
                .asIcon());

        component.apply(this, SlotPattern.DEFAULT, 2);
    }


}
