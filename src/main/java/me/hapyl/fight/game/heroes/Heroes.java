package me.hapyl.fight.game.heroes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.hapyl.fight.CF;
import me.hapyl.fight.Main;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.collection.HeroStatsCollection;
import me.hapyl.fight.database.entry.ExperienceEntry;
import me.hapyl.fight.database.entry.HeroEntry;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.archive.alchemist.Alchemist;
import me.hapyl.fight.game.heroes.archive.archer.Archer;
import me.hapyl.fight.game.heroes.archive.bloodfield.Bloodfiend;
import me.hapyl.fight.game.heroes.archive.bounty_hunter.BountyHunter;
import me.hapyl.fight.game.heroes.archive.dark_mage.DarkMage;
import me.hapyl.fight.game.heroes.archive.doctor.DrEd;
import me.hapyl.fight.game.heroes.archive.ender.Ender;
import me.hapyl.fight.game.heroes.archive.engineer.Engineer;
import me.hapyl.fight.game.heroes.archive.harbinger.Harbinger;
import me.hapyl.fight.game.heroes.archive.healer.Healer;
import me.hapyl.fight.game.heroes.archive.heavy_knight.SwordMaster;
import me.hapyl.fight.game.heroes.archive.hercules.Hercules;
import me.hapyl.fight.game.heroes.archive.iceologer.Freazly;
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
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Registry for Heroes.
 * <p>
 * Make sure not to change names, as it will break the database.
 */
public enum Heroes implements Formatted {

    // New Hero -> Halloween

    ARCHER(new Archer()),
    ALCHEMIST(new Alchemist()),
    MOONWALKER(new Moonwalker()),
    HERCULES(new Hercules()),
    MAGE(new Mage()),
    PYTARIA(new Pytaria()),
    TROLL(new Troll()),
    NIGHTMARE(new Nightmare()),
    DR_ED(new DrEd()),
    ENDER(new Ender()),
    SPARK(new Spark()),
    SHADOW_ASSASSIN(new ShadowAssassin()),
    WITCHER(new WitcherClass()),
    VORTEX(new Vortex()),
    FREAZLY(new Freazly()),
    DARK_MAGE(new DarkMage()),
    BLAST_KNIGHT(new BlastKnight()),
    NINJA(new Ninja()),
    TAKER(new Taker()),
    JUJU(new JuJu()),
    SWOOPER(new Swooper()),
    TAMER(new Tamer()),
    SHARK(new Shark()),
    LIBRARIAN(new Librarian()),
    TECHIE(new Techie()),
    WAR_MACHINE(new KillingMachine()),
    HARBINGER(new Harbinger()),
    SHAMAN(new Shaman()),

    // 1.5
    HEALER(new Healer()),
    VAMPIRE(new Vampire()),
    BOUNTY_HUNTER(new BountyHunter()),
    SWORD_MASTER(new SwordMaster()),
    ENGINEER(new Engineer()),

    // 1.6,
    ORC(new Orc()),

    // 2.0
    BLOODFIEND(new Bloodfiend()),
    ZEALOT(new Zealot()),
    RONIN(new Ronin()),

    ;

    public static final Heroes DEFAULT_HERO = ARCHER;

    private final static List<Heroes> PLAYABLE = Lists.newArrayList();
    private final static Map<Archetype, List<Heroes>> BY_ARCHETYPE = Maps.newHashMap();
    private final static Map<Hero, Heroes> BY_HANDLE = Maps.newHashMap();

    static {
        for (Heroes hero : values()) {
            // save handles either way
            if (hero.hero != null) {
                BY_HANDLE.put(hero.hero, hero);
            }

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
    }

    private final Hero hero;
    private final HeroStatsCollection stats; // can't store in a hero object because requires enum

    Heroes(Hero hero) {
        this.hero = hero;
        this.stats = new HeroStatsCollection(this);

        if (hero instanceof Listener listener) {
            Bukkit.getPluginManager().registerEvents(listener, Main.getPlugin());
        }
    }

    public HeroStatsCollection getStats() {
        return stats;
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
    public Hero getHero() {
        if (hero == null) {
            throw new NullPointerException("%s doesn't have a handle!".formatted(name()));
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
        return PlayerProfile.getOrCreateProfile(player).getDatabase().getHeroEntry().isFavourite(this);
    }

    /**
     * Sets if player has this hero favourite.
     *
     * @param player - Player to set.
     * @param flag   - True if favourite, false if not.
     */
    public void setFavourite(Player player, boolean flag) {
        PlayerProfile.getOrCreateProfile(player).getDatabase().getHeroEntry().setFavourite(this, flag);
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
        return hero.getArchetype().getPrefix() + " &f" + hero.getNameSmallCaps();
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
    public static List<Heroes> playable() {
        return Lists.newArrayList(PLAYABLE);
    }

    /**
     * Returns all playable heroes sorted by favourites.
     *
     * @param player - Player to sort by favourites.
     * @return all playable heroes sorted by favourites.
     */
    public static List<Heroes> playableRespectFavourites(Player player) {
        final List<Heroes> playable = playable();
        playable.sort((a, b) -> {
            final HeroEntry heroEntry = PlayerProfile.getOrCreateProfile(player).getDatabase().getHeroEntry();
            return (heroEntry.isFavourite(b) ? 1 : 0) - (heroEntry.isFavourite(a) ? 1 : 0);
        });
        return playable;
    }

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
    public static Heroes byHandle(Hero hero) {
        return BY_HANDLE.getOrDefault(hero, DEFAULT_HERO);
    }

}
