package me.hapyl.fight.gui.styled.eye;

import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.entry.DailyRewardEntry;
import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.reward.DailyReward;
import me.hapyl.fight.gui.styled.Size;
import me.hapyl.fight.gui.styled.StyledGUI;
import me.hapyl.fight.gui.styled.StyledTexture;
import me.hapyl.fight.ux.Notifier;
import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.player.PlayerLib;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class EyeGUI extends StyledGUI {

    public EyeGUI(Player player) {
        super(player, "The Eye", Size.FIVE);

        openInventory();
    }

    @Override
    public void onUpdate() {
        final PlayerDatabase database = PlayerDatabase.getDatabase(getPlayer());
        final PlayerRank playerRank = database.getRank();

        setHeader(StyledTexture.THE_EYE.asIcon());

        setItem(20, StyledTexture.RELIC_HUNT.asButton("view relics"), RelicHuntGUI::new);
        setItem(22, StyledTexture.DAILY.asButton("view bonds"), DailyGUI::new);
        setItem(24, StyledTexture.QUESTION.asIcon());

        // Daily rewards
        final DailyRewardEntry rewardEntry = database.dailyRewardEntry;

        int slot = 30;
        for (DailyRewardEntry.Type type : DailyRewardEntry.Type.values()) {
            final ItemBuilder builder = type.texture.toBuilder();
            final DailyReward reward = type.reward;
            final PlayerRank rankRequired = type.rank;

            if (reward == null) {
                Notifier.error(player, "Error loading rewards, try again before reporting this!");
                closeInventory();
                return;
            }

            builder.setName("&aDaily Reward");
            builder.addLore("&8" + Chat.capitalize(type));
            builder.addLore();

            final boolean canClaim = rewardEntry.canClaim(type);
            final String comeBackString = Color.ERROR + "Come back in %s.".formatted(reward.format(player));

            builder.addLore(canClaim ? "Today's Rewards:" : "Tomorrow's Rewards:");

            reward.formatBuilder(player, builder);

            // Streak
            final int streak = rewardEntry.getStreak(type);

            builder.addLore();
            builder.addLore("Current Streak: &b%s %s!".formatted(streak, streak == 1 ? "day" : "days"));
            builder.addLore();

            final String lowRankString =
                    Color.ERROR + "You must be at least " + rankRequired.getPrefixWithFallback() + Color.ERROR + " to claim!";

            if (canClaim) {
                if (!playerRank.isOrHigher(rankRequired)) {
                    builder.addLore(lowRankString);
                }
                else {
                    builder.addLore(Color.BUTTON + "Click to claim!");
                }
            }
            else {
                builder.addLore(Color.ERROR + "Already claimed today!");
                builder.addLore(comeBackString);
            }

            setItem(slot, builder.asIcon());

            if (canClaim) {
                setClick(slot, click -> {
                    if (!playerRank.isOrHigher(rankRequired)) {
                        Notifier.error(player, lowRankString);
                        PlayerLib.villagerNo(player);
                    }
                    else {
                        reward.grant(player);
                    }

                    update();
                });
            }
            else {
                setClick(slot, click -> {
                    Chat.sendMessage(player, comeBackString);
                    PlayerLib.playSound(player, Sound.BLOCK_ANVIL_LAND, 1.0f);

                    update();
                });
            }

            slot++;
        }
    }

}
