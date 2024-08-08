package me.hapyl.fight.game.heroes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.hapyl.fight.CF;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.collection.HeroStatsCollection;
import me.hapyl.fight.database.entry.ExperienceEntry;
import me.hapyl.fight.database.entry.HeroEntry;
import me.hapyl.fight.exception.HandleNotSetException;
import me.hapyl.fight.game.Disabled;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.alchemist.Alchemist;
import me.hapyl.fight.game.heroes.archer.Archer;
import me.hapyl.fight.game.heroes.archer_tutorial.TutorialArcher;
import me.hapyl.fight.game.heroes.bloodfield.Bloodfiend;
import me.hapyl.fight.game.heroes.bounty_hunter.BountyHunter;
import me.hapyl.fight.game.heroes.dark_mage.DarkMage;
import me.hapyl.fight.game.heroes.doctor.DrEd;
import me.hapyl.fight.game.heroes.echo.Echo;
import me.hapyl.fight.game.heroes.ender.Ender;
import me.hapyl.fight.game.heroes.engineer.Engineer;
import me.hapyl.fight.game.heroes.frostbite.Freazly;
import me.hapyl.fight.game.heroes.gunner.Gunner;
import me.hapyl.fight.game.heroes.harbinger.Harbinger;
import me.hapyl.fight.game.heroes.healer.Healer;
import me.hapyl.fight.game.heroes.heavy_knight.SwordMaster;
import me.hapyl.fight.game.heroes.hercules.Hercules;
import me.hapyl.fight.game.heroes.jester.Jester;
import me.hapyl.fight.game.heroes.juju.JuJu;
import me.hapyl.fight.game.heroes.km.KillingMachine;
import me.hapyl.fight.game.heroes.knight.BlastKnight;
import me.hapyl.fight.game.heroes.librarian.Librarian;
import me.hapyl.fight.game.heroes.mage.Mage;
import me.hapyl.fight.game.heroes.moonwalker.Moonwalker;
import me.hapyl.fight.game.heroes.nightmare.Nightmare;
import me.hapyl.fight.game.heroes.ninja.Ninja;
import me.hapyl.fight.game.heroes.nyx.Nyx;
import me.hapyl.fight.game.heroes.orc.Orc;
import me.hapyl.fight.game.heroes.pytaria.Pytaria;
import me.hapyl.fight.game.heroes.rogue.Rogue;
import me.hapyl.fight.game.heroes.ronin.Ronin;
import me.hapyl.fight.game.heroes.shadow_assassin.ShadowAssassin;
import me.hapyl.fight.game.heroes.shaman.Shaman;
import me.hapyl.fight.game.heroes.shark.Shark;
import me.hapyl.fight.game.heroes.spark.Spark;
import me.hapyl.fight.game.heroes.swooper.Swooper;
import me.hapyl.fight.game.heroes.taker.Taker;
import me.hapyl.fight.game.heroes.tamer.Tamer;
import me.hapyl.fight.game.heroes.techie.Techie;
import me.hapyl.fight.game.heroes.troll.Troll;
import me.hapyl.fight.game.heroes.vampire.Vampire;
import me.hapyl.fight.game.heroes.vortex.Vortex;
import me.hapyl.fight.game.heroes.witcher.WitcherClass;
import me.hapyl.fight.game.heroes.zealot.Zealot;
import me.hapyl.fight.game.heroes.aurora.Aurora;
import me.hapyl.fight.game.heroes.geo.Geo;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.util.Formatted;
import me.hapyl.eterna.module.util.CollectionUtils;
import me.hapyl.eterna.module.util.Compute;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.*;
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
    @OnReworkIgnoreForNow MOONWALKER(Moonwalker::new),
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
    @OnReworkIgnoreForNow LIBRARIAN(Librarian::new),
    TECHIE(Techie::new),
    @OnReworkIgnoreForNow WAR_MACHINE(KillingMachine::new),
    HARBINGER(Harbinger::new),
    SHAMAN(Shaman::new),
    HEALER(Healer::new),
    @OnReworkIgnoreForNow VAMPIRE(Vampire::new),
    BOUNTY_HUNTER(BountyHunter::new),
    SWORD_MASTER(SwordMaster::new),
    ENGINEER(Engineer::new),
    ORC(Orc::new),
    BLOODFIEND(Bloodfiend::new),
    ZEALOT(Zealot::new),
    @OnReworkIgnoreForNow RONIN(Ronin::new),
    @OnReworkIgnoreForNow JESTER(Jester::new),
    ROGUE(Rogue::new),

    GEO(Geo::new),
    AURORA(Aurora::new),
    NYX(Nyx::new),

    GUNNER(Gunner::new),
    ECHO(Echo::new),

    // *=* Tutorial Hero *=* //
    TUTORIAL_ARCHER(TutorialArcher::new),

    ;

    public static final Heroes DEFAULT_HERO = ARCHER;

    private final static List<Heroes> PLAYABLE = Lists.newArrayList();

    private final static Map<Archetype, Set<Heroes>> BY_ARCHETYPE = Maps.newHashMap();
    private final static Map<Gender, Set<Heroes>> BY_GENDER = Maps.newHashMap();
    private final static Map<Race, Set<Heroes>> BY_RACE = Maps.newHashMap();

    private static GlobalHeroStats globalStats;

    static {
        for (Heroes enumHero : values()) {
            if (!enumHero.isValidHero()) {
                continue;
            }

            // Store archetype for easier grab
            mapHero(enumHero, Hero::getGender, BY_GENDER);
            mapHero(enumHero, Hero::getRace, BY_RACE);

            // Map archetype because it's special
            final ArchetypeList archetypes = enumHero.getHero().getArchetypes();
            archetypes.forEach(archetype -> {
                mapHero(enumHero, __ -> archetype, BY_ARCHETYPE);
            });

            // Add playable
            PLAYABLE.add(enumHero);
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
        return !(hero instanceof Disabled);
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
        return profile != null && profile.getDatabase().heroEntry.isFavourite(this);
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

        profile.getDatabase().heroEntry.setFavourite(this, flag);
    }

    /**
     * Returns true if this hero is locked for player.
     *
     * @param player - Player to check.
     * @return true if this hero is locked for player.
     */
    public boolean isLocked(Player player) {
        final PlayerDatabase database = PlayerDatabase.getDatabase(player);

        final boolean purchased = database.heroEntry.isPurchased(this);
        final boolean hasLevel = database.experienceEntry.get(ExperienceEntry.Type.LEVEL) >= hero.getMinimumLevel();

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
     * @see me.hapyl.eterna.module.util.SmallCaps
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
        return hero.getArchetypes().getFirst().getPrefix();
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
            final HeroEntry heroEntry = profile.getDatabase().heroEntry;
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
    public static Set<Heroes> byArchetype(@Nonnull Archetype archetype) {
        return computeSetCopy(BY_ARCHETYPE, archetype);
    }

    @Nonnull
    public static Set<Heroes> byGender(@Nonnull Gender gender) {
        return computeSetCopy(BY_GENDER, gender);
    }

    public static Set<Heroes> byRace(@Nonnull Race race) {
        return computeSetCopy(BY_RACE, race);
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

    private static <T> void mapHero(Heroes hero, Function<Hero, T> fn, Map<T, Set<Heroes>> map) {
        map.compute(fn.apply(hero.getHero()), Compute.setAdd(hero));
    }

    private static <T> Set<Heroes> computeSetCopy(Map<T, Set<Heroes>> map, T t) {
        return Sets.newHashSet(map.computeIfAbsent(t, fn -> Sets.newHashSet()));
    }

}
