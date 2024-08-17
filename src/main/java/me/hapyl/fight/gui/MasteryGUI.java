package me.hapyl.fight.gui;

import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.entry.MasteryEntry;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.mastery.HeroMastery;
import me.hapyl.fight.game.heroes.mastery.HeroMasteryLevel;
import me.hapyl.fight.gui.styled.ReturnData;
import me.hapyl.fight.gui.styled.Size;
import me.hapyl.fight.gui.styled.StyledGUI;
import me.hapyl.fight.gui.styled.StyledItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public class MasteryGUI extends StyledGUI {

    private static final int[] SLOTS = {
            11, 13, 15, 19, 21, 23, 25, 29, 31, 33
    };

    private final Hero hero;
    private final int returnPage;

    public MasteryGUI(Player player, Hero hero, int returnPage) {
        super(player, "Mastery " + hero.getName(), Size.FOUR);

        this.hero = hero;
        this.returnPage = returnPage;

        openInventory();
    }

    @Nullable
    @Override
    public ReturnData getReturnData() {
        return ReturnData.of("Hero Preview - " + hero.getName(), player -> new HeroPreviewGUI(player, hero, returnPage));
    }

    @Override
    public void onUpdate() {
        setHeader(StyledItem.ICON_MASTERY.asIcon());

        final HeroMastery mastery = hero.getMastery();
        final MasteryEntry entry = PlayerDatabase.getDatabase(player).masteryEntry;

        final int level = entry.getLevel(hero);

        int index = 0;
        for (HeroMasteryLevel masteryLevel : mastery) {
            final int slot = SLOTS[index++];

            final ItemBuilder builder = new ItemBuilder(Material.HONEYCOMB)
                    .setName(masteryLevel.getName())
                    .setAmount(index)
                    .addLore(ChatColor.DARK_GRAY + HeroMastery.getLevelDisplay(index).string())
                    .addLore();

            builder.addTextBlockLore(masteryLevel.getDescription());
            builder.addLore();

            if (level >= masteryLevel.getLevel()) {
                builder.addLore("&a&lUNLOCKED!");
                builder.glow();
            }
            // Next level
            else if (level == masteryLevel.getLevel() - 1) {
                final long exp = entry.getExp(hero);
                final long expForThisLevel = HeroMastery.getExpRequiredForLevel(level);
                final long expForNextLevel = HeroMastery.getExpRequiredForLevel(level + 1);

                builder.addLore(Color.DEFAULT + "Progress: &8(%s&8)".formatted(
                        Chat.makeStringFractional((int) (exp - expForThisLevel), (int) (expForNextLevel - expForThisLevel)))
                );
                builder.addLore(entry.makeProgressBar(hero));
            }
            else {
                builder.addLore("&cUnlock previous level first!");
            }

            setItem(slot, builder.asIcon());
        }

        // Fx
        PlayerLib.playSound(player, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 0.75f);
    }

}
