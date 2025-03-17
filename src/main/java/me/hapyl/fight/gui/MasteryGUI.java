package me.hapyl.fight.gui;

import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.fight.CF;
import me.hapyl.fight.database.entry.MasteryEntry;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.mastery.HeroMastery;
import me.hapyl.fight.game.heroes.mastery.HeroMasteryLevel;
import me.hapyl.fight.gui.styled.ReturnData;
import me.hapyl.fight.gui.styled.Size;
import me.hapyl.fight.gui.styled.StyledGUI;
import me.hapyl.fight.gui.styled.StyledTexture;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public class MasteryGUI extends StyledGUI {

    private static final int[] SLOTS = {
            20, 21, 22, 23, 24
    };

    private static final String[] crownTextures = {
            "5a559548dacceefb14ee938de2cd76a3d0c23ce053041bf48780088dda6d4c18",
            "b1c05899f17090dc600c5dfc02b5a0d56cfa0ce310b68dafcd679b2002d29abf",
            "c0c0023bf8d8721e0229dafdfb9d83eeea4d4b6e1f2dd1490120c1cc117cbc78",
            "39b9882b352312fadba9d839a37f7bf5a1a893fd60b6eec8aaf1b4245032e676",
            "cb87730de20a38806d593aa888573918a367ab9c59368c9f7e8973a3601a688a",
    };

    private final Hero hero;
    private final int returnPage;

    public MasteryGUI(Player player, Hero hero, int returnPage) {
        super(player, "Mastery - " + hero.getName(), Size.FOUR);

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
        setHeader(StyledTexture.ICON_MASTERY.asIcon());

        final HeroMastery mastery = hero.getMastery();
        final MasteryEntry entry = CF.getDatabase(player).masteryEntry;

        final int level = entry.getLevel(hero);

        int index = 0;
        for (HeroMasteryLevel masteryLevel : mastery) {
            final int slot = SLOTS[index++];

            final ItemBuilder builder = ItemBuilder.playerHeadUrl(crownTexture(index)) // FIXME (Sat, Feb 8 2025 @xanyjl): Why is this honey
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

    private static String crownTexture(int index) {
        if (index < 0 || index > HeroMastery.MAX_LEVEL) {
            return "null";
        }

        return crownTextures[index - 1];
    }

}
