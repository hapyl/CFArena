package me.hapyl.fight.game.achievement;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.hapyl.eterna.module.util.Compute;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.heroes.tamer.Tamer;
import me.hapyl.fight.registry.Key;
import me.hapyl.fight.registry.KeyFunction;
import me.hapyl.fight.registry.SimpleRegistry;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class AchievementRegistry extends SimpleRegistry<Achievement> {

    /////////////////////////////////
    // *=* Common Achievements *=* //
    /////////////////////////////////
    public final Achievement PLAY_FIRST_GAME;
    public final Achievement FIRST_BLOOD;
    public final Achievement OVERCHARGED;
    public final Achievement SHREDDING_TIME;
    public final Achievement DEFENSELESS;
    public final Achievement COMPLETE_LAMP_PUZZLE;
    public final Achievement BEYOND_CLOUDS;
    public final Achievement RULES_ARE_NOT_FOR_ME;
    public final Achievement AFK;
    public final Achievement I_DONT_WANT_TO_PLAY;
    public final Achievement OWL_SPY;
    public final Achievement FIRST_TRY;
    public final Achievement WIN_GUESS_WHO;
    public final Achievement FORFEIT_GUESS_WHO;
    public final Achievement APRIL_FOOLS;

    /////////////////////////////////
    // *=* Tiered Achievements *=* //
    /////////////////////////////////
    public final Achievement USE_TALENTS;
    public final Achievement USE_ULTIMATES;
    public final Achievement LEVEL_TIERED;
    public final Achievement USE_GADGETS;
    public final Achievement GAIN_COINS;

    ///////////////////////////////////////
    // *=* Hero Related Achievements *=* //
    //////////////////////////////////////
    // >> These must be prefixed with the hero name in declaration
    public final Achievement TROLL_LAUGHING_OUT_LOUD;
    public final Achievement TROLL_LAUGHING_OUT_LOUD_VICTIM;

    public final Achievement FROSTBITE_CAGE_SELF;
    public final Achievement FROSTBITE_CAGE_SELF_OTHER;

    public final Achievement TAMER_FISHING_TIME;

    public final Achievement SHAMAN_TOTEM_OUT_OF_WORLD;

    public final Achievement WITCHER_COMBO;

    public final Achievement BLOODFIEND_THEY_ARE_TWINS_ALRIGHT;

    // This is private
    private final Map<Category, List<Achievement>> byCategory;

    public AchievementRegistry() {
        byCategory = Maps.newHashMap();

        /////////////////////////////////
        // *=* Common Achievements *=* //
        /////////////////////////////////
        PLAY_FIRST_GAME = build("play_first_game", builder -> builder
                .setName("So That's How It Is")
                .setDescription("Play your very first game.")
        );
        FIRST_BLOOD = build("first_blood", builder -> builder
                .setName("First Blood")
                .setDescription("""
                        Cause first blood in a game.
                        
                        &8You or your team.
                        """)
        );
        OVERCHARGED = build("overcharged", builder -> builder
                .setName("Overcharged!!!")
                .setDescription("Use overcharged ultimate for the first time.")
        );
        SHREDDING_TIME = build("shredding_time", builder -> builder
                .setName("Shredding Time!")
                .setDescription("Get tear to shred by a certain turbine.")
                .setSecret(true)
        );
        DEFENSELESS = register("defenseless", DefenselessAchievement::new);
        COMPLETE_LAMP_PUZZLE = build("complete_lamp_puzzle", builder -> builder.
                setName("Light Them Up!")
                .setDescription("Complete the lamp puzzle in the lobby.")
        );
        BEYOND_CLOUDS = build("beyond_clouds", builder -> builder
                .setName("Beyond Clouds")
                .setDescription("Die from falling out of a certain kingdom in the clouds.")
                .setSecret(true)
        );
        RULES_ARE_NOT_FOR_ME = build("rules_are_not_for_me", builder -> builder
                .setName("Rules Aren't For Me")
                .setDescription("Use any other than explained click in hero selection.")
                .setSecret(true)
        );
        AFK = build("afk", builder -> builder
                .setName("Be Right Back!")
                .setDescription("Go AFK in the middle of the game.")
                .setSecret(true)
        );
        I_DONT_WANT_TO_PLAY = build("i_dont_want_to_play", builder -> builder
                .setName("I DONT WANT TO PLAY!!!")
                .setDescription("Let everyone know that you don't want to play right now.")
                .setSecret(true)
        );
        OWL_SPY = build("owl_spy", builder -> builder
                .setName("Owl Spy")
                .setDescription("Find all the hidden owls at the winery in the single game.")
                .setSecret(true)
        );
        FIRST_TRY = build("first_try", builder -> builder
                .setName("First Try!")
                .setDescription("Hit all the target blocks in your first try.")
                .setSecret(true)
        );
        WIN_GUESS_WHO = build("win_guess_who", builder -> builder
                .setName("Pro Guesser")
                .setDescription("Win a game of Guess Who.")
        );
        FORFEIT_GUESS_WHO = build("forfeit_guess_who", builder -> builder
                .setName("I'm Not a Chicken!")
                .setDescription("Forfeit a game of Guess Who!")
                .setSecret(true)
        );
        APRIL_FOOLS = build("april_fools", builder -> builder
                .setName("April Fools!")
                .setDescription("Join the sever on april fools week.")
                .setSecret(true)
        );

        /////////////////////////////////
        // *=* Tiered Achievements *=* //
        /////////////////////////////////
        USE_TALENTS = tiered("use_talents",
                "Talent Mastery",
                "Use any talent {} times.",
                10, 100, 1000, 5000, 10000
        );
        USE_ULTIMATES = tiered("use_ultimates",
                "Ultimate Showdown",
                "Use your ultimate {} times.",
                5, 50, 100, 500, 1000
        );
        LEVEL_TIERED = tiered("level_tiered",
                "Mountain Climber",
                "Reach level {}.",
                10, 20, 30, 40, 50
        );
        USE_GADGETS = tiered("use_gadgets",
                "Playful Nature",
                "Use gadgets {} times.",
                100, 500, 1_000, 1_500, 2_000
        );
        GAIN_COINS = tiered("gain_coins",
                "Oh, the Riches!",
                "Obtain {} coins.",
                1_000, 10_000, 100_000, 500_000, 1_000_000
        );

        ///////////////////////////////////////
        // *=* Hero Related Achievements *=* //
        ///////////////////////////////////////
        TROLL_LAUGHING_OUT_LOUD = build("troll_laughing_out_loud", builder -> builder
                .setName("LOL!")
                .setDescription("Perform a special troll technique to instantly annihilate your opponent!")
                .setSecret(true)
                .setHeroSpecific(HeroRegistry.TROLL)
        );

        TROLL_LAUGHING_OUT_LOUD_VICTIM = build("troll_laughing_out_loud_victim", builder -> builder
                .setName("That's Not Fair!")
                .setDescription("Get killed by Troll's passive ability.")
                .setSecret(true) // Don't make this one hero specific, please
        );

        FROSTBITE_CAGE_SELF = build("frostbite_cage_self", builder -> builder
                .setName("Ice Cold")
                .setDescription("Cage yourself or your teammate with your own snowball.")
                .setSecret(true)
                .setHeroSpecific(HeroRegistry.FREAZLY)
        );

        FROSTBITE_CAGE_SELF_OTHER = build("frostbite_cage_self_other", builder -> builder
                .setName("Bing Chilling")
                .setDescription("Get caged by your own teammate.")
                .setSecret(true)
        );

        TAMER_FISHING_TIME = build("tamer_fishing_time", builder -> builder
                .setName("Fishing Time!")
                .setDescription("Go fishing in the middle of the game.")
                .setSecret(true)
                .setHeroSpecific(HeroRegistry.TAMER)
        );

        SHAMAN_TOTEM_OUT_OF_WORLD = build("shaman_totem_out_of_world", builder -> builder
                .setName("MY TOTEM!!")
                .setDescription("Throw a totem outside this world.")
                .setSecret(true)
                .setHeroSpecific(HeroRegistry.SHAMAN)
        );

        WITCHER_COMBO = build("witcher_combo", builder -> builder
                .setName("C-c-combo!")
                .setDescription("Get a combo streak of sixteen or higher.")
                .setSecret(true)
                .setHeroSpecific(HeroRegistry.WITCHER)
        );

        BLOODFIEND_THEY_ARE_TWINS_ALRIGHT = build("they_are_twins_alright", builder -> builder
                .setName("They're Twins Alright!")
                .setDescription("Hit two enemies with Twin Claws at the same time.")
                .setHeroSpecific(HeroRegistry.BLOODFIEND)
                .setPointReward(20)
        );

    }

    @Nonnull
    public List<String> listIds() {
        return registered.keySet().stream().map(Key::getKey).toList();
    }

    /**
     * Returns copy of all achievements in a category.
     *
     * @param category - Category to get achievements from.
     * @return List of achievements in category.
     */
    @Nonnull
    public LinkedList<Achievement> byCategory(Category category, boolean progressive) {
        final LinkedList<Achievement> achievements = Lists.newLinkedList(byCategory.getOrDefault(category, Lists.newArrayList()));

        // remove non-progressive
        if (progressive) {
            achievements.removeIf(achievement -> !achievement.isProgressive());
        }
        else {
            achievements.removeIf(Achievement::isProgressive);
        }

        return achievements;
    }

    @Nonnull
    public LinkedList<Achievement> byCategory(Category category) {
        return Lists.newLinkedList(byCategory.getOrDefault(category, Lists.newArrayList()));
    }

    @Override
    @Deprecated
    public boolean unregister(@Nonnull Achievement achievement) {
        throw new UnsupportedOperationException("Cannot unregister achievement!");
    }

    public Achievement build(@Nonnull String key, @Nonnull Consumer<Achievement.Builder> consumer) {
        return register(key, k -> {
            final Achievement.Builder builder = Achievement.builder(k);
            consumer.accept(builder);

            return builder.build();
        });
    }

    public Achievement tiered(@Nonnull String key, @Nonnull String name, @Nonnull String description, int tier1, int tier2, int tier3, int tier4, int tier5) {
        return register(key, k -> new TieredAchievement(k, name, description, tier1, tier2, tier3, tier4, tier5));
    }

    @Override
    public Achievement register(@Nonnull String key, @Nonnull KeyFunction<Achievement> fn) {
        final Achievement achievement = super.register(key, fn);

        byCategory.compute(achievement.getCategory(), Compute.listAdd(achievement));
        return achievement;
    }

}
