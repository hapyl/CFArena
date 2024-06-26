package me.hapyl.fight.game.challenge;

import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.entry.CollectibleEntry;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Archetype;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.util.EnumWrapper;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.util.Enums;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.A;

import javax.annotation.Nonnull;

public enum ChallengeType implements EnumWrapper<Challenge> {

    KILL_ENEMIES(
            new Challenge("Swift Slayer", "Eliminate {} players.")
                    .setMin(4)
                    .setMax(8)
    ),

    WIN_GAMES(
            new Challenge("Chicken Dinner!", "Win {} games.")
                    .setMax(1)
    ),

    USE_TALENTS(
            new Challenge("Talented", "Use talents {} times.")
                    .setMin(10)
                    .setMax(20)
    ),

    USE_ULTIMATES(
            new Challenge("Ultimate Showdown", "Use ultimate {} times.")
                    .setMin(2)
                    .setMax(10)
    ),

    PLAY_GAMES(
            new Challenge("Ready Player One", "Play {} games.")
                    .setMin(2)
                    .setMax(3)
    ),

    PLAY_GUESS_WHO(
            new Challenge("The Guesser", "Play a game of Guess Who.")
                    .setMax(1)
    ),

    // Archetype related bonds, have to be hard codded because bonds are enums
    PLAY_HERO_DAMAGE(new ArchetypeChallenge("Brute Force", Archetype.DAMAGE)),
    PLAY_HERO_RANGE(new ArchetypeChallenge("Bullseye", Archetype.RANGE)),
    PLAY_HERO_MOBILITY(new ArchetypeChallenge("I'm Fast Boi", Archetype.MOBILITY)),
    PLAY_HERO_STRATEGY(new ArchetypeChallenge("The Thinker", Archetype.STRATEGY)),
    PLAY_HERO_SUPPORT(new ArchetypeChallenge("Support", Archetype.SUPPORT)),
    PLAY_HERO_HEXBANE(new ArchetypeChallenge("Black Widow", Archetype.HEXBANE)),

    // *=* Uncommon *=* //

    OPEN_CRATE(
            new Challenge("Gambler", "Open {} crates.")
                    .setRarity(ChallengeRarity.UNCOMMON)
                    .setMax(3)
    ),

    FIRST_BLOOD(
            new Challenge("First Blood!", "Cause first blood in a game.")
                    .setRarity(ChallengeRarity.UNCOMMON)
                    .setMax(1)
    ),

    // *=* Rare *=* //

    COLLECT_RELIC(
            new Challenge("Archeologist", "Find a relic.") {
                @Override
                public boolean canGenerate(@Nonnull PlayerProfile profile) {
                    final PlayerDatabase database = profile.getDatabase();
                    final CollectibleEntry entry = database.collectibleEntry;

                    return entry.anyMissing();
                }
            }
                    .setRarity(ChallengeRarity.RARE)
                    .setMax(1)
    ),

    COMPLETE_PARKOUR(
            new Challenge("Parkour parkour!", "Complete any parkour.")
                    .setRarity(ChallengeRarity.RARE)
                    .setMax(1)
    ),


    ;

    private final Challenge challenge;

    ChallengeType(Challenge challenge) {
        this.challenge = challenge;
    }

    @Nonnull
    @Override
    public Challenge get() {
        return challenge;
    }

    /**
     * Increments the given profile challenges for this type.
     * <br>
     * <b>Plugin <i>must</i> use this method because it checks for daily resets.</b>
     *
     * @param profile - Profile.
     */
    public void progress(@Nonnull PlayerProfile profile) {
        final PlayerChallengeList challengeList = profile.getChallengeList();

        challengeList.validateSameDay();

        for (PlayerChallenge challenge : challengeList.getChallenges()) {
            // I don't think challenges can be null, but
            // just in case my code is ass because it is.
            if (challenge == null) {
                continue;
            }

            if (challenge.getType() == this) {
                final boolean isComplete = challenge.increment();

                if (!isComplete) {
                    continue;
                }

                final Player player = profile.getPlayer();

                Chat.sendMessage(player, "");
                Chat.sendCenterMessage(player, "&2&lxx &a&lDAILY BOND COMPLETE! &2&lxx");
                Chat.sendCenterMessage(player, "&7" + this.challenge.getName());
                Chat.sendMessage(player, "");
                Chat.sendCenterMessage(player, "&8&oTalk to The Eye to claim your rewards!");
                Chat.sendMessage(player, "");

                PlayerLib.playSound(player, Sound.UI_TOAST_CHALLENGE_COMPLETE, 2.0f);
            }
        }
    }

    public void progress(@Nonnull Player player) {
        progress(PlayerProfile.getProfileOrThrow(player));
    }

    public void progress(@Nonnull GamePlayer player) {
        progress(player.getProfile());
    }

    @Nonnull
    public static PlayerChallenge createPlayerChallenge(int bound, @Nonnull PlayerChallengeList challengeList) {
        final ChallengeType randomValue = Enums.getRandomValue(ChallengeType.class, KILL_ENEMIES);

        // Don't allow creating non-completable challenges
        if (!challengeList.canGenerate(randomValue)) {
            return createPlayerChallenge(bound - 1, challengeList);
        }

        if (bound > 0 && challengeList.hasOfType(randomValue)) {
            return createPlayerChallenge(bound - 1, challengeList);
        }

        return PlayerChallenge.of(randomValue);
    }

    public static void progressArchetypeBond(@Nonnull Player player, @Nonnull Archetype heroArchetype) {
        switch (heroArchetype) {
            case DAMAGE -> PLAY_HERO_DAMAGE.progress(player);
            case RANGE -> PLAY_HERO_RANGE.progress(player);
            case MOBILITY -> PLAY_HERO_MOBILITY.progress(player);
            case STRATEGY -> PLAY_HERO_STRATEGY.progress(player);
            case SUPPORT -> PLAY_HERO_SUPPORT.progress(player);
            case HEXBANE -> PLAY_HERO_HEXBANE.progress(player);
        }
    }


}
