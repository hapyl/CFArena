package me.hapyl.fight.game.achievement;

import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.fight.trigger.Triggers;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public enum Achievements {

    PLAY_FIRST_GAME("So That's How It Is", "Play your very first game."),
    FIRST_BLOOD("First Blood", "Cause first blood in a game.__&8You or your team."),

    /////////////////////////
    // TIERED ACHIEVEMENTS //
    /////////////////////////
    USE_TALENTS(
            new TieredAchievement(
                    "Talent Mastery",
                    "Use any talent {} times.",
                    10, 100, 1000, 5000, 10000
            )
    ),

    USE_ULTIMATES(
            new TieredAchievement(
                    "Ultimate Showdown",
                    "Use your ultimate {} times.",
                    5, 50, 100, 500, 1000
            )
    ),

    TEST_TIERED_ACHIEVEMENT(
            new TieredAchievement(
                    "test test blah blah blah",
                    "remove me {} times",
                    1, 5, 10, 15, 20
            )
    ),

    ///////////////////////////////
    // HERO RELATED ACHIEVEMENTS //
    ///////////////////////////////

    // Troll
    LAUGHING_OUT_LOUD(
            new HiddenAchievement(
                    "LOL!",
                    "Perform a special troll technique to instantly annihilate your opponent!"
            )
    ),

    LAUGHING_OUT_LOUD_VICTIM(
            new HiddenAchievement("That's Not Fair!", "Get killed by Troll's passive ability.")
    ),

    ////////////////////////
    // OTHER ACHIEVEMENTS //
    ////////////////////////

    SHREDDING_TIME(
            new HiddenAchievement("Shredding Time!", "Get tear to shred by a certain turbine.")
    ),

    DEFENSELESS(
            new HiddenAchievement("Defenseless", "Get 0 and less defense.")
                    .setTrigger(Triggers.ATTRIBUTE_CHANGE, trigger -> {
                        return trigger.type == AttributeType.DEFENSE && trigger.newValue <= 0.0d;
                    })
    ),

    COMPLETE_LAMP_PUZZLE(
            new Achievement("Light Them Up!", "Complete the lamp puzzle in the lobby.")
    ),

    BEYOND_CLOUDS(
            new HiddenAchievement("Beyond Clouds", "Die from falling out of a certain kingdom in the clouds.")
    ),

    THEY_ARE_TWINS_ALRIGHT(
            new Achievement("They're Twins Alright!", "Hit two enemies with Twin Claws at the same time.")
                    .setPointReward(20)
    ),

    RULES_ARE_NOT_FOR_ME(
            new HiddenAchievement("Rules Aren't For Me", "Use any other than explained click in hero selection.")
    ),

    AFK(
            new HiddenAchievement("Be Right Back!", "Go AFK in the middle of the game.")
    ),

    I_DONT_WANT_TO_PLAY(
            new HiddenAchievement("I DONT WANT TO PLAY!!!", "Let everyone know that you don't want to play right now.")
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

    public boolean complete(GamePlayer player) {
        return complete(player.getPlayer());
    }

    public boolean complete(Player player) {
        return achievement.complete(player);
    }

    public void complete(GameTeam team) {
        achievement.completeAll(team);
    }

}
