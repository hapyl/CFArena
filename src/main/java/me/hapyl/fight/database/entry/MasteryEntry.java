package me.hapyl.fight.database.entry;

import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.StrictPlayerDatabaseEntry;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.mastery.HeroMastery;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class MasteryEntry extends StrictPlayerDatabaseEntry {

    private final static int BAR_LENGTH = 15;

    public MasteryEntry(PlayerDatabase playerDatabase) {
        super(playerDatabase, "mastery");
        setPath("mastery");
    }

    public final int getLevel(@Nonnull Hero hero) {
        return HeroMastery.getLevel(getExp(hero));
    }

    public final String getLevelString(@Nonnull Hero hero) {
        return HeroMastery.getLevelString(getLevel(hero));
    }

    public final long getExp(@Nonnull Hero hero) {
        return getValue("%s.exp".formatted(hero.getKeyAsString()), 0L);
    }

    public void addExp(@Nonnull Hero hero, long expToAdd) {
        setExp(hero, getExp(hero) + Math.abs(expToAdd));
    }

    public void setExp(@Nonnull Hero hero, long newExp) {
        final long currentExp = getExp(hero);
        final int currentLevel = HeroMastery.getLevel(currentExp);
        final int newLevel = HeroMastery.getLevel(newExp);

        // Play level up animation
        if (newLevel > currentLevel) {
            playMasteryLevelUpEffect(hero, currentLevel, newLevel);
        }

        setValue("%s.exp".formatted(hero.getKeyAsString()), newExp);
    }

    public void playMasteryLevelUpEffect(@Nonnull Hero hero, int currentLevel, int newLevel) {
        final Player player = getOnlinePlayer();

        if (player == null) {
            return;
        }

        Chat.sendMessage(player, "");
        Chat.sendCenterMessage(player, HeroMastery.PREFIX);
        Chat.sendCenterMessage(player, ChatColor.GOLD + hero.getNameSmallCaps());
        Chat.sendCenterMessage(player, "&8[&7%s&8] &f➠&7 &7[&a&l%s&7]".formatted(
                HeroMastery.getLevelString(currentLevel),
                HeroMastery.getLevelString(newLevel)
        ));
        Chat.sendMessage(player, "");

        PlayerLib.playSound(player, Sound.UI_TOAST_CHALLENGE_COMPLETE, 2.0f);
    }

    @Nonnull
    public String makeProgressBar(@Nonnull Hero hero) {
        final int level = getLevel(hero);

        final long exp = getExp(hero);
        final long expForThisLevel = HeroMastery.getExpRequiredForLevel(level);
        final long expForNextLevel = HeroMastery.getExpRequiredForLevel(level + 1);

        if (expForNextLevel == Long.MAX_VALUE) {
            return ChatColor.YELLOW + "-".repeat(BAR_LENGTH) + " &2&lMAXED!";
        }
        else {
            final float percentToNextLevel = (float) (exp - expForThisLevel) / (expForNextLevel - expForThisLevel);
            final int percentBar = (int) Math.ceil(percentToNextLevel * BAR_LENGTH);

            return ""
                    + (ChatColor.GREEN + "-".repeat(percentBar))
                    + (ChatColor.GRAY + "-".repeat(BAR_LENGTH - percentBar))
                    + (ChatColor.DARK_GREEN + " %.0f%%".formatted(percentToNextLevel * 100));
        }

    }

    @Nonnull
    public String makeMasteryHeader(@Nonnull Hero hero) {
        return "&6&l\uD83C\uDFC5 ᴍᴀꜱᴛᴇʀʏ &8(&e%s&8)".formatted(getLevelString(hero));
    }
}
