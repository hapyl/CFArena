package me.hapyl.fight.game.achievement;

import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.reward.CurrencyReward;
import me.hapyl.fight.game.reward.Reward;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.fight.trigger.Triggers;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public enum Achievements {

    PLAY_FIRST_GAME("So That's How It Is", "Play your very first game."),
    FIRST_BLOOD("First Blood", "Cause first blood in a game.__&8You or your team"),

    ///////////////////////////
    // PROGRESS ACHIEVEMENTS //
    ///////////////////////////
    USE_TALENTS(
            new ProgressAchievement(
                    "Master of Talents",
                    "Use talents {} times.",
                    10, 100, 500, 1_000, 5_000, 10_000
            ).forEachRequirement((achievement, i) -> achievement.setReward(i, Reward.currency().withCoins(50L * i).withExp(i / 10)))
    ),

    USE_ULTIMATES(
            new ProgressAchievement(
                    "Ultimate Showdown",
                    "Use ultimate {} times.",
                    5, 10, 50, 100, 500, 1_000
            ).forEachRequirement((achievement, i) -> achievement.setReward(i, Reward.currency().withCoins(i * 100).withExp(i / 5)))
    ),

    ///////////////////////////////
    // HERO RELATED ACHIEVEMENTS //
    ///////////////////////////////

    // Troll
    LAUGHING_OUT_LOUD(
            new HiddenAchievement(
                    "LOL!",
                    "Perform a special troll technique to instantly annihilate your opponent!"
            ).setReward(CurrencyReward.create().withCoins(1000))
    ),

    LAUGHING_OUT_LOUD_VICTIM(
            new HiddenAchievement("That's Not Fair!", "Get killed by Troll's passive ability.")
                    .setReward(Reward.currency().withCoins(1000))
    ),

    ////////////////////////
    // OTHER ACHIEVEMENTS //
    ////////////////////////

    SHREDDING_TIME(
            new HiddenAchievement("Shredding Time!", "Get tear to shred by a certain turbine.")
                    .setReward(Reward.currency().withCoins(1500))
    ),

    DEFENSELESS(
            new HiddenAchievement("Defenseless", "Get 0 and less defense.")
                    .setReward(Reward.currency().withCoins(5000))
                    .setTrigger(Triggers.ATTRIBUTE_CHANGE, trigger -> {
                        return trigger.type == AttributeType.DEFENSE && trigger.newValue <= 0.0d;
                    })
    ),

    COMPLETE_LAMP_PUZZLE(
            new Achievement("Light Them Up!", "Complete the lamp puzzle in the lobby.")
                    .setReward(Reward.currency().withCoins(2500))
    ),

    BEYOND_CLOUDS(
            new HiddenAchievement("Beyond Clouds", "Dei from falling out of the clouds.")
                    .setReward(Reward.currency().withCoins(1000))
    ),

    ;

    public final Achievement achievement;

    Achievements(@Nonnull Achievement achievement) {
        this.achievement = achievement;
        this.achievement.setId(name());
    }

    Achievements(@Nonnull String name, @Nonnull String description) {
        this(new Achievement(name, description));
    }

    public boolean hasCompletedAtLeastOnce(Player player) {
        return achievement.hasCompletedAtLeastOnce(player);
    }

    public boolean complete(Player player) {
        return achievement.complete(player);
    }

    public void complete(GameTeam team) {
        achievement.completeAll(team);
    }

}
