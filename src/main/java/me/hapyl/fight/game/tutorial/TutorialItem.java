package me.hapyl.fight.game.tutorial;

import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.Material;

public class TutorialItem {

    private final ItemBuilder builder;

    protected TutorialItem(Material material) {
        this.builder = ItemBuilder.of(material);
    }

}
