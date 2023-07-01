package me.hapyl.fight.game.cosmetic;

import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.color.ColorFlag;
import me.hapyl.fight.game.color.GradientColor;

public enum Rarity {

    UNSET(Color.ERROR, "NOT SET", -1, -1),

    COMMON(new Color("#b6dbd1"), "ᴄᴏᴍᴍᴏɴ", 1, 0.35f),
    UNCOMMON(new Color("#12e63d"), "ᴜɴᴄᴏᴍᴍᴏɴ", 2, 0.25f),
    RARE(new Color("#1283db"), "ʀᴀʀᴇ", 5, 0.20f),
    EPIC(new GradientColor("#e314b6", "#ad0789").setFlags(ColorFlag.BOLD), "ᴇᴘɪᴄ", 10, 0.10f),
    LEGENDARY(new GradientColor("#faa61e", "#fa7a1e").setFlags(ColorFlag.BOLD), "ʟᴇɢᴇɴᴅᴀʀʏ", 20, 0.07f),
    MYTHIC(new GradientColor("#8c018c", "#eb13eb").setFlags(ColorFlag.BOLD), "ᴍʏᴛʜɪᴄ", 50, 0.03f);

    private final Color color;
    private final String name;
    private final long defaultPrice;
    private final float dropChance;

    Rarity(Color color, String name, long defaultPrice, float dropChance) {
        this.color = color;
        this.name = name;
        this.defaultPrice = defaultPrice;
        this.dropChance = dropChance;
    }

    public float getDropChance() {
        return dropChance;
    }

    public String getDropChanceString() {
        return "%.1f%%".formatted(dropChance * 100);
    }

    public long getDefaultPrice() {
        return defaultPrice;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public String getNameColored() {
        return color + name;
    }

    @Override
    public String toString() {
        return color.color(name);
    }

}
