package me.hapyl.fight.gui;

import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.game.reward.DailyReward;
import me.hapyl.fight.game.reward.Reward;
import me.hapyl.fight.game.reward.Rewards;
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
        final boolean canClaimDaily = database.dailyRewardEntry.canClaim();

        final Reward dailyReward = Rewards.DAILY.getReward();
        final ItemBuilder builder = ItemBuilder.of(canClaimDaily ? Material.CHEST_MINECART : Material.MINECART, "&aDaily Reward");

        dailyReward.display(getPlayer(), builder);

        setItem(31, builder.asIcon());

        if (canClaimDaily) {
            setClick(31, player -> {
                dailyReward.grantReward(player);
                update();
            });
        } else {
            setClick(31, player -> {
                Chat.sendMessage(player, "&cCome again in %s to claim!", DailyReward.formatDaily(player));
                PlayerLib.playSound(player, Sound.BLOCK_ANVIL_LAND, 1.0f);

                update();
            });
        }
    }

}
