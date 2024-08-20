package me.hapyl.fight.game.heroes;

import me.hapyl.eterna.module.util.CollectionUtils;
import me.hapyl.eterna.module.util.Compute;
import me.hapyl.fight.database.entry.HeroEntry;
import me.hapyl.fight.game.heroes.alchemist.Alchemist;
import me.hapyl.fight.game.heroes.archer.Archer;
import me.hapyl.fight.game.heroes.archer_tutorial.TutorialArcher;
import me.hapyl.fight.game.heroes.aurora.Aurora;
import me.hapyl.fight.game.heroes.bloodfield.Bloodfiend;
import me.hapyl.fight.game.heroes.bounty_hunter.BountyHunter;
import me.hapyl.fight.game.heroes.dark_mage.DarkMage;
import me.hapyl.fight.game.heroes.doctor.DrEd;
import me.hapyl.fight.game.heroes.echo.Echo;
import me.hapyl.fight.game.heroes.ender.Ender;
import me.hapyl.fight.game.heroes.engineer.Engineer;
import me.hapyl.fight.game.heroes.frostbite.Freazly;
import me.hapyl.fight.game.heroes.geo.Geo;
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
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.registry.AbstractStaticRegistry;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

import static me.hapyl.fight.database.key.DatabaseKey.ofEnum;

/**
 * {@inheritDoc}
 */
public final class HeroRegistry extends AbstractStaticRegistry<Hero> {

    public static final Archer ARCHER;
    public static final Alchemist ALCHEMIST;
    public static final Moonwalker MOONWALKER;
    public static final Hercules HERCULES;
    public static final Mage MAGE;
    public static final Pytaria PYTARIA;
    public static final Troll TROLL;
    public static final Nightmare NIGHTMARE;
    public static final DrEd DR_ED;
    public static final Ender ENDER;
    public static final Spark SPARK;
    public static final ShadowAssassin SHADOW_ASSASSIN;
    public static final WitcherClass WITCHER;
    public static final Vortex VORTEX;
    public static final Freazly FREAZLY;
    public static final DarkMage DARK_MAGE;
    public static final BlastKnight BLAST_KNIGHT;
    public static final Ninja NINJA;
    public static final Taker TAKER;
    public static final JuJu JUJU;
    public static final Swooper SWOOPER;
    public static final Tamer TAMER;
    public static final Shark SHARK;
    public static final Librarian LIBRARIAN;
    public static final Techie TECHIE;
    public static final KillingMachine WAR_MACHINE;
    public static final Harbinger HARBINGER;
    public static final Shaman SHAMAN;
    public static final Healer HEALER;
    public static final Vampire VAMPIRE;
    public static final BountyHunter BOUNTY_HUNTER;
    public static final SwordMaster SWORD_MASTER;
    public static final Engineer ENGINEER;
    public static final Orc ORC;
    public static final Bloodfiend BLOODFIEND;
    public static final Zealot ZEALOT;
    public static final Ronin RONIN;
    public static final Jester JESTER;
    public static final Rogue ROGUE;
    public static final Geo GEO;
    public static final Aurora AURORA;
    public static final Nyx NYX;
    public static final Gunner GUNNER;
    public static final Echo ECHO;

    // *=* Tutorial Hero *=* // Please keep last
    public static final TutorialArcher TUTORIAL_ARCHER;

    private static final Set<Hero> values;
    private static final List<Hero> playable;

    private static final Map<Archetype, List<Hero>> byArchetype;

    private static GlobalHeroStats globalStats;

    static {
        AbstractStaticRegistry.ensure(HeroRegistry.class, Hero.class);

        values = new LinkedHashSet<>();
        playable = new ArrayList<>();
        byArchetype = new HashMap<>();

        /*/ ⬇️ Register below ⬇️ /*/

        ARCHER = register(new Archer(ofEnum("ARCHER")));
        ALCHEMIST = register(new Alchemist(ofEnum("ALCHEMIST")));
        MOONWALKER = register(new Moonwalker(ofEnum("MOONWALKER")));
        HERCULES = register(new Hercules(ofEnum("HERCULES")));
        MAGE = register(new Mage(ofEnum("MAGE")));
        PYTARIA = register(new Pytaria(ofEnum("PYTARIA")));
        TROLL = register(new Troll(ofEnum("TROLL")));
        NIGHTMARE = register(new Nightmare(ofEnum("NIGHTMARE")));
        DR_ED = register(new DrEd(ofEnum("DR_ED")));
        ENDER = register(new Ender(ofEnum("ENDER")));
        SPARK = register(new Spark(ofEnum("SPARK")));
        SHADOW_ASSASSIN = register(new ShadowAssassin(ofEnum("SHADOW_ASSASSIN")));
        WITCHER = register(new WitcherClass(ofEnum("WITCHER")));
        VORTEX = register(new Vortex(ofEnum("VORTEX")));
        FREAZLY = register(new Freazly(ofEnum("FREAZLY")));
        DARK_MAGE = register(new DarkMage(ofEnum("DARK_MAGE")));
        BLAST_KNIGHT = register(new BlastKnight(ofEnum("BLAST_KNIGHT")));
        NINJA = register(new Ninja(ofEnum("NINJA")));
        TAKER = register(new Taker(ofEnum("TAKER")));
        JUJU = register(new JuJu(ofEnum("JUJU")));
        SWOOPER = register(new Swooper(ofEnum("SWOOPER")));
        TAMER = register(new Tamer(ofEnum("TAMER")));
        SHARK = register(new Shark(ofEnum("SHARK")));
        LIBRARIAN = register(new Librarian(ofEnum("LIBRARIAN")));
        TECHIE = register(new Techie(ofEnum("TECHIE")));
        WAR_MACHINE = register(new KillingMachine(ofEnum("WAR_MACHINE")));
        HARBINGER = register(new Harbinger(ofEnum("HARBINGER")));
        SHAMAN = register(new Shaman(ofEnum("SHAMAN")));
        HEALER = register(new Healer(ofEnum("HEALER")));
        VAMPIRE = register(new Vampire(ofEnum("VAMPIRE")));
        BOUNTY_HUNTER = register(new BountyHunter(ofEnum("BOUNTY_HUNTER")));
        SWORD_MASTER = register(new SwordMaster(ofEnum("SWORD_MASTER")));
        ENGINEER = register(new Engineer(ofEnum("ENGINEER")));
        ORC = register(new Orc(ofEnum("ORC")));
        BLOODFIEND = register(new Bloodfiend(ofEnum("BLOODFIEND")));
        ZEALOT = register(new Zealot(ofEnum("ZEALOT")));
        RONIN = register(new Ronin(ofEnum("RONIN")));
        JESTER = register(new Jester(ofEnum("JESTER")));
        ROGUE = register(new Rogue(ofEnum("ROGUE")));
        GEO = register(new Geo(ofEnum("GEO")));
        AURORA = register(new Aurora(ofEnum("AURORA")));
        NYX = register(new Nyx(ofEnum("NYX")));
        GUNNER = register(new Gunner(ofEnum("GUNNER")));
        ECHO = register(new Echo(ofEnum("ECHO")));

        // Keep last
        TUTORIAL_ARCHER = register(new TutorialArcher(ofEnum("TUTORIAL_ARCHER")));

        calculateGlobalStats();
    }

    @Nonnull
    public static GlobalHeroStats getGlobalStats() {
        return Objects.requireNonNull(globalStats, "Global stats hasn't yet been instantiated!");
    }

    public static void calculateGlobalStats() {
        if (globalStats != null) {
            globalStats.clear();
        }

        globalStats = new GlobalHeroStats();
    }

    @Nonnull
    public static Hero ofString(@Nonnull String string) {
        return AbstractStaticRegistry.ofString(values, string, defaultHero());
    }

    @Nullable
    public static Hero ofStringOrNull(@Nonnull String string) {
        return AbstractStaticRegistry.ofStringOrNull(values, string);
    }

    @Nonnull
    public static Hero defaultHero() {
        return ARCHER;
    }

    @Nonnull
    public static List<Hero> values() {
        return new ArrayList<>(values);
    }

    @Nonnull
    public static List<Hero> playable() {
        return new ArrayList<>(playable);
    }

    @Nonnull
    public static List<Hero> playableRespectFavourites(@Nonnull Player player) {
        final PlayerProfile profile = PlayerProfile.getProfile(player);
        final List<Hero> playable = playable();

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
    public static List<Hero> playableRespectLockedFavourites(@Nonnull Player player) {
        final List<Hero> heroes = playableRespectFavourites(player);
        heroes.sort(Comparator.comparingInt(a -> (a.isLocked(player) ? 1 : 0)));

        return heroes;
    }

    @Nonnull
    public static Hero randomHero(@Nonnull Player player) {
        final List<Hero> playable = playable();
        playable.removeIf(hero -> hero.isLocked(player));

        return CollectionUtils.randomElement(playable, defaultHero());
    }

    @Nonnull
    public static Hero randomHero() {
        return CollectionUtils.randomElement(playable, defaultHero());
    }

    @Nonnull
    public static Set<Hero> byArchetype(@Nonnull Archetype archetype) {
        return setFromByMap(byArchetype, archetype);
    }

    @Nonnull
    public static List<String> keys() {
        return AbstractStaticRegistry.keys(values);
    }

    private static <K> Set<Hero> setFromByMap(Map<K, List<Hero>> map, K key) {
        final List<Hero> list = map.get(key);

        return list != null ? new HashSet<>(list) : new HashSet<>();
    }

    private static <T extends Hero> T register(@Nonnull T hero) {
        values.add(hero);

        if (hero.isValidHero()) {
            playable.add(hero);
        }

        hero.getArchetypes().forEach(archetype -> {
            byArchetype.compute(archetype, Compute.listAdd(hero));
        });

        return hero;
    }


}
