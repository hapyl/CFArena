package me.hapyl.fight.game.cosmetic.archive.gadget.dice;

import me.hapyl.fight.game.color.Color;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.inventory.ItemStack;

public class DiceSide {

    private final int side;
    private final String texture;
    private final ItemStack item;

    public DiceSide(int side, String texture) {
        this.side = side;
        this.texture = texture;
        this.item = ItemBuilder.playerHeadUrl(texture).setName("dice " + side).asIcon();
    }

    public int getSide() {
        return side;
    }

    public String getTexture() {
        return texture;
    }

    public ItemStack getItem() {
        return item;
    }

    @Override
    public String toString() {
        return (side < 3 ? Color.RED : side < 6 ? Color.GREEN : Color.DEEP_PURPLE).bold() + side;
    }
}