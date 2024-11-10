package me.hapyl.fight.gui.styled.eye;

import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.eterna.module.util.TimeFormat;
import me.hapyl.fight.CF;
import me.hapyl.fight.Notifier;
import me.hapyl.fight.game.challenge.Challenge;
import me.hapyl.fight.game.challenge.ChallengeRarity;
import me.hapyl.fight.game.challenge.PlayerChallenge;
import me.hapyl.fight.game.challenge.PlayerChallengeList;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.reward.Reward;
import me.hapyl.fight.game.reward.RewardDescription;
import me.hapyl.fight.gui.styled.ReturnData;
import me.hapyl.fight.gui.styled.Size;
import me.hapyl.fight.gui.styled.StyledGUI;
import me.hapyl.fight.gui.styled.StyledTexture;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public class DailyGUI extends StyledGUI {

    private final PlayerProfile profile;
    private final PlayerChallengeList challengeList;

    public DailyGUI(Player player) {
        super(player, "Daily Bonds", Size.FIVE);

        this.profile = CF.getProfile(player);
        this.challengeList = profile.getChallengeList();

        openInventory();
    }

    @Nullable
    @Override
    public ReturnData getReturnData() {
        return ReturnData.of("The Eye", EyeGUI::new);
    }

    @Override
    public void onUpdate() {
        setHeader(StyledTexture.DAILY.asIcon());

        final PlayerChallenge[] challenges = challengeList.getChallenges();

        int slot = 20;
        for (PlayerChallenge playerChallenge : challenges) {
            final Challenge challenge = playerChallenge.getType().getWrapped();
            final ChallengeRarity rarity = challenge.getRarity();
            final boolean complete = playerChallenge.isComplete();
            final boolean hasClaimedRewards = playerChallenge.hasClaimedRewards();

            // Icon
            setItem(slot, new ItemBuilder(complete ? Material.WRITTEN_BOOK : Material.WRITABLE_BOOK)
                    .setName(challenge.getName())
                    .addLore("&8" + rarity.getName())
                    .addLore()
                    .addSmartLore(challenge.getDescription(playerChallenge), "&7&o")
                    .addLore()
                    .addLore("Progress: " + playerChallenge.getProgressString())
                    .addLore("&cExpires in " + TimeFormat.format(Challenge.getTimeUntilReset(), TimeFormat.HOURS, TimeFormat.MINUTES))
                    .asIcon()
            );

            // Reward
            final ItemBuilder builder = new ItemBuilder(hasClaimedRewards ? Material.MINECART : Material.CHEST_MINECART)
                    .setName(Color.GREEN + challenge.getName() + " Rewards")
                    .addLore();

            final Reward reward = rarity.getReward();
            final RewardDescription display = reward.getDescription(player);
            final int rewardsSlot = slot + 9;

            display.forEach(builder::addLore);
            builder.addLore();

            if (hasClaimedRewards) {
                builder.addLore(Color.ERROR + "Already claimed!");

                setItem(rewardsSlot, builder.asIcon(), player -> {
                    Notifier.error(player, "Already claimed!");
                    PlayerLib.playSound(player, Sound.BLOCK_ANVIL_LAND, 1.0f);
                });
            }
            else {
                if (!complete) {
                    builder.addLore(Color.ERROR + "Cannot claim yet!");

                    setItem(rewardsSlot, builder.asIcon(), player -> {
                        Notifier.error(player, "You cannot claim this yet!");
                        PlayerLib.playSound(player, Sound.BLOCK_ANVIL_LAND, 1.0f);
                    });
                }
                else {
                    builder.glow();
                    builder.addLore(Color.BUTTON + "Click to claim!");

                    setItem(rewardsSlot, builder.asIcon(), player -> {
                        if (playerChallenge.hasClaimedRewards()) {
                            Notifier.error(player, "You have already claimed this reward! If you haven't, report this!");
                            player.closeInventory();
                            return;
                        }

                        playerChallenge.setHasClaimedRewards(true);

                        reward.grant(player);

                        final RewardDescription description = reward.getDescription(player);

                        Notifier.success(player, "Claimed bond rewards:");
                        description.forEach(player, Notifier::info);

                        Notifier.sound(player, Sound.ENTITY_PLAYER_LEVELUP, 2.0f);

                        update();
                    });
                }
            }

            slot += 2;
        }

        final boolean hasResetToday = profile.getDatabase().challengeEntry.hasResetToday();

        // Reset daily
        final ItemBuilder builder = new ItemBuilder(Material.RED_GLAZED_TERRACOTTA)
                .setName("&cReset Bonds")
                .addLore()
                .addTextBlockLore("""
                        &7&o;;Not happy with today's bonds?
                        
                        &7&o;;Well, for a small price, we can always change them!
                        """)
                .addLore();

        if (hasResetToday) {
            setItem(52, builder.addLore(Color.ERROR + "You have already set today's bonds!").asIcon(),
                    player -> {
                        Notifier.error(player, Color.ERROR + "You have already reset today's bonds!");
                        PlayerLib.villagerNo(player);
                    }
            );
        }
        else {
            setItem(
                    52,
                    builder
                            .addLore(Color.BUTTON + "Click to open reset GUI!")
                            .asIcon(), DailyResetGUI::new
            );
        }
    }
}
