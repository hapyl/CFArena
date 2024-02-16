package me.hapyl.fight.game.heroes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.hapyl.fight.CF;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.collection.HeroStatsCollection;
import me.hapyl.fight.database.entry.ExperienceEntry;
import me.hapyl.fight.database.entry.HeroEntry;
import me.hapyl.fight.exception.HandleNotSetException;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.archive.alchemist.Alchemist;
import me.hapyl.fight.game.heroes.archive.archer.Archer;
import me.hapyl.fight.game.heroes.archive.archer_tutorial.TutorialArcher;
import me.hapyl.fight.game.heroes.archive.bloodfield.Bloodfiend;
import me.hapyl.fight.game.heroes.archive.bounty_hunter.BountyHunter;
import me.hapyl.fight.game.heroes.archive.dark_mage.DarkMage;
import me.hapyl.fight.game.heroes.archive.doctor.DrEd;
import me.hapyl.fight.game.heroes.archive.ender.Ender;
import me.hapyl.fight.game.heroes.archive.engineer.Engineer;
import me.hapyl.fight.game.heroes.archive.frostbite.Freazly;
import me.hapyl.fight.game.heroes.archive.harbinger.Harbinger;
import me.hapyl.fight.game.heroes.archive.healer.Healer;
import me.hapyl.fight.game.heroes.archive.heavy_knight.SwordMaster;
import me.hapyl.fight.game.heroes.archive.hercules.Hercules;
import me.hapyl.fight.game.heroes.archive.jester.Jester;
import me.hapyl.fight.game.heroes.archive.juju.JuJu;
import me.hapyl.fight.game.heroes.archive.km.KillingMachine;
import me.hapyl.fight.game.heroes.archive.knight.BlastKnight;
import me.hapyl.fight.game.heroes.archive.librarian.Librarian;
import me.hapyl.fight.game.heroes.archive.mage.Mage;
import me.hapyl.fight.game.heroes.archive.moonwalker.Moonwalker;
import me.hapyl.fight.game.heroes.archive.nightmare.Nightmare;
import me.hapyl.fight.game.heroes.archive.ninja.Ninja;
import me.hapyl.fight.game.heroes.archive.orc.Orc;
import me.hapyl.fight.game.heroes.archive.pytaria.Pytaria;
import me.hapyl.fight.game.heroes.archive.rogue.Rogue;
import me.hapyl.fight.game.heroes.archive.ronin.Ronin;
import me.hapyl.fight.game.heroes.archive.shadow_assassin.ShadowAssassin;
import me.hapyl.fight.game.heroes.archive.shaman.Shaman;
import me.hapyl.fight.game.heroes.archive.shark.Shark;
import me.hapyl.fight.game.heroes.archive.spark.Spark;
import me.hapyl.fight.game.heroes.archive.swooper.Swooper;
import me.hapyl.fight.game.heroes.archive.taker.Taker;
import me.hapyl.fight.game.heroes.archive.tamer.Tamer;
import me.hapyl.fight.game.heroes.archive.techie.Techie;
import me.hapyl.fight.game.heroes.archive.troll.Troll;
import me.hapyl.fight.game.heroes.archive.vampire.Vampire;
import me.hapyl.fight.game.heroes.archive.vortex.Vortex;
import me.hapyl.fight.game.heroes.archive.witcher.WitcherClass;
import me.hapyl.fight.game.heroes.archive.zealot.Zealot;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.util.Formatted;
import me.hapyl.fight.util.SmallCaps;
import me.hapyl.spigotutils.module.util.CollectionUtils;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Registry for Heroes.
 * <p>
 * Make sure not to change names, as it will break the database.
 */
public enum Heroes implements Formatted {

    // New Hero -> Halloween

    ARCHER(Archer::new),
    ALCHEMIST(Alchemist::new),
    MOONWALKER(Moonwalker::new),
    @OnReworkIgnoreForNow HERCULES(Hercules::new),
    MAGE(Mage::new),
    PYTARIA(Pytaria::new),
    TROLL(Troll::new),
    NIGHTMARE(Nightmare::new),
    DR_ED(DrEd::new),
    ENDER(Ender::new),
    SPARK(Spark::new),
    SHADOW_ASSASSIN(ShadowAssassin::new),
    WITCHER(WitcherClass::new),
    VORTEX(Vortex::new),
    FREAZLY(Freazly::new),
    DARK_MAGE(DarkMage::new),
    BLAST_KNIGHT(BlastKnight::new),
    NINJA(Ninja::new),
    TAKER(Taker::new),
    JUJU(JuJu::new),
    SWOOPER(Swooper::new),
    TAMER(Tamer::new),
    SHARK(Shark::new),
    LIBRARIAN(Librarian::new),
    TECHIE(Techie::new),
    WAR_MACHINE(KillingMachine::new),
    HARBINGER(Harbinger::new),
    SHAMAN(Shaman::new),
    HEALER(Healer::new),
    VAMPIRE(Vampire::new),
    BOUNTY_HUNTER(BountyHunter::new),
    SWORD_MASTER(SwordMaster::new),
    ENGINEER(Engineer::new),
    ORC(Orc::new),
    BLOODFIEND(Bloodfiend::new),
    ZEALOT(Zealot::new),
    RONIN(Ronin::new),
    JESTER(Jester::new),
    ROGUE(Rogue::new),

    // *=* Tutorial Hero *=* //
    TUTORIAL_ARCHER(TutorialArcher::new),

    ;

    public static final Heroes DEFAULT_HERO = ARCHER;

    private final static List<Heroes> PLAYABLE = Lists.newArrayList();
    private final static Map<Archetype, List<Heroes>> BY_ARCHETYPE = Maps.newHashMap();
    private static GlobalHeroStats globalStats;

    static {
        for (Heroes hero : values()) {
            if (!hero.isValidHero()) {
                continue;
            }

            // Store archetype for easier grab
            final Archetype archetype = hero.hero.getArchetype();
            final List<Heroes> byArchetype = BY_ARCHETYPE.computeIfAbsent(archetype, l -> Lists.newArrayList());

            byArchetype.add(hero);

            // Add playable
            PLAYABLE.add(hero);
        }

        // Global Stats Calculations
        calculateGlobalStats();
    }

    private final Hero hero;

    Heroes(@Nonnull Function<Heroes, Hero> fn) {
        this.hero = fn.apply(this);
    }

    @Nonnull
    public HeroStatsCollection getStats() {
        return hero.getStats();
    }

    public boolean isValidHero() {
        return !(hero instanceof DisabledHero);
    }

    /**
     * Returns a handle of a hero.
     * <p>
     * Note that this method only returns a base handle,
     * for specific hero handles, use {@link #getHero(Class)}.
     *
     * @return handle of a hero.
     */
    @Nonnull
    public Hero getHero() {
        if (hero == null) {
            throw new HandleNotSetException(this);
        }

        return hero;
    }

    /**
     * Returns a handle of a hero.
     * <p>
     * This method tries to cast the handle to the specified class.
     *
     * @param cast - Cast to.
     * @return handle of a hero.
     * @throws IllegalArgumentException if the cast is invalid.
     */
    @Nonnull
    public <E extends Hero> E getHero(Class<E> cast) throws IllegalArgumentException {
        if (cast.isInstance(hero)) {
            return cast.cast(hero);
        }

        throw new IllegalArgumentException("Invalid cast! Expected %s, got %s.".formatted(
                cast.getSimpleName(),
                hero.getClass().getSimpleName()
        ));
    }

    /**
     * Returns list of players that have this hero selected.
     * Or empty list if no players have this hero selected.
     *
     * @return list of players that have this hero selected.
     */
    @Nonnull
    public List<GamePlayer> getPlayers() {
        final Set<GamePlayer> players = CF.getPlayers();
        players.removeIf(player -> !isSelected(player));

        return Lists.newArrayList(players);
    }

    /**
     * Returns list of living payers that have this hero selected.
     *
     * @return list of alive players that have this hero selected.
     */
    @Nonnull
    public List<GamePlayer> getAlivePlayers() {
        final List<GamePlayer> players = getPlayers();
        players.removeIf(filter -> !filter.isAlive());

        return players;
    }

    /**
     * Returns true if player's current hero is this hero.
     *
     * @param player - Player to check.
     * @return true if player's current hero is this hero.
     */
    public boolean isSelected(GamePlayer player) {
        return Manager.current().getCurrentEnumHero(player) == this;
    }

    /**
     * Returns true if player has this hero favourite.
     *
     * @param player - Player to check.
     * @return true if player has this hero favourite.
     */
    public boolean isFavourite(Player player) {
        final PlayerProfile profile = PlayerProfile.getProfile(player);
        return profile != null && profile.getDatabase().getHeroEntry().isFavourite(this);
    }

    /**
     * Sets if player has this hero favourite.
     *
     * @param player - Player to set.
     * @param flag   - True if favourite, false if not.
     */
    public void setFavourite(Player player, boolean flag) {
        final PlayerProfile profile = PlayerProfile.getProfile(player);
        if (profile == null) {
            return;
        }

        profile.getDatabase().getHeroEntry().setFavourite(this, flag);
    }

    /**
     * Returns true if this hero is locked for player.
     *
     * @param player - Player to check.
     * @return true if this hero is locked for player.
     */
    public boolean isLocked(Player player) {
        final PlayerDatabase database = PlayerDatabase.getDatabase(player);

        final boolean purchased = database.getHeroEntry().isPurchased(this);
        final boolean hasLevel = database.getExperienceEntry().get(ExperienceEntry.Type.LEVEL) >= hero.getMinimumLevel();

        return !purchased && !hasLevel;
    }

    /**
     * Returns actual name of the hero, not enum.
     *
     * @return actual name of the hero, not enum.
     */
    @Nonnull
    public String getName() {
        return hero.getName();
    }

    /**
     * Returns the name of the hero in small caps.
     *
     * @return name of the hero in small caps.
     * @see SmallCaps
     */
    public String getNameSmallCaps() {
        return hero.getNameSmallCaps();
    }

    @Nonnull
    @Override
    public String getFormatted() {
        return getFormatted(Color.WHITE);
    }

    @Nonnull
    public String getFormatted(@Nonnull Color color) {
        return getPrefix() + color + " " + hero.getNameSmallCaps();
    }

    @Nonnull
    public String getPrefix() {
        return hero.getArchetype().getPrefix();
    }

    @Nonnull
    public static GlobalHeroStats getGlobalStats() {
        return globalStats;
    }

    public static void calculateGlobalStats() {
        if (globalStats != null) {
            globalStats.clear();
        }

        globalStats = new GlobalHeroStats();
    }

    /**
     * Returns all playable heroes.
     *
     * <p>
     * Note that <b>admins</b> can still select non-playable hero using <i>'-IKnowItsDisabledHeroAndWillBreakTheGame'</i> argument.
     * </p>
     *
     * @return all playable heroes.
     */
    @Nonnull
    public static List<Heroes> playable() {
        return Lists.newArrayList(PLAYABLE);
    }

    /**
     * Returns all playable heroes sorted by favourites.
     *
     * @param player - Player to sort by favourites.
     * @return all playable heroes sorted by favourites.
     */
    @Nonnull
    public static List<Heroes> playableRespectFavourites(Player player) {
        final PlayerProfile profile = PlayerProfile.getProfile(player);
        final List<Heroes> playable = playable();

        if (profile == null) {
            return playable;
        }

        playable.sort((a, b) -> {
            final HeroEntry heroEntry = profile.getDatabase().getHeroEntry();
            return (heroEntry.isFavourite(b) ? 1 : 0) - (heroEntry.isFavourite(a) ? 1 : 0);
        });
        return playable;
    }

    @Nonnull
    public static List<Heroes> playableRespectLockedFavourites(Player player) {
        final List<Heroes> heroes = playableRespectFavourites(player);
        heroes.sort(Comparator.comparingInt(a -> (a.isLocked(player) ? 1 : 0)));

        return heroes;
    }

    /**
     * Returns playable heroes names by their Enum name.
     *
     * @return playable heroes names by their Enum name.
     */
    @Nonnull
    public static List<String> playableStings() {
        return playable().stream().map(Enum::name).toList();
    }

    /**
     * Gets a copy of heroes with a given archetype.
     *
     * @param archetype - Archetype.
     * @return a copy of heroes with a given archetype.
     */
    @Nonnull
    public static List<Heroes> byArchetype(@Nonnull Archetype archetype) {
        return Lists.newArrayList(BY_ARCHETYPE.computeIfAbsent(archetype, (s) -> Lists.newArrayList()));
    }

    /**
     * Returns a random playable hero.
     *
     * @return random playable hero.
     */
    @Nonnull
    public static Heroes randomHero() {
        return CollectionUtils.randomElement(playable(), DEFAULT_HERO);
    }

    @Nonnull
    public static Heroes randomHero(@Nonnull Player player) {
        final List<Heroes> playable = playable();
        playable.removeIf(hero -> hero.isLocked(player));

        return CollectionUtils.randomElement(playable, DEFAULT_HERO);
    }

}
