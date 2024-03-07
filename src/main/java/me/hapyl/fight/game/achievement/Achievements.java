package me.hapyl.fight.game.achievement;

import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.fight.trigger.Triggers;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

// This enum is not a registry! (wow) Rather a wrapper.
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

    LEVEL_TIERED(
            new TieredAchievement(
                    "Mountain Climber",
                    "Reach level {}.",
                    10, 20, 30, 40, 50
            )
    ),

    USE_GADGETS(
            new TieredAchievement(
                    "Playful Nature",
                    "Use gadgets {} times.",
                    100, 500, 1_000, 1_500, 2_000
            )
    ),

    GAIN_COINS(
            new TieredAchievement(
                    "Oh, the Riches!",
                    "Obtain {} coins.",
                    1_000, 10_000, 100_000, 500_000, 1_000_000
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

    // Frostbite
    CAGE_SELF(
            new HiddenAchievement("Ice Cold", "Cage yourself or your teammate with your own snowball.")
    ),

    CAGE_SELF_OTHER(
            new HiddenAchievement("Bing Chilling", "Get caged by your own teammate.")
    ),

    // Tamer
    FISHING_TIME(
            new HiddenAchievement("Fishing Time", "Go fishing in the middle of the game.")
    ),

    // Shaman
    TOTEM_OUT_OF_WORLD(
            new HiddenAchievement("MY TOTEM!!", "Throw a totem outside this world.")
    ),

    // Witcher
    COMBO(
            new HiddenAchievement("C-c-combo!", "Get a combo streak of twenty or higher.")
    ),

    ////////////////////////
    // OTHER ACHIEVEMENTS //
    ////////////////////////

    SHREDDING_TIME(
            new HiddenAchievement("Shredding Time!", "Get tear to shred by a certain turbine.")
    ),

    DEFENSELESS(
            new HiddenAchievement("Defenseless", "Get zero and less defense.")
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

    OWL_SPY(
            new HiddenAchievement("Owl Spy", "Find all the hidden owls at the winery in the single game.")
    ),

    FIRST_TRY(
            new HiddenAchievement("First Try!", "Hit all the target blocks in your first try.")
    ),

    WIN_GUESS_WHO(
            new Achievement("Pro Guesser", "Win a game of Guess Who.")
    ),

    FORFEIT_GUESS_WHO(
            new HiddenAchievement("I'm Not a Chicken!", "Forfeit a game of Guess Who!")
    ),

    APRIL_FOOLS(
            new HiddenAchievement("April Fools!", "Join the sever on april fools week.")
    ),

    ;

    public final Achievement achievement;

    Achievements(@Nonnull Achievement achievement) {
        this.achievement = achievement;
        this.achievement.setId(name().toLowerCase());
    }

    Achievements(@Nonnull String name, @Nonnull String description) {
        this(new Achievement(name, description));
    }

    public boolean hasCompletedAtLeastOnce(GamePlayer player) {
        return hasCompletedAtLeastOnce(player.getPlayer());
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

    public void setProgress(Player player, int progress) {
        achievement.setCompleteCount(player, progress);
    }

    public void addProgress(Player player, int progress) {
        achievement.setCompleteCount(player, achievement.getCompleteCount(player) + progress);
    }
}
