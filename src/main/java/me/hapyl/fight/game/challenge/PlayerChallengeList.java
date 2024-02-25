package me.hapyl.fight.game.challenge;

import me.hapyl.fight.database.entry.ChallengeEntry;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.reward.CurrencyReward;
import me.hapyl.fight.util.HoverEventBuilder;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class PlayerChallengeList {

    public static final int DAILY_CHALLENGE_COUNT = 3;

    private final PlayerProfile profile;
    private final PlayerChallenge[] challenges;
    private int currentDay;

    public PlayerChallengeList(PlayerProfile profile) {
        this.profile = profile;
        this.challenges = new PlayerChallenge[DAILY_CHALLENGE_COUNT];
        this.currentDay = -1;

        loadChallenges();
    }

    @Nonnull
    public PlayerChallenge[] getChallenges() {
        return challenges;
    }

    public boolean hasOfType(@Nonnull ChallengeType type) {
        for (PlayerChallenge challenge : challenges) {
            if (challenge != null && challenge.getType() == type) {
                return true;
            }
        }

        return false;
    }

    public boolean canGenerate(@Nonnull ChallengeType type) {
        return type.get().canGenerate(profile);
    }

    public void validateSameDay() {
        final int currentDay = Challenge.getCurrentDay();

        if (this.currentDay == currentDay) {
            return;
        }

        newChallenges(true);
    }

    public boolean hasCompleteAndNonClaimed() {
        for (PlayerChallenge challenge : challenges) {
            if (challenge.isComplete() && !challenge.hasClaimedRewards()) {
                return true;
            }
        }

        return false;
    }

    public void resetBonds() {
        newChallenges(true);
    }

    public int getCurrentDay() {
        return currentDay;
    }

    // either load or generate based on a day
    private void loadChallenges() {
        final ChallengeEntry challengeEntry = profile.getDatabase().challengeEntry;
        final int day = challengeEntry.getDay();
        final long currentDay = Challenge.getCurrentDay();

        // Generate
        if (day == -1 || currentDay != day) {
            newChallenges(true);

            // Reset 'reset'
            challengeEntry.resetResetToday();
            return;
        }

        this.currentDay = challengeEntry.getDay();

        // Load
        for (int i = 0; i < DAILY_CHALLENGE_COUNT; i++) {
            final PlayerChallenge challenge = challengeEntry.loadChallenge(i);

            this.challenges[i] = challenge != null ? challenge : ChallengeType.createPlayerChallenge(1 << 4, this);
        }
    }

    private void newChallenges(boolean notify) {
        this.currentDay = Challenge.getCurrentDay();

        for (int i = 0; i < DAILY_CHALLENGE_COUNT; i++) {
            this.challenges[i] = ChallengeType.createPlayerChallenge(1 << 4, this);
        }

        if (notify) {
            final Player player = profile.getPlayer();

            Chat.sendMessage(player, "");
            Chat.sendMessage(player, " &2&lxx &a&lNEW DAILY BONDS &2&lxx");
            Chat.sendMessage(player, "    &8&oHover for details");
            Chat.sendMessage(player, "");


            for (PlayerChallenge challenge : this.challenges) {
                final ChallengeRarity rarity = challenge.getRarity();
                final HoverEventBuilder hoverEvent = new HoverEventBuilder();

                hoverEvent.append("&a" + challenge.getName());
                hoverEvent.append("&8" + challenge.getRarity().getName());
                hoverEvent.append();

                hoverEvent.append("&7&o" + challenge.getDescription());

                hoverEvent.append();

                hoverEvent.append("&aRewards:");

                final CurrencyReward reward = rarity.getReward();

                hoverEvent.append(" &8+ &6%s Coins".formatted(reward.getCoins()));
                hoverEvent.append(" &8+ &9%s Experience".formatted(reward.getExp()));

                final long rubies = reward.getRubies();

                if (rubies > 0) {
                    hoverEvent.append(" &8+ &4%s Rubies".formatted(rubies));
                }

                Chat.sendHoverableMessage(player, hoverEvent.build(), "  &a+ %s".formatted(challenge.getName()));
            }

            Chat.sendMessage(player, "");

            PlayerLib.playSound(player, Sound.ENTITY_ENDERMAN_HURT, 0.25f);
            PlayerLib.playSound(player, Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 0.75f);
        }
    }

}
