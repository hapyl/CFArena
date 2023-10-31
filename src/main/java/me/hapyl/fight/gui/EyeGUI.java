package me.hapyl.fight.gui;

import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.entry.DailyRewardEntry;
import me.hapyl.fight.game.collectible.relic.Type;
import me.hapyl.fight.game.reward.DailyReward;
import me.hapyl.fight.game.reward.Reward;
import me.hapyl.fight.game.reward.Rewards;
import me.hapyl.fight.util.ItemStacks;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.inventory.gui.Arguments;
import me.hapyl.spigotutils.module.inventory.gui.PlayerDynamicGUI;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class EyeGUI extends PlayerDynamicGUI {

    public EyeGUI(Player player) {
        super(player, "The Eye", 5);

        openInventory();
    }

    @Override
    public void setupInventory(@Nonnull Arguments arguments) {
        final PlayerDatabase database = PlayerDatabase.getDatabase(getPlayer());

        // Relic Hunt
        setItem(11, ItemBuilder.of(Material.PLAYER_HEAD, "Relic Hunt")
                .addSmartLore("There are relics scattered all around the world. Try to find them all to get unique rewards!")
                .setHeadTextureUrl(Type.AMETHYST.getTexture())
                .addLore()
                .addLore("&eClick to browse")
                .asIcon(), RelicHuntGUI::new);

        // Daily Reward
        final DailyRewardEntry rewardEntry = database.dailyRewardEntry;
        final boolean canClaimDaily = rewardEntry.canClaim();
        final Reward dailyReward = Rewards.DAILY.getReward();
        final ItemBuilder builder = ItemBuilder.of(canClaimDaily ? Material.CHEST_MINECART : Material.MINECART, "&aDaily Reward");

        builder.addLore(canClaimDaily ? "Today's Rewards:" : "Tomorrow's Rewards:");

        dailyReward.formatBuilder(getPlayer(), builder);

        builder.addLore();
        builder.addLore("&7Current streak: &a%s &7days", rewardEntry.getStreak());
        builder.addLore();

        if (canClaimDaily) {
            builder.addLore("&eClick to claim!");
        }
        else {
            builder.addLore("&eAlready claimed today!");
            builder.addLore("&eCome again in %s to claim.", DailyReward.formatDaily(player));
        }

        setItem(31, builder.asIcon());

        if (canClaimDaily) {
            setClick(31, player -> {
                dailyReward.grant(player);
                update();
            });
        }
        else {
            setClick(31, player -> {
                Chat.sendMessage(player, "&cCome again in %s to claim!", DailyReward.formatDaily(player));
                PlayerLib.playSound(player, Sound.BLOCK_ANVIL_LAND, 1.0f);

                update();
            });
        }

        // Reserved
        setItem(13, ItemStacks.OAK_QUESTION);
        setItem(15, ItemStacks.OAK_QUESTION);
    }

}
